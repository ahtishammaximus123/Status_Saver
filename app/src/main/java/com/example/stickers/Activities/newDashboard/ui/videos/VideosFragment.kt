package com.example.stickers.Activities.newDashboard.ui.videos


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.base.BaseLiveStatusFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.Activities.repositories.Coroutines
import com.example.stickers.Adapter.VideoAdapter
import com.example.stickers.Adapter.VideoAdapter30plus
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.WAoptions
import com.example.stickers.Utils.WAoptions.Companion.appPackage
import com.example.stickers.ads.InterAdmobClass
import com.example.stickers.ads.afterDelay
import com.example.stickers.ads.showInterAd
import com.example.stickers.ads.showInterDemandAdmob
import com.example.stickers.app.AppClass
import com.example.stickers.app.AppClass.Companion.file30List
import com.example.stickers.app.AppClass.Companion.fileList
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.databinding.FragmentLiveVideosBinding
import com.example.stickers.dialog.ProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class VideosFragment : BaseLiveStatusFragment(), ImageAdapterCallBack {

    private var _binding: FragmentLiveVideosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    // private val imagesViewModel: ImagesViewModel by activityViewModels()
    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }
    private val videoList: ArrayList<Status> = ArrayList()
    private var videoList30plus: ArrayList<StatusDocFile> = ArrayList()
    private val handler = Handler()
    private var videoAdapter: VideoAdapter? = null
    private var container: ConstraintLayout? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var messageTextView: TextView? = null
    private var imgNoFound: ImageView? = null
    var anyImages = MutableLiveData<Int>()

    private val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 5544

    private var videoAdapter30plus: VideoAdapter30plus? = null
    private val notFromButtonnnn = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveVideosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tabLayoutNav = binding.tabLayout.tabLayoutNav
        selectTabCustom(1)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoList.clear()
        videoList30plus.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SplashActivity.fbAnalytics?.sendEvent("VideosActy_Open")

        recyclerView = view.findViewById(R.id.recyclerViewVideo)
        imgNoFound = view.findViewById(R.id.img_no_saved)
        progressBar = view.findViewById(R.id.prgressBarVideo)
        container = view.findViewById(R.id.container_video)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        messageTextView = view.findViewById(R.id.messageTextVideo)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
        )

        try {
            swipeRefreshLayout?.setOnRefreshListener { this.getStatus() }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = GridLayoutManager(activity, Common.GRID_COUNT)

        _binding?.grantPermission?.setOnClickListener {
            imagesViewModel.select(0)
        }
        imagesViewModel.selected.observe(viewLifecycleOwner) { item ->
            // Update the UI
//            if (item == 1) {

            videoList.clear()
            videoList30plus.clear()
//                if (notFromButtonnnn)
            getStatus()
            Log.d("tagV**", "getStatus")
//            }
            Log.e("status*", "statusV $item")
        }

//        if (notFromButtonnnn)

        afterDelay(1500) {
            getStatus()
        }

        _binding?.howToUse?.setOnClickListener {
            showHowToUse()
        }
    }
    private fun showHowToUse() {
        val dialog2 = activity?.let {
            Dialog(
                it,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
        }
        dialog2?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2?.setCancelable(false)
        dialog2?.setContentView(R.layout.dialog_open_whatsapp_actual)
        dialog2?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val okButton = dialog2?.findViewById<ImageView>(R.id.open_whatsApp_img)
        val cancelDialog = dialog2?.findViewById<ImageView>(R.id.close_open_whatsapp_dialog)
        okButton?.setOnClickListener { //open whatsapp
            val i = activity?.packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
            if (i != null) {
                startActivity(i)
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "whatsApp is not installed",
                    Toast.LENGTH_LONG
                ).show()
            }
            dialog2.dismiss()
        }
        cancelDialog?.setOnClickListener {
            dialog2.dismiss()
        }
        dialog2?.show()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("tree", "onActivityResult: came here : ")
        if (resultCode == -1) {
            Log.d("tree", "onActivityResult: RESULT_OK : ")
            if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE) {
                val uriTree = data!!.data
                Log.d("tree", "onActivityResult: uriTree : $uriTree")
                Log.d("tree", "onActivityResult: uriTree.getPath() : " + uriTree!!.path)
                //todo taking pres

// Check for the freshest data.
                requireActivity().contentResolver
                    .takePersistableUriPermission(
                        uriTree,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                val sharedPreferences =
                    requireContext().getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("uriTree", uriTree.toString())
                editor.apply()
                lifecycleScope.launch(Dispatchers.Default) {
                    read30SDKWithUri(uriTree)
                }
            }
        } else {
            _binding?.shimmerContent?.root?.visibility = View.GONE
            Log.d("tree", "onActivityResult: RESULT_NOT_OK : ")
        }
    }

    private var isInprog = false
    private suspend fun read30SDKWithUri(uriTree: Uri?) {
//        if(isInprog)
//            return
        isInprog = true
        showDialog()


        videoList.clear()
        videoList30plus.clear()
        fileList = videoList
        file30List = videoList30plus
        Coroutines.ioThenMain({
            val docFilePath = DocumentFile.fromTreeUri(requireActivity(), uriTree!!)
            docFilePath?.listFiles()?.forEach { docFile ->
                val statusDocFile = StatusDocFile(docFile, docFile.name, docFile.uri.path)
                if (statusDocFile.title != null && statusDocFile.title.endsWith(".mp4")) {
                    videoList30plus.add(statusDocFile)
                    val check: Boolean = getSavedFile(statusDocFile.title)
                    statusDocFile.setSavedStatus(check)
                }
            }
        }, {
            if (videoList30plus.size <= 0) {
                anyImages.postValue(0)
                imgNoFound!!.visibility = View.VISIBLE
                messageTextView!!.visibility = View.VISIBLE
                _binding?.points?.visibility=View.VISIBLE
                _binding?.howToUse?.visibility=View.VISIBLE
                _binding?.grantPermission?.visibility = View.GONE

            } else {
                messageTextView!!.visibility = View.GONE
                _binding?.points?.visibility=View.GONE
                _binding?.howToUse?.visibility=View.GONE
                _binding?.grantPermission?.visibility = View.GONE
                imgNoFound!!.visibility = View.GONE
            }

            _binding?.shimmerContent?.root?.visibility = View.GONE
            videoAdapter30plus?.notifyDataSetChanged()

            progressBar!!.visibility = View.GONE
            fileList = videoList
            file30List = videoList30plus
            swipeRefreshLayout?.isRefreshing = false
            checkList()
        })
/*        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.Default) {


            Log.e("tagV**", "S: ${videoList30plus.size}")
            withContext(Dispatchers.Main) {
//                isInprog = false
//                _binding?.shimmerContent?.root?.visibility = View.GONE

            }

        }
        job?.start()*/
    }

    private fun checkList() {
        try {
            _binding?.shimmerContent?.root?.visibility = View.GONE
            val adp = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                videoList
            } else {
                videoList30plus
            }
            if (adp.isEmpty()) {
                imgNoFound!!.visibility = View.VISIBLE
                messageTextView!!.visibility = View.VISIBLE
                _binding?.points?.visibility=View.VISIBLE
                _binding?.howToUse?.visibility=View.VISIBLE
                _binding?.grantPermission?.visibility = View.GONE
            } else {

                imgNoFound!!.visibility = View.GONE
                messageTextView!!.visibility = View.GONE
                _binding?.points?.visibility=View.GONE
                _binding?.howToUse?.visibility=View.GONE
                _binding?.grantPermission?.visibility = View.GONE
            }
            _binding?.shimmerContent?.root?.visibility = View.GONE
        } catch (exp: Exception) {
            dismissDialog()
        }
        dismissDialog()
    }

    var job: Job? = null
    private fun getStatus() {


        job?.cancel()
        videoList.clear()
        videoList30plus.clear()
//        recyclerView?.adapter = null
        _binding?.shimmerContent?.root?.visibility = View.VISIBLE

        _binding?.messageTextVideo?.visibility = View.GONE
        _binding?.grantPermission?.visibility = View.GONE
        _binding?.imgNoSaved?.visibility = View.GONE

        Log.d("tree", "getStatus: getStatus.getStatus  : called")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //for below 11
            Log.d("tree", "getStatus: below 30")

            videoList.clear()
            videoList30plus.clear()

            videoAdapter = VideoAdapter(videoList, container, this@VideosFragment)
            recyclerView?.adapter = videoAdapter
            videoAdapter?.notifyDataSetChanged()

            val dir = if (appPackage == "com.whatsapp.w4b") {
                Common.STATUS_DIRECTORY_2
            } else {
                Common.STATUS_DIRECTORY
            }
            lifecycleScope.launch(Dispatchers.Default) {
                execute(dir)
            }
        } else {
            //for 11
            Log.d("tree", "getStatus: 30 or above")
            val sharedPreferences =
                requireContext().getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
            var ut = "uriTree"
            if (appPackage == "com.whatsapp.w4b") ut = "uriTree1"
            if (sharedPreferences.getString(ut, "not present") != "not present") {
                Log.d("tree", "getStatus:  30 or above perm yes")
                val uriTree = sharedPreferences.getString(ut, "null")
                Log.d("tree", "getStatus : ureTree is : $uriTree")

                videoList.clear()
                videoList30plus.clear()
                videoAdapter30plus = VideoAdapter30plus(videoList30plus, requireActivity()) {
                    imagesViewModel.getAllFiles()
                    if(remoteAdSettings.admob_inter_download_btn_id.value.isNotEmpty())
                    {
                        requireActivity().showInterDemandAdmob(remoteAdSettings.admob_inter_download_btn_ad,remoteAdSettings.admob_inter_download_btn_id.value,{})

                    }

                }
                recyclerView?.adapter = videoAdapter30plus
                //                DocumentFile documentFile = DocumentFile.fromTreeUri(getActivity(), Uri.parse(uriTree));
//                execute30plus(documentFile);
//                execute30plusNewSameLikeOnActivity(Uri.parse(uriTree));
                lifecycleScope.launch(Dispatchers.Default) {
                    read30SDKWithUri(Uri.parse(uriTree))
                }
            } else {
                Log.d("tree", "onActivityResult: VERSION.SDK_INT  : 30 or above perm no, now take")
                anyImages.postValue(0)
                messageTextView!!.visibility = View.VISIBLE
                _binding?.points?.visibility=View.VISIBLE
                _binding?.grantPermission?.visibility = View.VISIBLE
                imgNoFound!!.visibility = View.VISIBLE
                swipeRefreshLayout?.isRefreshing = false
                _binding?.shimmerContent?.root?.visibility = View.GONE

//                takePermOfSpecialWhatsappFolder();
            }
        }
    }

    private fun execute(waFolder: File) {

        showDialog()
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.Default) {
            try {
                val statusFiles = waFolder.listFiles()
                videoList.clear()
                if (statusFiles != null && statusFiles.size > 0) {
                    Arrays.sort(statusFiles)
                    for (file in statusFiles) {
                        try {
                            Log.e(
                                "Checkingg",
                                "  " + file.name + "    " + file.parentFile
                            )
                            val status =
                                Status(
                                    file,
                                    file.name,
                                    file.absolutePath
                                )
                            if (status.isVideo) {
                                val check = getSavedFile(status.title)
                                status.setSavedStatus(check)
                                videoList.add(status)
                            }
                        } catch (ex: java.lang.Exception) {
                            swipeRefreshLayout?.isRefreshing = false
                            checkList()
                        }
                    }
                    handler.post {
                        checkList()
                        _binding?.shimmerContent?.root?.visibility = View.GONE
                        if (videoList.size <= 0) {
                            anyImages.postValue(0)
                            messageTextView!!.visibility = View.VISIBLE
                            _binding?.points?.visibility=View.VISIBLE
                            _binding?.howToUse?.visibility=View.VISIBLE
                            _binding?.grantPermission?.visibility = View.GONE
                            imgNoFound!!.visibility = View.VISIBLE
                        } else {
                            messageTextView?.visibility = View.GONE
                            _binding?.points?.visibility=View.GONE
                            _binding?.howToUse?.visibility=View.GONE
                            _binding?.grantPermission?.visibility = View.GONE
                            imgNoFound?.visibility = View.GONE
                        }
                        videoAdapter?.notifyDataSetChanged()
                        progressBar?.visibility = View.GONE
                    }
                } else {
                    handler.post {
                        anyImages.postValue(0)
                        progressBar?.visibility = View.GONE
                        messageTextView?.visibility = View.VISIBLE
                        if (MainDashActivity.isStoragePermissionDeny)
                            _binding?.grantPermission?.visibility = View.VISIBLE
                        _binding?.howToUse?.visibility=View.GONE
                        imgNoFound?.visibility = View.VISIBLE
                    }
                }
                swipeRefreshLayout?.isRefreshing = false
                _binding?.shimmerContent?.root?.visibility = View.GONE

                fileList = videoList
                file30List = videoList30plus
            } catch (ex: java.lang.Exception) {
                swipeRefreshLayout?.isRefreshing = false
                _binding?.shimmerContent?.root?.visibility = View.GONE
                checkList()
            }
            checkList()
        }
        job?.start()
    }


    private fun getSavedFile(name: String): Boolean {
        val app_dir = File(Common.APP_DIR)
        if (app_dir.exists()) {
            val savedFiles: Array<File>?
            savedFiles = app_dir.listFiles()
            if (savedFiles != null && savedFiles.size > 0) {
                Arrays.sort(savedFiles)
                for (file in savedFiles) {
                    val status = Status(file, file.name, file.absolutePath)
                    if (status.isVideo) {
                        if (name == status.title) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    override fun onShareClicked(status: Status) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val link = "http://play.google.com/store/apps/details?id=" + requireContext().packageName
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
        )
        shareIntent.type = "video/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.file.absolutePath))
        startActivity(shareIntent)
    }

    override fun onImageViewClicked(status: Status?, tag: Any) {
        fileList = videoList
        file30List = videoList30plus
        val i = Intent(context, FullScreenVideoActivity::class.java)
        i.action = "sa"
        i.putExtra("status", status)
        i.putExtra("img_tag", tag.toString() + "")
        Log.e("img_tag", tag.toString() + "")
        startActivity(i)


        Log.e("tagS**", "tagS**- " + status.toString())
    }

    override fun onDownloadClick(status: Status?, container: ConstraintLayout?) {

        InterAdmobClass.getInstance().loadAndShowInter(requireActivity(),remoteAdSettings.getAdmobDownloadBtnInterId(),ProgressDialog(requireActivity()),{})

        activity?.showInterDemandAdmob(remoteAdSettings.admob_inter_download_btn_ad,remoteAdSettings.admob_inter_download_btn_id.value) {
            Common.copyFile(status, activity, container)
            imagesViewModel.getAllFiles()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            var isNewDownloaded = false
            videoAdapter?.videoList?.forEach {
                if (it.isSavedStatus != getSavedFile(it.title))
                    isNewDownloaded = true
                it.isSavedStatus = getSavedFile(it.title)
            }
            videoAdapter?.notifyDataSetChanged()
            videoAdapter30plus?.mList?.forEach {
                if (it.isSavedStatus != getSavedFile(it.title))
                    isNewDownloaded = true
                it.isSavedStatus = getSavedFile(it.title)
            }
            videoAdapter30plus?.notifyDataSetChanged()
            Log.e("tag**", "a: $isNewDownloaded")
            if (isNewDownloaded)
                imagesViewModel.getAllFiles()
        } catch (exp: Exception) {
        }
    }
}