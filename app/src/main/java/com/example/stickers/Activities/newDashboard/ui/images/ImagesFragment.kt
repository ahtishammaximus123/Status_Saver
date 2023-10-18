package com.example.stickers.Activities.newDashboard.ui.images

import android.Manifest
import android.app.Activity
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
import android.os.Looper
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.downloadClicked
import com.example.stickers.Activities.newDashboard.base.BaseLiveStatusFragment
import com.example.stickers.Activities.newDashboard.ui.videodownloader.VideoDownloaderMainFragment
import com.example.stickers.Activities.repositories.Coroutines
import com.example.stickers.Adapter.ImageAdapter
import com.example.stickers.Adapter.ImageAdapter30plus
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.MultiSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.WAoptions
import com.example.stickers.ads.InterAdsClass

import com.example.stickers.ads.showToast
import com.example.stickers.app.AppClass
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteAdDetails
import com.example.stickers.app.RemoteAdSettings
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.databinding.FragmentLiveImagesBinding
import com.example.stickers.dialog.ProgressDialog
import com.example.stickers.dialog.ShareFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ImagesFragment : BaseLiveStatusFragment(), ImageAdapterCallBack, MultiSelectCallback {

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
    private var adisready = ""
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null

    companion object {
        var clickedPosition: Int = 0
        var openSaved = false
        var ItemsViewModel: StatusDocFile? = null
        private const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 5544
        private val REQUEST_ACTION_OPEN_DOCUMENT_TREE_2 = 55442
        private var isLoadedAllStatus = false
        val imagesList: MutableList<StatusDocFile> = ArrayList()
        val imagesList29: MutableList<Status> = ArrayList()

        var isMultiSelect = false
        var isSavedMultiSelect = false
        val selectedStatusList = mutableListOf<StatusDocFile>()
        val selectedStatusList29 = mutableListOf<Status>()
        val savedSelectedVideoStatusList29 = mutableListOf<Status>()

        val savedSelectedStatusList = mutableListOf<Status?>()
        val savedSelectedVideoStatusList = mutableListOf<StatusDocFile>()
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
        loadingDialog = ProgressDialog(requireActivity(), "Loading...")
        Log.e("atg**, ", "Img")
      //  VideoDownloaderMainFragment.hideMenuItemButton.invoke(false)
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
            imageAdapter =
                ImageAdapter(container, this, this, binding.recyclerViewImage, requireActivity())

            with(_binding?.recyclerViewImage) {
                //     this?.layoutManager = GridLayoutManager(activity, Common.GRID_COUNT)
                this?.adapter = imageAdapter
                imageAdapter?.setLayoutManager()
            }

        } else {
            imageAdapter30plus = activity?.let {
                ImageAdapter30plus(it, {
                    imagesViewModel.getAllFiles()
                    downloadClicked = true

                }, this, binding.recyclerViewImage, requireActivity().supportFragmentManager)
            }
            with(_binding?.recyclerViewImage) {
                this?.adapter = imageAdapter30plus
                imageAdapter30plus?.setLayoutManager()
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
            if (arePermissionDenied()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(
                        MainDashActivity.storagePermissions33,
                        MainDashActivity.REQUEST_PERMISSIONS
                    )
                } else {
                    requestPermissions(
                        MainDashActivity.PERMISSIONS,
                        MainDashActivity.REQUEST_PERMISSIONS
                    )
                }

            } else
                specialPermissionDialog()
        }

        try {
            status()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun specialPermissionDialog() {
        if (WAoptions.appPackage == "com.whatsapp" &&
            !AppCommons.isAppInstalled(requireActivity(), "com.whatsapp")
        ) {
            requireActivity().showToast("WhatsApp Not installed!")

        } else if (WAoptions.appPackage == "com.whatsapp.w4b" &&
            !AppCommons.isAppInstalled(requireActivity(), "com.whatsapp.w4b")
        ) {
            requireActivity().showToast("WhatsApp Business Not installed!")
        } else {
            val dialog =
                Dialog(requireActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            dialog.setContentView(R.layout.dialog_grant_permission)
            val okButton = dialog.findViewById<Button>(R.id.grant_permission)
            val cancelDialog = dialog.findViewById<ImageView>(R.id.close_permission_dialog)
            okButton.setOnClickListener {
                permissionSpecial
                dialog.dismiss()
            }
            cancelDialog.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun arePermissionDenied(): Boolean {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            for (permissions in MainDashActivity.storagePermissions33) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    MainDashActivity.isStoragePermissionDeny = true
                    return true
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) {
            for (permissions in MainDashActivity.PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    MainDashActivity.isStoragePermissionDeny = true
                    return true
                }
            }
        } else {
            for (permissions in MainDashActivity.PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    MainDashActivity.isStoragePermissionDeny = true
                    return true
                }
            }
        }
        MainDashActivity.isStoragePermissionDeny = false
        return false
    }

    private val permissionSpecial: Unit
        get() {
            BillingBaseActivity.isApplovinClicked = true
//            val sharedPreferences = getSharedPreferences("uriTreePref", MODE_PRIVATE)
//            var ut = "uriTree"
//            if (WAoptions.appPackage == "com.whatsapp.w4b") ut = "uriTree1"
//            if (sharedPreferences.getString(ut, "not present") != "not present") {
//                Log.d("tree", "getStatus:  30 or above perm yes")
//                val uriTree = sharedPreferences.getString(ut, "null")
//                Log.d("tree", "getStatus : ureTree is : $uriTree")
//            } else {
            if (WAoptions.appPackage === "com.whatsapp") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    takePermOfSpecialWhatsappFolder()
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    takePermOfSpecialWhatsappBusinessFolder()
                }
            }

        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takePermOfSpecialWhatsappFolder() {
        BillingBaseActivity.isApplovinClicked = true
        BillingBaseActivity.wasAppinBack = false
        BillingBaseActivity.isAnyVisible = true
        val sm =
            requireActivity().getSystemService(AppCompatActivity.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()


        //takes directly to whatsapp .statuses path
        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.statuses"
        //val startDir = "Android%2Fmedia"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        //Log.d("tree", "INITIAL_URI scheme: $scheme")
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, startDir)
//        }
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        //Log.d("tree", "uri: $uri")
        BillingBaseActivity.wasAppinBack = false
        BillingBaseActivity.isAnyVisible = true
        BillingBaseActivity.isApplovinClicked = true
        startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun takePermOfSpecialWhatsappBusinessFolder() {

        val sm =
            requireActivity().getSystemService(AppCompatActivity.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir =
            "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        Log.d("tree", "INITIAL_URI scheme: $scheme")
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)

        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        Log.d("tree", "uri: $uri")
        BillingBaseActivity.isApplovinClicked = true
        startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE_2)
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
        val etxt = dialog2?.findViewById<TextView>(R.id.open_text)
        val i = activity?.packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
        if (i != null && i.`package` == "com.whatsapp") {
            Log.e("showHowToUse", "showHowToUse: ${i.`package`}")
            etxt?.setText("WhatsApp")
        } else if (i != null && i.`package` == "com.whatsapp.w4b") {
            etxt?.setText("WA Business")
        }
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
        isMultiSelect = false
        val parentActivity = activity as? MainDashActivity
        val frame = parentActivity?.findViewById<FrameLayout>(R.id.main_dash_native)
        frame?.visibility = View.GONE
        parentActivity?.hideSelectorLayout()
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
                binding.points.visibility = View.VISIBLE
                binding.howToUse.visibility = View.GONE
                imgNoFound!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                anyImages.postValue(0)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        BillingBaseActivity.isApplovinClicked = true
        if (requestCode == MainDashActivity.REQUEST_PERMISSIONS && grantResults.isNotEmpty()) {
            if (arePermissionDenied()) {

                val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!storageAccepted) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {

                        return
                    } else {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 1023)
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (arePermissionDenied()) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        MainDashActivity.getPermissions(),
                        MainDashActivity.REQUEST_PERMISSIONS
                    )
                } else {
                    specialPermissionDialog()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                specialPermissionDialog()
            } else {
                // model.select(1)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
            } else if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE_2) {
                BillingBaseActivity.isApplovinClicked = true
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
                val sharedPreferences = requireActivity().getSharedPreferences(
                    "uriTreePref",
                    AppCompatActivity.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString("uriTree1", uriTree.toString())
                editor.apply()

            }
        } else {
            _binding?.shimmerContent?.root?.visibility = View.GONE
            Log.d("tree", "onActivityResult: RESULT_NOT_OK : ")
        }
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
                if (imagesList29.size > 0) {
                    imagesList29.clear()
                }
                val statusFiles: Array<File>? = wAFolder?.listFiles()
                wAFolder?.listFiles()?.forEach { file ->
                    val status = Status(file, file.name, file.absolutePath)
                    if (!status.isVideo && status.title.endsWith(".jpg") && !imagesList.contains(
                            status
                        )
                    ) {
                        imagesList.add(status)
                        imagesList29.add(status)
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
                binding.points.visibility = View.VISIBLE

                if (MainDashActivity.isStoragePermissionDeny) {
                    binding.howToUse.visibility = View.GONE
                    _binding?.grantPermission?.visibility = View.VISIBLE
                } else {
                    _binding?.grantPermission?.visibility = View.GONE
                    binding.howToUse.visibility = View.VISIBLE
                    imgNoFound?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE

                }

            } else {
                messageTextView?.visibility = View.GONE
                binding.points.visibility = View.GONE
                binding.howToUse.visibility = View.GONE
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
                        binding.points.visibility = View.VISIBLE

                        if (MainDashActivity.isStoragePermissionDeny) {
                            binding.howToUse.visibility = View.GONE
                            _binding?.grantPermission?.visibility = View.VISIBLE
                        } else {
                            binding.howToUse.visibility = View.VISIBLE
                            _binding?.grantPermission?.visibility = View.GONE
                            imgNoFound?.visibility = View.VISIBLE
                        }

//                recyclerView?.visibility = View.GONE
                    } else {
                        messageTextView?.visibility = View.GONE
                        binding.points.visibility = View.GONE
                        binding.howToUse.visibility = View.GONE
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
            if (imagesList.size > 0) {
                imagesList.clear()
            }

            docFilePath?.listFiles()?.forEach { docFile ->
                val statusDocFile = StatusDocFile(docFile, docFile.name, docFile.uri.path)

                if (!statusDocFile.isVideo && statusDocFile.title != null
                    && statusDocFile.title.endsWith(".jpg") && !imagesList30plus.contains(
                        statusDocFile
                    )
                ) {
                    imagesList30plus.add(statusDocFile)
                    imagesList.add(statusDocFile)
                    //imagesList29.add(status)
                    val check = getSavedFile(statusDocFile.title)
                    statusDocFile.setSavedStatus(check)
                }
            }
        }, {
            Log.e("status*", "statusI shimmerContent G")
            swipeRefreshLayout?.isRefreshing = false
            _binding?.shimmerContent?.root?.visibility = View.GONE

            try {
                if (imagesList30plus.size <= 0) {
                    anyImages.postValue(0)
                    messageTextView?.visibility = View.VISIBLE
                    binding.points.visibility = View.VISIBLE
                    binding.howToUse.visibility = View.VISIBLE
                    _binding?.grantPermission?.visibility = View.GONE
                    imgNoFound?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                } else {
                    messageTextView?.visibility = View.GONE
                    binding.points.visibility = View.GONE
                    binding.howToUse.visibility = View.GONE
                    _binding?.grantPermission?.visibility = View.GONE
                    imgNoFound?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null,
            {
                val shareIntent = Intent(Intent.ACTION_SEND)
                val link =
                    "http://play.google.com/store/apps/details?id=" + requireContext().packageName
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
                )
                shareIntent.type = "image/*"
                shareIntent.setPackage("com.whatsapp")
                Log.e("share29", "${Uri.parse("file://" + status.file.absolutePath)} ")
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse("file://" + status.file.absolutePath)
                )
                startActivity(shareIntent)
            },
            {
                val shareIntent = Intent(Intent.ACTION_SEND)
                val link =
                    "http://play.google.com/store/apps/details?id=" + requireContext().packageName
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
                )
                shareIntent.type = "image/*"
                Log.e("share29", "${Uri.parse("file://" + status.file.absolutePath)} ")
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse("file://" + status.file.absolutePath)
                )
                startActivity(shareIntent)
            })
        exitDialogFragment!!.show(requireActivity().supportFragmentManager, "exit_dialog_tag")

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
        downloadClicked = true

        Common.copyFile(status, activity, container)
        imagesViewModel.getAllFiles()
        imageAdapter?.notifyDataSetChanged()
        //     }
    }

    override fun onPause() {
        super.onPause()
        isActivityRunning = false
        job?.cancel()
    }

    override fun onResume() {
        super.onResume()
        isActivityRunning = true


        try {
            status()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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


    override fun onMultiSelectModeActivated() {
        val parentActivity = activity as? MainDashActivity
        parentActivity?.onMultiSelectMode(
            {
                status()
            },
            {
                //deleteListener

            },
            {
                //ShareListener


            })

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        val parentActivity = activity as? MainDashActivity
        parentActivity?.hideSelectorLayout()
    }
}


