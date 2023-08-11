package com.example.stickers.Activities.newDashboard.ui.images

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.base.BaseLiveStatusFragment
import com.example.stickers.Activities.repositories.Coroutines
import com.example.stickers.Adapter.ImageAdapter
import com.example.stickers.Adapter.ImageAdapter30plus
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.WAoptions
import com.example.stickers.ads.InterAdmobClass
import com.example.stickers.ads.beGone
import com.example.stickers.ads.beVisible
import com.example.stickers.ads.showInterAd
import com.example.stickers.ads.showInterDemandAdmob
import com.example.stickers.app.AppClass
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.databinding.FragmentLiveImagesBinding
import com.example.stickers.dialog.ProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ImagesFragment : BaseLiveStatusFragment(), ImageAdapterCallBack {

    private var _binding: FragmentLiveImagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private val handler = Handler()
    private var imageAdapter: ImageAdapter? = null
    private var imageAdapter30plus: ImageAdapter30plus? = null
    private var container: ConstraintLayout? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var messageTextView: TextView? = null
    private var imgNoFound: ImageView? = null
    private val notFromButtonnnn = true
    val anyImages = MutableLiveData<Int>()

    companion object {
        var ItemsViewModel: StatusDocFile? = null
        private const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 5544
        private var isLoadedAllStatus = false
    }

    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val imagesViewModel =
//            ViewModelProvider(this)[ImagesViewModel::class.java]

        SplashActivity.fbAnalytics?.sendEvent("ImgStatusActy_Open")
        _binding = FragmentLiveImagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tabLayoutNav = binding.tabLayout.tabLayoutNav
        selectTabCustom(0)

        Log.e("atg**, ", "Img")

        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.folder)
        item.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = _binding?.recyclerViewImage
        imgNoFound = _binding?.imgNoSaved
        progressBar = _binding?.progressBarImage
        container = _binding?.container
        swipeRefreshLayout = _binding?.swipeRefreshLayout
        messageTextView = _binding?.messageTextImage
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
        )
        try {
            swipeRefreshLayout?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                status()
            })
        } catch (e: Exception) {
            Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }


        _binding?.howToUse?.setOnClickListener {
            showHowToUse()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            imageAdapter = ImageAdapter(container, this)
            with(_binding?.recyclerViewImage) {
                this?.layoutManager = GridLayoutManager(activity, Common.GRID_COUNT)
                this?.adapter = imageAdapter
            }
        } else {
            imageAdapter30plus = activity?.let {
                ImageAdapter30plus(it) {
                    imagesViewModel.getAllFiles()
                    if(remoteAdSettings.admob_inter_download_btn_id.value.isNotEmpty())
                    {
                        requireActivity().showInterDemandAdmob(remoteAdSettings.admob_inter_download_btn_ad,remoteAdSettings.admob_inter_download_btn_id.value,{})

                    }

                }
            }
            with(_binding?.recyclerViewImage) {
                this?.layoutManager = GridLayoutManager(activity, Common.GRID_COUNT)
                this?.adapter = imageAdapter30plus
            }
        }
        imagesViewModel.selected.observe(viewLifecycleOwner) { item ->
            // Update the UI
            val adp = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                imageAdapter
            } else {
                imageAdapter30plus
            }

            Log.e("status*", "statusI $item")
            if (adp?.currentList.isNullOrEmpty())
                status()
        }
        _binding?.grantPermission?.setOnClickListener {
            imagesViewModel.select(0)
        }

        status()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    private fun status() {

        _binding?.messageTextImage?.visibility = View.GONE
        _binding?.grantPermission?.visibility = View.GONE
        _binding?.imgNoSaved?.visibility = View.GONE
        Log.d("tree", "getStatus: getStatus.getStatus  : called")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.d("tree", "getStatus: below 30")


            _binding?.shimmerContent?.root?.visibility = View.VISIBLE
            if (MainDashActivity.isStoragePermissionDeny) {
                _binding?.grantPermission?.visibility = View.VISIBLE
            } else {

            }
            execute()
            Log.e("status*", "statusI execute")
        } else {
            //for 11
            Log.d("tree", "getStatus: 30 or above")
            val sharedPreferences =
                activity?.getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
            var ut = "uriTree"
            if (WAoptions.appPackage == "com.whatsapp.w4b") ut = "uriTree1"
            if (sharedPreferences?.getString(ut, "not present") != "not present") {
                Log.d("tree", "getStatus:  30 or above perm yes")
                val uriTree = sharedPreferences?.getString(ut, "null")

                _binding?.shimmerContent?.root?.visibility = View.VISIBLE
                imageAdapter?.submitList(arrayListOf())
                imageAdapter30plus?.submitList(arrayListOf())
                lifecycleScope.launch(Dispatchers.Default) {
                    read30SDKWithUri(Uri.parse(uriTree))
                }
            } else {
                Log.e("status*", "statusI else")
                _binding?.grantPermission?.visibility = View.VISIBLE
                _binding?.shimmerContent?.root?.visibility = View.GONE
                messageTextView!!.visibility = View.VISIBLE
                binding.points.visibility=View.VISIBLE
                binding.howToUse.visibility=View.GONE
                imgNoFound!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                anyImages.postValue(0)
            }
        }
    }

    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            for (permissions in MainDashActivity.PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
            }
        } else {
            for (permissions in MainDashActivity.PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun execute() {

        showDialog()
        job?.cancel()
        val imagesList: MutableList<Status> = ArrayList()

        Coroutines.ioThenMain({

            try {
                if (WAoptions.appPackage == "com.whatsapp") {
                    if (Common.STATUS_DIRECTORY_1.listFiles() != null) {
                        if (Objects.requireNonNull(Common.STATUS_DIRECTORY_1.listFiles())
                                .isNotEmpty()
                        ) {
                            wAFolder = Common.STATUS_DIRECTORY_1
                        }
                    }
                    if (Common.STATUS_DIRECTORY_NEW.listFiles() != null) {
                        if (Objects.requireNonNull(Common.STATUS_DIRECTORY_NEW.listFiles())
                                .isNotEmpty()
                        ) {
                            wAFolder = Common.STATUS_DIRECTORY_NEW
                        }
                    }
                } else {
                    if (Common.STATUS_DIRECTORY_2.listFiles() != null) {
                        if (Objects.requireNonNull(Common.STATUS_DIRECTORY_2.listFiles())
                                .isNotEmpty()
                        ) {
                            wAFolder = Common.STATUS_DIRECTORY_2
                        }
                    }
                    if (Common.STATUS_DIRECTORY_NEW_2.listFiles() != null) {
                        if (Objects.requireNonNull(Common.STATUS_DIRECTORY_NEW_2.listFiles())
                                .isNotEmpty()
                        ) {
                            wAFolder = Common.STATUS_DIRECTORY_NEW_2
                        }
                    }
                }
                val statusFiles: Array<File>? = wAFolder?.listFiles()
                wAFolder?.listFiles()?.forEach { file ->
                    val status = Status(file, file.name, file.absolutePath)
                    if (!status.isVideo && status.title.endsWith(".jpg") && !imagesList.contains(
                            status
                        )
                    ) {
                        imagesList.add(status)
                        val check = getSavedFile(status.title)
                        status.setSavedStatus(check)
                    }
                }
                /*if (statusFiles != null && statusFiles.isNotEmpty()) {
                    var i = 0
                    val size = statusFiles.size
//                    Arrays.sort(statusFiles)
                    while (isActive && i < size) {
                        val file = statusFiles[i]
                        try {
                            val status = Status(file, file.name, file.absolutePath)
                            if (!status.isVideo && status.title.endsWith(".jpg") && !imagesList.contains(
                                    status
                                )
                            ) {
                                imagesList.add(status)
                                val check = getSavedFile(status.title)
                                status.setSavedStatus(check)
                            }
                        } catch (ex: Exception) {
                            Log.e("sdds", "dds")
                            checkList()
                        }
                        i++
                    }
                }*/
            } catch (ex: Exception) {
            }
        }, {
            swipeRefreshLayout?.isRefreshing = false
            _binding?.shimmerContent?.root?.visibility = View.GONE
            if (imagesList.size <= 0) {
                anyImages.postValue(0)
                messageTextView?.visibility = View.VISIBLE
                binding.points.visibility=View.VISIBLE

                if (MainDashActivity.isStoragePermissionDeny) {
                    binding.howToUse.visibility = View.GONE
                    _binding?.grantPermission?.visibility = View.VISIBLE
                }
                else
                    _binding?.grantPermission?.visibility = View.GONE
                binding.howToUse.visibility=View.VISIBLE
                imgNoFound?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
            } else {
                messageTextView?.visibility = View.GONE
                binding.points.visibility=View.GONE
                binding.howToUse.visibility=View.GONE
                _binding?.grantPermission?.visibility = View.GONE
                imgNoFound?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                imageAdapter?.submitList(null)
                imageAdapter?.submitList(imagesList)
                progressBar?.visibility = View.GONE
            }


            AppClass.fileList = imagesList
            AppClass.file30List = null

            checkList()
        })

//        job?.start()
    }


    private fun checkList() {
        activity?.runOnUiThread {
            try {
                Log.e(
                    "status*", "statusI checkList" +
                            " ${imageAdapter?.currentList?.size}-${imageAdapter30plus?.currentList?.size}"
                )
                _binding?.shimmerContent?.root?.visibility = View.GONE
                _binding?.apply {
                    val adp = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        imageAdapter
                    } else {
                        imageAdapter30plus
                    }
                    if ((adp?.itemCount ?: 0) <= 0) {
                        messageTextView?.visibility = View.VISIBLE
                        binding.points.visibility=View.VISIBLE

                        if (MainDashActivity.isStoragePermissionDeny) {
                            binding.howToUse.visibility = View.GONE
                            _binding?.grantPermission?.visibility = View.VISIBLE
                        }
                        else
                            binding.howToUse.visibility=View.VISIBLE
                            _binding?.grantPermission?.visibility = View.GONE
                        imgNoFound?.visibility = View.VISIBLE
//                recyclerView?.visibility = View.GONE
                    } else {
                        messageTextView?.visibility = View.GONE
                        binding.points.visibility=View.GONE
                        binding.howToUse.visibility=View.GONE
                        _binding?.grantPermission?.visibility = View.GONE
                        imgNoFound?.visibility = View.GONE
//                recyclerView?.visibility = View.VISIBLE
                        progressBar?.visibility = View.GONE
                    }
                    _binding?.shimmerContent?.root?.visibility = View.GONE
                }
            } catch (exp: Exception) {

                dismissDialog()
            }

            _binding?.shimmerContent?.root?.visibility = View.GONE
            dismissDialog()
        }

    }

    private suspend fun read30SDKWithUri(uriTree: Uri?) {
        //imagesList30plus.clear()

        Log.e("status*", "statusI read30SDKWithUri")
        showDialog()
//        job?.cancel()

        val imagesList30plus: MutableList<StatusDocFile> = ArrayList()
        Coroutines.ioThenMain({
            val docFilePath = DocumentFile.fromTreeUri(requireActivity(), uriTree!!)

            docFilePath?.listFiles()?.forEach { docFile ->
                val statusDocFile = StatusDocFile(docFile, docFile.name, docFile.uri.path)
                if (!statusDocFile.isVideo && statusDocFile.title != null && statusDocFile.title.endsWith(
                        ".jpg"
                    ) && !imagesList30plus.contains(
                        statusDocFile
                    )
                ) {
                    imagesList30plus.add(statusDocFile)
                    val check = getSavedFile(statusDocFile.title)
                    statusDocFile.setSavedStatus(check)
                }
            }
        }, {
            Log.e("status*", "statusI shimmerContent G")
            swipeRefreshLayout?.isRefreshing = false
            _binding?.shimmerContent?.root?.visibility = View.GONE
            if (imagesList30plus.size <= 0) {
                anyImages.postValue(0)
                messageTextView?.visibility = View.VISIBLE
                binding.points.visibility=View.VISIBLE
                binding.howToUse.visibility=View.VISIBLE
                _binding?.grantPermission?.visibility = View.GONE
                imgNoFound?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
            } else {
                messageTextView?.visibility = View.GONE
                binding.points.visibility=View.GONE
                binding.howToUse.visibility=View.GONE
                _binding?.grantPermission?.visibility = View.GONE
                imgNoFound?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
            }
            imageAdapter30plus?.submitList(null)
            imageAdapter30plus?.submitList(imagesList30plus)
            progressBar?.visibility = View.GONE
            AppClass.fileList = null
            AppClass.file30List = imagesList30plus

            checkList()
        })
        job = lifecycleScope.launch(Dispatchers.IO) {

            /*while (isActive && i < size) {
                val docFile = docFilePath.listFiles()[i]
                val statusDocFile = StatusDocFile(docFile, docFile.name, docFile.uri.path)
                if (!statusDocFile.isVideo && statusDocFile.title != null && statusDocFile.title.endsWith(".jpg") && !imagesList30plus.contains(
                        statusDocFile
                    )
                ) {
                    imagesList30plus.add(statusDocFile)
                    val check = getSavedFile(statusDocFile.title)
                    statusDocFile.setSavedStatus(check)
                }
                i++
            }*/

            withContext(Dispatchers.Main) {

            }

        }
        job?.start()
    }

    var wAFolder: File? = null
    var job: Job? = null


    private fun getSavedFile(name: String): Boolean {

        if (Common.APP_DIR != null) {
            val app_dir = File(Common.APP_DIR)
            Log.e("checkinggsaved", "Saved Time    " + app_dir.absolutePath + "")
            if (app_dir.exists()) {
                val savedFiles: Array<File>?
                savedFiles = app_dir.listFiles()
                if (savedFiles != null && savedFiles.size > 0) {
                    Arrays.sort(savedFiles)
                    for (file in savedFiles) {
                        val status = Status(file, file.name, file.absolutePath)
                        if (!status.isVideo) {
//                            Log.e("checkinggsaved", "Saved Time    " + status.title)
//                            Log.e("checkinggsaved", "Saved Time    $name")
                            if (name == status.title) {
                                return true
                            }
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
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.file.absolutePath))
        startActivity(shareIntent)
    }

    override fun onImageViewClicked(status: Status, tag: Any) {
        //AppClass.Companion.setFileList(imagesList);
        //AppClass.Companion.setFile30List(imagesList30plus);
        Log.e("ss1", "ss $status")
        val i = Intent(context, FullScreenImageActivity::class.java)
        i.action = "asa"
        i.putExtra("status", status)
        i.putExtra("img_tag", tag.toString() + "")
        Log.e("img_tag", tag.toString() + "")
        startActivity(i)
    }

    override fun onDownloadClick(status: Status, container: ConstraintLayout) {
        InterAdmobClass.getInstance().loadAndShowInter(requireActivity(),remoteAdSettings.getAdmobDownloadBtnInterId(),
            ProgressDialog(requireActivity()),{})
        activity?.showInterAd(remoteAdSettings.inter_download_status) {
            Common.copyFile(status, activity, container)
            imagesViewModel.getAllFiles()
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    override fun onResume() {
        super.onResume()
        var isNewDownloaded = false
        imageAdapter?.currentList?.forEach {
            if (it.isSavedStatus != getSavedFile(it.title))
                isNewDownloaded = true
            it.isSavedStatus = getSavedFile(it.title)
        }
        imageAdapter?.notifyDataSetChanged()
        imageAdapter30plus?.currentList?.forEach {
            if (it.isSavedStatus != getSavedFile(it.title))
                isNewDownloaded = true
            it.isSavedStatus = getSavedFile(it.title)
        }
        imageAdapter30plus?.notifyDataSetChanged()

        Log.e("tag**", "a: $isNewDownloaded")
        if (isNewDownloaded)
            imagesViewModel.getAllFiles()
    }
}