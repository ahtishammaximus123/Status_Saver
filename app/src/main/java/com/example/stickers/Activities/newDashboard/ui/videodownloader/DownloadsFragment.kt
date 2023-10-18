package com.example.stickers.Activities.newDashboard.ui.videodownloader

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stickers.Activities.HowToUseActivity

import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.playerActivityBack
import com.example.stickers.Activities.newDashboard.ui.videodownloader.FileUtility.Companion.getDuration
import com.example.stickers.Activities.newDashboard.ui.videodownloader.FileUtility.Companion.isPackageInstalled
import com.example.stickers.R
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.databinding.AlertDialogBinding
import com.example.stickers.databinding.DeleteDialogBinding
import com.example.stickers.databinding.FragmentDownloadsBinding
import com.example.stickers.dialog.ProgressDialog
import com.example.stickers.dialog.ShareFragment
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2.fetch.FetchModulesBuilder
import com.tonyodev.fetch2.util.toDownloadInfo
import com.tonyodev.fetch2core.getUniqueId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadsFragment : Fragment(),OnItemClick<Download> {
private lateinit var binding:FragmentDownloadsBinding
    private val viewModel: DownloadedFilesViewModel by viewModels()
    var loadingDialog: ProgressDialog? = null
    var adapter: DownloadsAdapter? = null
    var downloads: ArrayList<Download?>? = ArrayList()
    var reverseList: List<Download?>? = ArrayList()
    private var adisready = "notshowed"
    var isActivityRunning = false
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DownloadService.vidCount = 0
        binding=FragmentDownloadsBinding.inflate(layoutInflater,container,false)

        DownloadService.downloadsList?.forEach { download ->
            val index = reverseList?.indexOfFirst { d -> d?.dmId?.toInt() == download.id }
            if (download != null) {
                val file = try {
                    download.fileUri.toFile()
                } catch (ex: Exception) {
                    null
                }
                val duration = try {
                    file?.getDuration(requireActivity())
                } catch (ex: Exception) {
                    null
                }
                if (file != null) {
                    if (index != null && index > -1) {
                        val d = reverseList?.get(index)
                        d?.isDownloading = false
                        d?.name = download.fileUri.lastPathSegment
                        d?.path = download.fileUri.toString()
                        d?.size = file.length()
                        d?.duration = duration
                        d?.dmId = download.id.toLong()
                        adapter?.notifyItemChanged(index)
                    } else {
                        val d = convert(download)
                        downloads?.add(d)
                        reverseList = downloads?.asReversed()
                        adapter?.submitList(reverseList)

                    }
                }
            }

        }

        DownloadService.updated.observe(requireActivity(), Observer { download ->
            if (download != null) {
                update(download)
            }
        })
        DownloadService.canceled.observe(requireActivity(), Observer { download ->
            if (download != null) {
                if (reverseList != null)
                    downloads = ArrayList(reverseList!!.asReversed())
                val index = downloads?.indexOfFirst { d -> d?.path == download?.fileUri.toString() }

                if (index != null && index > -1) {
                    downloads?.removeAt(index)
                    reverseList = downloads?.asReversed()
                    adapter?.submitList(reverseList)
                }

            }
            adapter?.notifyDataSetChanged()
        })

        DownloadService.completed.observe(requireActivity(), Observer { download ->
            val file = try {
                download?.fileUri?.toFile()
            } catch (ex: Exception) {
                null
            }
            if (file != null && download != null) {
                completed(download)
            }
        })
        adapter = DownloadsAdapter(this)
        binding.rvDownloads.adapter = adapter
        val lytL = LinearLayoutManager(requireActivity())
        binding.rvDownloads.layoutManager = lytL
        adapter?.submitList(reverseList)
        return binding.root
    }

    fun convert(download: com.tonyodev.fetch2.Download): Download {
        val file = download.fileUri.toFile()
        val duration = try {
            file.getDuration(requireActivity())
        } catch (ex: Exception) {
            null
        }
        val d =Download(
            download.fileUri.lastPathSegment,
            download.fileUri.toString(),
            file.length(),
            duration,
            download.id.toLong(),
            false,
            100,
            download.id.toLong(),
            download.tag,
            download.total,
            false
        )
        return d
    }

    fun update(download: com.tonyodev.fetch2.Download?) {
        val index = reverseList?.indexOfFirst { d -> d?.path == download?.fileUri.toString() }
        var p = download?.progress ?: 1
        if (p < -1) p = 1
        if (download != null) {
            if (index != null && index > -1) {
                if (p < 100) {
                    val d = reverseList?.get(index)
                    d?.isDownloading = true
                    d?.progress = p
                    d?.totalbytes = download.total
                    //d?.name = download.fileUri.lastPathSegment
                    //d?.path = download.fileUri.toString()
                    //d?.dmId = download.id.toLong()

                    if (reverseList != null)
                        downloads = ArrayList(reverseList!!.asReversed())

                    adapter?.notifyItemChanged(index)
                }
            } else {
                val d = Download(
                    download.fileUri.lastPathSegment,
                    download.fileUri.toString(),
                    0,
                    0,
                    download.id.toLong(),
                    true,
                    p,
                    download.id.toLong(),
                    download.url,
                    download.total,
                    false
                )
                downloads?.add(d)
                reverseList = downloads?.asReversed()
                adapter?.submitList(reverseList)
            }
        }


        adapter?.notifyDataSetChanged()
//        val lastIndex = reverseList?.count()?: 0
//        val secondLast = lastIndex - 2
//        if(secondLast > 0) {
//            val d = reverseList?.get(secondLast)
//            val s = downloads?.get(1)
//
//           // if(s?.path != d?.path){
//                d?.isDownloading = s?.isDownloading == true
//                d?.isCanceled = s?.isCanceled == true
//                d?.progress = s?.progress?:1
//                d?.totalbytes = s?.totalbytes?:0
//                d?.name = s?.name
//                d?.path = s?.path
//                d?.dmId = s?.dmId?:0
//                d?.id = s?.id
//
//                adapter?.notifyItemChanged(secondLast)
//            //}
//
//        }
    }

    fun completed(download: com.tonyodev.fetch2.Download?) {

        val index = reverseList?.indexOfFirst { d -> d?.path == download?.fileUri.toString() }
        if (download != null) {
            val file = download.fileUri.toFile()
            val duration = try {
                file.getDuration(requireActivity())
            } catch (ex: Exception) {
                null
            }
            if (index != null && index > -1) {
                val d = reverseList?.get(index)
                d?.isDownloading = false
                d?.name = download.fileUri.lastPathSegment
                d?.path = download.fileUri.toString()
                d?.size = file.length()
                d?.duration = duration
                d?.dmId = download.id.toLong()

                if (reverseList != null)
                    downloads = ArrayList(reverseList!!.asReversed())

                adapter?.notifyItemChanged(index)
            } else {
                val d = Download(
                    download.fileUri.lastPathSegment,
                    download.fileUri.toString(),
                    file.length(),
                    duration,
                    download.id.toLong(),
                    false,
                    100,
                    download.id.toLong(),
                    download.tag,
                    download.total,
                    false
                )
                downloads?.add(d)
                reverseList = downloads?.asReversed()
                adapter?.submitList(reverseList)
            }
        }

        adapter?.notifyDataSetChanged()
//        val lastIndex = reverseList?.count()?: 0
//        val secondLast = lastIndex - 2
//        if(secondLast > 0) {
//            val d = reverseList?.get(secondLast)
//            val s = downloads?.get(1)
//
//            // if(s?.path != d?.path){
//            d?.isDownloading = s?.isDownloading == true
//            d?.isCanceled = s?.isCanceled == true
//            d?.progress = s?.progress?:1
//            d?.totalbytes = s?.totalbytes?:0
//            d?.name = s?.name
//            d?.path = s?.path
//            d?.dmId = s?.dmId?:0
//            d?.id = s?.id
//
//            adapter?.notifyItemChanged(secondLast)
//            //}
//
//        }

    }

    override fun itemClickResult(w: Download, name: String) {
        when (name) {
            "onCancel" -> {
                if (reverseList != null)
                    downloads = ArrayList(reverseList!!.asReversed())
                DownloadService.rxFetch?.cancel(w.dmId.toInt())
                DownloadService.rxFetch?.delete(w.dmId.toInt())
                DownloadService.updated.postValue(null)
                DownloadService.canceled.postValue(null)
                DownloadService.completed.postValue(null)

                val index2 = downloads?.indexOfFirst { d -> d?.dmId == w?.dmId }
                val i = DownloadService.downloadsList?.indexOfFirst { s -> s.id == w.dmId.toInt() }
                if (i != null && i > -1) {
                    DownloadService.downloadsList?.removeAt(i)
                }
                if (index2 != null && index2 > -1) {
                    downloads?.removeAt(index2)
                    reverseList = downloads?.asReversed()
                    adapter?.submitList(reverseList)
                }

                adapter?.notifyDataSetChanged()
                //StorageVideos.removeVideo(w.dmId)
            }

            "onRename" -> {
                alertDialogDemo(w.name, w.dmId)
            }

            "onPlay" -> {
                startActivity(
                    Intent(
                        requireActivity(),
                        PlayerActivity::class.java
                    ).putExtra("url", w.path.toString()).putExtra("id", w.name)
                )
            }

            "onShare" -> {
                val f: File? = try {
                    Uri.parse(w.path).toFile()
                } catch (ex: java.lang.Exception) {
                    null
                }
                val uri = f?.let {
                    FileProvider.getUriForFile(
                        requireActivity(),
                        "${requireActivity().packageName}",
                        it
                    )
                }
                if (uri != null) {
                    var shareFragment: ShareFragment? = null
                    shareFragment = ShareFragment(uri, {},{})
                    shareFragment.show(requireActivity().supportFragmentManager, "exit_dialog_tag")


                }

//                    startActivity(
//                        Intent(
//                            requireActivity(),
//                            ShareActivity::class.java
//                        ).putExtra("url", uri.toString())
//                    )
            }

            "onRepost" -> {
                if (requireActivity().isPackageInstalled("com.facebook.katana")) {
                    val intent: Intent? =
                        requireActivity().packageManager.getLaunchIntentForPackage("com.facebook.katana")
                    val f: File? = try {
                        Uri.parse(w.path).toFile()
                    } catch (ex: java.lang.Exception) {
                        null
                    }
                    val uri = f?.let {
                        FileProvider.getUriForFile(
                            requireActivity(),
                            "${requireActivity().packageName}",
                            it
                        )
                    }
                    if (uri != null) {

                        if (intent != null) {
                            try {
                                // The application exists
                                val shareIntent = Intent()
                                shareIntent.action = Intent.ACTION_SEND
                                shareIntent.setPackage("com.facebook.katana")
                                shareIntent.putExtra(Intent.EXTRA_TITLE, "Shared Video!")
                                shareIntent.type = "video/*"
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                // Start the specific social application
                                startActivity(shareIntent)
                            } catch (ex: Exception) {
                                Toast.makeText(requireActivity(), "Please Install App!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                } else Toast.makeText(requireActivity(), "Please Install App!", Toast.LENGTH_LONG).show()

            }

            "onMirror" -> {
                startActivity(Intent(requireActivity(), HowToUseActivity::class.java))
            }

            "onDelete" -> {
                deleteDialogDemo(w)
            }

            else ->{

            }

        }
    }

    private fun deleteDialogDemo(w: Download) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.delete_dialog)

        val okButton = dialog.findViewById<TextView>(R.id.grant_permission)
        val cancelDialog = dialog.findViewById<TextView>(R.id.button)
        okButton.setOnClickListener {
            DownloadService.rxFetch?.delete(w.dmId.toInt())
            val i = DownloadService.downloadsList?.indexOfFirst { s -> s.id == w.dmId.toInt() }
            if (i != null && i > -1) {
                DownloadService.downloadsList?.removeAt(i)
            }
            DownloadService.updated.postValue(null)
            DownloadService.canceled.postValue(null)
            DownloadService.completed.postValue(null)
            if (reverseList != null)
                downloads = ArrayList(reverseList!!.asReversed())
            val index2 = downloads?.indexOfFirst { d -> d?.dmId == w.dmId }
            if (index2 != null && index2 > -1) {
                downloads?.removeAt(index2)
                reverseList = downloads?.asReversed()
                adapter?.submitList(reverseList)
            }
            if(reverseList?.size!! <1)
            {
                binding.emptyListLayout.visibility=View.VISIBLE
            }
            else{
                binding.emptyListLayout.visibility=View.GONE
            }
            adapter?.notifyDataSetChanged()
            dialog.dismiss()
        }
        cancelDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun alertDialogDemo(name1: String?, dmId: Long) {

        // create an alert builder
        val builder = AlertDialog.Builder(requireActivity())
        val customLayout = AlertDialogBinding.inflate(layoutInflater, null, false)
        builder.setView(customLayout.root)
        val dialog = builder.create()
        customLayout.nameTextField.setText(name1)
        customLayout.btnDelete.setOnClickListener {
            val nameFile = customLayout.nameTextField.text.toString()
            if(nameFile.length>0)
            {
                val id = dmId.toInt()
                DownloadService.rxFetch?.getDownload(id) {
                    Log.v("", "")
                    val namePre = it?.fileUri?.lastPathSegment?.let { it1 -> getBaseName(it1) }

                    val name = namePre?.let { it1 -> it.file.replace(it1, nameFile) }

                    val f: File? = try {
                        it?.fileUri?.toFile()
                    } catch (ex: java.lang.Exception) {
                        null
                    }
                    if (f != null) {

                        lifecycleScope.launch(Dispatchers.IO) {

                            val fetchDatabaseManagerWrapper =
                                DownloadService.rxFetch?.fetchConfiguration?.let { it1 ->
                                    FetchModulesBuilder.buildModulesFromPrefs(
                                        it1
                                    ).fetchDatabaseManagerWrapper
                                }

                            val download = it?.id?.let { it1 -> fetchDatabaseManagerWrapper?.get(it1) }
                            withContext(Dispatchers.Main) {
                                dialog.dismiss()
                            }
                            if (download?.status != Status.COMPLETED) {
                                withContext(Dispatchers.Main) {
                                    dialog.dismiss()
                                }
                            }
                            val downloadWithFile = name?.let { it1 ->
                                fetchDatabaseManagerWrapper?.getByFile(
                                    it1
                                )
                            }
                            if (downloadWithFile != null) {
                                withContext(Dispatchers.Main) {
                                    dialog.dismiss()
                                }
                            }
                            val copy = fetchDatabaseManagerWrapper?.getNewDownloadInfoInstance()
                                ?.let { it1 -> download?.toDownloadInfo(it1) }
                            copy?.id = download?.url?.let { it1 ->
                                name?.let { it2 ->
                                    getUniqueId(
                                        it1,
                                        it2
                                    )
                                }
                            }!!
                            if (name != null) {
                                copy?.file = name
                            }
                            try {
                                val pair = copy?.let { it1 -> fetchDatabaseManagerWrapper.insert(it1) }
                                if (!pair?.second!!) {
                                    withContext(Dispatchers.Main) {
                                        dialog.dismiss()
                                    }
                                }
                                val renamed = nameFile.let { it1 -> FileUtility().renameFile(f, it1) }
                                if (!renamed) {
                                    fetchDatabaseManagerWrapper.delete(copy)
                                    withContext(Dispatchers.Main) {
                                        dialog.dismiss()
                                    }
                                } else {
                                    fetchDatabaseManagerWrapper.delete(download)
                                    val d2 = pair.first
                                    val index = reverseList?.indexOfFirst { d -> d?.dmId == dmId }
                                    val index2 = downloads?.indexOfFirst { d -> d?.dmId == dmId }
                                    val i =
                                        DownloadService.downloadsList?.indexOfFirst { s -> s.id == id }
                                    if (index2 != null && index2 > -1) {
                                        if (i != null) {
                                            DownloadService.downloadsList?.removeAt(i)
                                            DownloadService.downloadsList?.add(d2)
                                            DownloadService.updated.postValue(null)
                                            DownloadService.canceled.postValue(null)
                                            DownloadService.completed.postValue(null)
                                        }
                                        downloads?.get(index2)?.name = d2.fileUri.lastPathSegment
                                        downloads?.get(index2)?.path = d2.fileUri.toString()
                                        reverseList = downloads?.asReversed()
                                       withContext(Dispatchers.Main) {
                                            if (index != null && index > -1)
                                                adapter?.notifyItemChanged(index)
                                            dialog.dismiss()
                                        }
                                    }

                                }
                            } catch (e: Exception) {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireActivity(),
                                        "Name already exists!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                e.printStackTrace()

                            }
                        }

                    } else {
                        requireActivity().startService(Intent(requireActivity(), DownloadService::class.java))
                        Toast.makeText(
                            requireActivity(),
                            "SomeThing Went wrong!",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }

                }
            }
            else{
                Toast.makeText(requireActivity(), "Please enter video name", Toast.LENGTH_SHORT).show()
            }


        }
        dialog.show()


    }

    private fun getBaseName(fileName: String): String {
        val index = fileName.lastIndexOf('.')
        return if (index == -1) {
            fileName
        } else {
            fileName.substring(0, index)
        }
    }




    override fun onResume() {
        super.onResume()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.rvDownloads.windowToken, 0)
        isActivityRunning = true
        DownloadService.downloadsList?.forEach { download ->
            val index = reverseList?.indexOfFirst { d -> d?.dmId?.toInt() == download.id }
            if (download != null) {
                val file = try {
                    download.fileUri.toFile()
                } catch (ex: Exception) {
                    null
                }
                val duration = try {
                    file?.getDuration(requireActivity())
                } catch (ex: Exception) {
                    null
                }
                if (file != null) {
                    if (index != null && index > -1) {
                        val d = reverseList?.get(index)
                        d?.isDownloading = false
                        d?.name = download.fileUri.lastPathSegment
                        d?.path = download.fileUri.toString()
                        d?.size = file.length()
                        d?.duration = duration
                        d?.dmId = download.id.toLong()
                        adapter?.notifyItemChanged(index)
                    } else {
                        val d = convert(download)
                        downloads?.add(d)
                        reverseList = downloads?.asReversed()
                        adapter?.submitList(reverseList)



                    }
                }
            }

        }
        if(reverseList?.size!! <1)
        {
            binding.emptyListLayout.visibility=View.VISIBLE
        }
        else{
            binding.emptyListLayout.visibility=View.GONE
        }
        loadNativeAd(requireActivity(),binding.downloadsdNative,
            RemoteDateConfig.remoteAdSettings.admob_native_videos_downloaded_native_ad.value,requireActivity().layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})

        if(playerActivityBack)
        {
            adisready="notshowed"
            showInterAd(requireActivity(), RemoteDateConfig.remoteAdSettings.admob_video_downloader_player_back_inter_ad.value,{})
        }

    }
    private fun showInterAd(activity: Activity, status: String, functionalityListener: () -> Unit) {

        if (status == "on" && adisready == "notshowed" && InterAdsClass.currentInterAd != null) {
            loadingDialog = ProgressDialog(requireActivity(), "Loading...")
            loadingDialog?.dialogShow()
            Handler(Looper.getMainLooper()).postDelayed({
                if (isActivityRunning) {
                    playerActivityBack=false
                    InterAdsClass.getInstance().showInterAd123(activity,
                        {
                            functionalityListener.invoke()
                        }, {}, {

                            adisready = "showed"
                            loadingDialog?.dismiss()
                        })
                }
                else{
                    loadingDialog?.dismiss()
                }


            }, 900)
        } else {
            functionalityListener.invoke()
        }
    }
    override fun onPause() {
        super.onPause()
        isActivityRunning = false
    }

    companion object {

    }
}