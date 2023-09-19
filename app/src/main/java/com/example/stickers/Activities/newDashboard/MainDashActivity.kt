package com.example.stickers.Activities.newDashboard

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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stickers.Activities.CollageFilesActivity
import com.example.stickers.Activities.HowToUseActivity
import com.example.stickers.Activities.ShareActivity
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.SplashActivity.Companion.splashAdLoaded
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isSavedMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedStatusList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList29
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.selectedStatusList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.selectedStatusList29
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.Adapter.ImageAdapter30plus
import com.example.stickers.BuildConfig
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.PremActivity
import com.example.stickers.R
import com.example.stickers.Utils.*
import com.example.stickers.Utils.AppCommons.Companion.initDash
import com.example.stickers.Utils.AppCommons.Companion.isAppInstalled
import com.example.stickers.ads.*
import com.example.stickers.ads.AdmobCollapsibleBanner.Companion.adViewCollapsible
import com.example.stickers.ads.AdmobCollapsibleBanner.Companion.isAdLoadedCollapsible
import com.example.stickers.app.AppClass
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.SharedPreferenceData
import com.example.stickers.app.getUriPath
import com.example.stickers.databinding.ActivityMainDashBinding
import com.example.stickers.databinding.DialogOpenWhatsappBinding
import com.example.stickers.dialog.ExitDialogFragment
import com.example.stickers.dialog.ProgressDialog
import com.example.stickers.dialog.SelectWhatsAppFragment
import com.example.stickers.dialog.ShareFragment
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.io.File


class MainDashActivity : BillingBaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var navController: NavController? = null
    private lateinit var binding: ActivityMainDashBinding
    var navView: NavigationView? = null
    private val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 5544
    private val REQUEST_ACTION_OPEN_DOCUMENT_TREE_2 = 55442
    private var dialog: ProgressDialog? = null
    private var imageAdapter30plus: ImageAdapter30plus? = null
    private lateinit var openWhat: ImageView
    private var exitDialogFragment: ExitDialogFragment? = null
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null
    private lateinit var nativeFrame: FrameLayout

    companion object {
        var isStoragePermissionDeny = true
        var isInterShown = false
        var isActivityShown = true
        var downloadClicked = false
        const val REQUEST_PERMISSIONS = 1234
        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var nativeAdNew1: NativeAd? = null
        var nativeAdNew2: NativeAd? = null


        val storagePermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        val storagePermissions33 = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )

        fun getPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                storagePermissions33
            } else {
                storagePermissions
            }
        }
    }


    private val model: ImagesViewModel by viewModels {
        ImagesViewModelFactory((application as AppClass).photosRep)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        initDash(SharedPreferenceData(this))
        binding = ActivityMainDashBinding.inflate(layoutInflater)
        loadingDialog = ProgressDialog(this, "Loading...")

        setContentView(binding.root)
        binding.textView10.text = "Version " + BuildConfig.VERSION_NAME
        initDrawer()
        openWhat = findViewById<ImageView>(R.id.open_whatsApp_icon)
        nativeFrame = findViewById<FrameLayout>(R.id.main_dash_native)
        nativeFrame.visibility = View.GONE
        var whatsAppType = SharedPreferenceData(this).getString("apppackage")
        if (whatsAppType == "com.whatsapp") {
            openWhat.setBackgroundResource(R.drawable.whats_app_icon)
        } else if (whatsAppType == "com.whatsapp.w4b") {
            openWhat.setBackgroundResource(R.drawable.w_business_icon)
        }

        openWhat.setOnClickListener {
            whatsAppType = SharedPreferenceData(this).getString("apppackage")
            Log.e("WhatsAppicon", "onCreate: Clicked")
            Log.e("WhatsAppicon", "$whatsAppType: Clicked")
            if (whatsAppType == "com.whatsapp") {
                Log.e("WhatsAppicon", "$whatsAppType: Clicked")
                val i = packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
                if (i != null) {
                    startActivity(i)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "whatsApp is not installed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (whatsAppType == "com.whatsapp.w4b") {
                val i = packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
                if (i != null) {
                    startActivity(i)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "whatsApp is not installed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val firstTime = SharedPreferenceData(this).getBoolean("ComingFirstTime", true)
        if (firstTime) {
            selectWhatsAppDialog() {
                var isAdShown = false
                model.selected.observe(this) {
                    Log.e("status*", "selected - $it")
                    if (it == 0 && isAdShown) {
                        startPermissions()

                    } else {

                    }
                }
                initDrawer()
                SplashActivity.fbAnalytics = FirebaseAnalytics(this)
                SplashActivity.fbAnalytics?.sendEvent("Dashboard_Open")


                interAdShowFunc(isAdShown)
            }
        } else {
            var isAdShown = false
            model.selected.observe(this) {
                Log.e("status*", "selected - $it")
                if (it == 0 && isAdShown) {
                    startPermissions()

                } else {

                }
            }
            initDrawer()
            SplashActivity.fbAnalytics = FirebaseAnalytics(this)
            SplashActivity.fbAnalytics?.sendEvent("Dashboard_Open")

            interAdShowFunc(isAdShown)

        }


        //binding.content.toolbar.setNavigationIcon(R.drawable.ic_menu)

        setSupportActionBar(binding.content.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navBottomView: BottomNavigationView = binding.content.navViewBottom
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.content.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main_dash)
        appBarConfiguration = AppBarConfiguration(navController!!.graph, drawerLayout)
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView?.setupWithNavController(navController!!)
        navBottomView.setupWithNavController(navController!!)
        navController?.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (/*nd.id == R.id.LiveVideosFragment
                || nd.id == R.id.SavedStatusesFragment
               || nd.id == R.id.LiveImagesFragment
               || */nd.id == R.id.photoCollageFragment || nd.id == R.id.stickerMakerFragment
            ) {
                openWhat.visibility = View.GONE
                if (adViewCollapsible == null) {
                    nativeFrame.visibility = View.VISIBLE
                }
                else{
                    nativeFrame.visibility = View.GONE
                }

                val multiSelectLay = findViewById<ConstraintLayout>(R.id.multiselect_lay)
                multiSelectLay?.visibility = View.GONE
                isMultiSelect = false
                isSavedMultiSelect = false
                selectedStatusList.clear()
                savedSelectedStatusList.clear()
                savedSelectedVideoStatusList.clear()

                supportActionBar?.show()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                toggle.isDrawerIndicatorEnabled = true
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()
            } else if (nd.id == R.id.galleryItemFragment || nd.id == R.id.galleryFragment
            ) {
                openWhat.visibility = View.VISIBLE
                nativeFrame.visibility = View.GONE
                hideSelectorLayout()
                supportActionBar?.hide()
            } else {
                openWhat.visibility = View.VISIBLE
                nativeFrame.visibility = View.GONE
                Log.e("atg**2", "$nd")
            }
            Log.e("atg**2", "$nd")
        }


    }

    private fun interAdShowFunc(isAdShown: Boolean) {
        var isAdShown1 = isAdShown
        if (isNetworkAvailable() && verifyInstallerId() && !isAlreadyPurchased() && !isInterShown && splashAdLoaded == "ready") {
            dialog = ProgressDialog(this, "Ad is Loading")
            dialog?.show()
            Handler(Looper.getMainLooper()).postDelayed({

                dialog?.dismiss()
                splashAdLoaded = "showed"
                isInterShown = true
                isAdShown1 = true

                if (!arePermissionDenied()) {

                    val sharedPreferences =
                        getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
                    var ut = "uriTree"
                    if (WAoptions.appPackage == "com.whatsapp.w4b") ut = "uriTree1"
                    if (sharedPreferences?.getString(ut, "not present") != "not present") {
                        model.select(1)
                    }
                } else {
                    startPermissions()
                    model.select(0)


                }



                Log.e("max_inter*", "loadMaxSplashInter")

            }, 700)


        } else {
            isInterShown = true
            isAdShown1 = true


            if (!arePermissionDenied()) {

                val sharedPreferences =
                    getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
                var ut = "uriTree"
                if (WAoptions.appPackage == "com.whatsapp.w4b") ut = "uriTree1"
                if (sharedPreferences?.getString(ut, "not present") != "not present") {
                    model.select(1)
                }
            } else {
                startPermissions()
                model.select(0)


            }
        }
    }


    override fun onResume() {
        super.onResume()
        isActivityShown = true
        isActivityRunning = true
        val frame = findViewById<FrameLayout>(R.id.main_dash_native)
        loadCollapsableBannerAd()
        loadNativeAd(this,
            frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_dashboard_ad.value,
            layoutInflater,
            R.layout.gnt_medium_template_without_media_view,
            { },
            {})
        showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_splash_inter_ad.value) {}
    }

    private fun loadCollapsableBannerAd() {
        if (RemoteDateConfig.remoteAdSettings.collapseAble_banner_ID.value.isNotEmpty() && RemoteDateConfig.remoteAdSettings.admob_collapsable_banner_ad.value == "on" && adViewCollapsible == null) {
            binding.content.collapseAbleBanner.visibility = View.VISIBLE
            if (!isAdLoadedCollapsible) {
                AdmobCollapsibleBanner.getInstance()
                    .loadAdmobCollapsible(this,
                        binding.content.collapseAbleBanner,
                        RemoteDateConfig.remoteAdSettings.collapseAble_banner_ID.value,
                        binding.content.collapseAbleBanner,
                        {
                            //load listener

                            AdmobCollapsibleBanner.getInstance()
                                .showCollapsibleAd(
                                    binding.content.collapseAbleBanner
                                )
                        },
                        {
                            //fail listener

                            loadAdaptiveBanner()
                            binding.content.collapseAbleBanner.visibility = View.GONE
                        },
                        {
                            //onAdd Close listener
                        },
                        {
                            //onAdd Open listener
                        })
            } else {
                AdmobCollapsibleBanner.getInstance().showCollapsibleAd(
                    binding.content.collapseAbleBanner,

                    )
            }

        } else if (adViewCollapsible == null) {
            loadAdaptiveBanner()

        } else if (adViewCollapsible != null) {
            AdmobCollapsibleBanner.getInstance().showCollapsibleAd(
                binding.content.collapseAbleBanner,

                )
        }

    }

    private fun loadAdaptiveBanner() {
        val bannerAd = findViewById<FrameLayout>(R.id.banner_adview)
        AdmobCollapsibleBanner.getInstance().loadAdmobAdaptiveBanner(this, bannerAd) {}

    }

    override fun onPause() {
        super.onPause()
        isActivityShown = false
        isActivityRunning = false
    }


    private fun initDrawer() {
        with(binding) {
            premium.setOnClickListener {
                if (!isAlreadyPurchased())
                    startActivity(Intent(applicationContext, PremActivity::class.java))
                else
                    showToast("Already Subscribed!")
            }
            screeenmirr.setOnClickListener {
                startActivity(Intent(applicationContext, HowToUseActivity::class.java))
            }
            collageFiles.setOnClickListener {
                startActivity(Intent(applicationContext, CollageFilesActivity::class.java))
            }
            feedback.setOnClickListener {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "thinkshotinc@gmail.com", null
                    )
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Status Saver")
                startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_us)))
            }
            rateus.setOnClickListener {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "http://play.googlee.com/store/apps/details?id="
                                    + applicationContext.packageName
                        )
                    )
                    startActivity(intent)
                } catch (e: Exception) {
                }
            }
            shareee.setOnClickListener {
                startActivity(Intent(applicationContext, ShareActivity::class.java))
            }
            moreappsads.setOnClickListener {

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.more_app))
                        )
                    )
                } catch (e: Exception) {
                }
            }
            privacypolicy.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.privacy_policy))
                        )
                    )
                } catch (e: Exception) {
                }
            }
            imageView21.setOnClickListener {
                exits.performClick()
            }
            exits.setOnClickListener {
                exitDialogFragment = ExitDialogFragment()
                exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")
            }
        }
    }

    fun actions() {
        Log.e("status*", "actions - selected")
        model.selected.postValue(1)
    }

    fun specialPermissionDialog() {
        if (WAoptions.appPackage == "com.whatsapp" &&
            !isAppInstalled(applicationContext, "com.whatsapp")
        ) {
            showToast("WhatsApp Not installed!")

        } else if (WAoptions.appPackage == "com.whatsapp.w4b" &&
            !isAppInstalled(applicationContext, "com.whatsapp.w4b")
        ) {
            showToast("WhatsApp Business Not installed!")
        } else {
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
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

    var permissionsD: PermissionDialog? = null
    private fun startPermissions() {
        permissionsD?.apply {
            if (isShowing)
                dismiss()
        }
        permissionsD = PermissionDialog(this)
        permissionsD?.show()
    }

    inner class PermissionDialog(activity: Activity) : Dialog(activity) {

        private lateinit var binding: DialogOpenWhatsappBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DialogOpenWhatsappBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setCancelable(false)

            window?.apply {
                setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setGravity(Gravity.BOTTOM)
            }

            with(binding) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    textView28.text = getString(R.string.permision_text)
                else
                    textView28.text = getString(R.string.permision_text_)

                grantPermission.setOnClickListener {
                    if (RemoteDateConfig.remoteAdSettings.admob_allow_permission_inter_ad.value == "on") {
                        adisready = "notshowed"
                    }

                    showInterAd(
                        this@MainDashActivity,
                        RemoteDateConfig.remoteAdSettings.admob_allow_permission_inter_ad.value
                    ) {
                        if (arePermissionDenied()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestPermissions(storagePermissions33, REQUEST_PERMISSIONS)
                            } else {
                                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS)
                            }

                        } else
                            specialPermissionDialog()
                        dismiss()

                    }


                }
            }
            loadNativeAd(this@MainDashActivity,
                binding.allowPermissionNative!!,
                RemoteDateConfig.remoteAdSettings.admob_native_allow_permission_bottom_sheet_ad.value,
                layoutInflater,
                R.layout.gnt_medium_template_view,
                { },
                {})


        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        isApplovinClicked = true
        if (requestCode == REQUEST_PERMISSIONS && grantResults.isNotEmpty()) {
            if (arePermissionDenied()) {

                val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!storageAccepted) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {

                        return
                    } else {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 1023)
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (arePermissionDenied()) {
                    ActivityCompat.requestPermissions(
                        this,
                        getPermissions(),
                        REQUEST_PERMISSIONS
                    )
                } else {
                    specialPermissionDialog()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                specialPermissionDialog()
            } else {
                model.select(1)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //todo from here ask this with a proper dialog or use previous dialog
    private val permissionSpecial: Unit
        get() {
            isApplovinClicked = true
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("tree", "onActivityResult: came here : ")
        if (resultCode == -1) {
            Log.d("tree", "onActivityResult: RESULT_OK : ")
            if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE) {
                isApplovinClicked = true
                val uriTree = data?.data
                if (uriTree != null) {
                    this.contentResolver
                        .takePersistableUriPermission(
                            uriTree,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                }
                val sharedPreferences = getSharedPreferences("uriTreePref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("uriTree", uriTree.toString())
                editor.apply()
                actions()

            }
            if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE_2) {
                isApplovinClicked = true
                val uriTree = data!!.data
                Log.d("tree", "onActivityResult: uriTree : $uriTree")
                Log.d("tree", "onActivityResult: uriTree.getPath() : " + uriTree!!.path)
                //todo taking pres

// Check for the freshest data.
                this.contentResolver
                    .takePersistableUriPermission(
                        uriTree,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                val sharedPreferences = getSharedPreferences("uriTreePref", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("uriTree1", uriTree.toString())
                editor.apply()
                actions()

            }

        } else {
            Log.d("tree", "onActivityResult: RESULT_NOT_OK : ")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                takePermOfSpecialWhatsappBusinessFolder()
//            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takePermOfSpecialWhatsappFolder() {
        isApplovinClicked = true
        wasAppinBack = false
        isAnyVisible = true
        val sm = this.getSystemService(STORAGE_SERVICE) as StorageManager
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
        wasAppinBack = false
        isAnyVisible = true
        BillingBaseActivity.isApplovinClicked = true
        startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun takePermOfSpecialWhatsappBusinessFolder() {

        val sm = this.getSystemService(STORAGE_SERVICE) as StorageManager
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

    private fun arePermissionDenied(): Boolean {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            for (permissions in storagePermissions33) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePermissionDeny = true
                    return true
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) {
            for (permissions in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePermissionDeny = true
                    return true
                }
            }
        } else {
            for (permissions in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        permissions
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    isStoragePermissionDeny = true
                    return true
                }
            }
        }
        isStoragePermissionDeny = false
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_dash)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    var myMenu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.status_menu, menu)
        if (WAoptions.appPackage == "com.whatsapp.w4b") {
            menu.getItem(1).isChecked = true
        }
        if (WAoptions.appPackage == "com.whatsapp") {
            menu.getItem(0).isChecked = true
        }
        myMenu = menu

        return true
    }

    private fun showInterAd(activity: Activity, status: String, functionalityListener: () -> Unit) {

        if (status == "on" && adisready == "notshowed" && InterAdsClass.currentInterAd != null) {

            loadingDialog?.dialogShow()
            Handler(Looper.getMainLooper()).postDelayed({
                if (isActivityRunning) {
                    InterAdsClass.getInstance().showInterAd123(activity,
                        {
                            functionalityListener.invoke()
                        }, {}, {

                            adisready = "showed"
                            loadingDialog?.dismiss()
                        })
                }


            }, 900)
        } else {
            functionalityListener.invoke()
        }
    }

    private fun selectWhatsAppDialog(proceedListenerr: () -> Unit) {

        val selectwhatsApp = SelectWhatsAppFragment(openWhat) { proceedListenerr.invoke() }
        selectwhatsApp.show(supportFragmentManager, "select_whatsApp_tag")

//        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomPAlertDialog)
//        val inflater = this.layoutInflater
//        val dialogView = inflater.inflate(R.layout.select_whats_app_dialog, null)
//        dialogBuilder.setView(dialogView)
//        val alertDialog = dialogBuilder.create()
//        val simpleBtn = dialogView.findViewById<TextView>(R.id.go_for_simple)
//        val businessBtn = dialogView.findViewById<TextView>(R.id.go_for_business)
//
//        simpleBtn.setOnClickListener {
//            alertDialog.dismiss()
//
//            SharedPreferenceData(this).putString("apppackage", WAoptions.appPackage)
//            SharedPreferenceData(this).putBoolean("ComingFirstTime", false)
//            if (isAppInstalled(applicationContext, "com.whatsapp")) {
//
//                SharedPreferenceData(this).putString("apppackage", "com.whatsapp")
//                myMenu?.getItem(0)?.isChecked = true
//                navController?.navigate(R.id.status)
//                openWhat.setBackgroundResource(R.drawable.whats_app_icon)
//
//
//            } else Toast.makeText(
//                applicationContext,
//                "WhatsApp is not installed",
//                Toast.LENGTH_LONG
//            ).show()
//            alertDialog.dismiss()
//            proceedListenerr.invoke()
//        }
//        businessBtn.setOnClickListener {
//            SharedPreferenceData(this).putString("apppackage", "com.whatsapp.w4b")
//            SharedPreferenceData(this).putBoolean("ComingFirstTime", false)
//            if (isAppInstalled(applicationContext, "com.whatsapp.w4b")) {
//                WAoptions.appPackage = "com.whatsapp.w4b"
//                SharedPreferenceData(this).putString(
//                    "apppackage",
//                    WAoptions.appPackage
//                )
//
//                myMenu?.getItem(1)?.isChecked = true
//                navController?.navigate(R.id.status)
//                openWhat.setBackgroundResource(R.drawable.w_business_icon)
//            } else Toast.makeText(
//                applicationContext,
//                "WhatsApp Business is not installed",
//                Toast.LENGTH_LONG
//            ).show()
//            proceedListenerr.invoke()
//            alertDialog.dismiss()
//
//        }
//        alertDialog.setCancelable(false)
//        alertDialog.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ba -> {
                if (isAppInstalled(applicationContext, "com.whatsapp.w4b")) {
                    WAoptions.appPackage = "com.whatsapp.w4b"
                    SharedPreferenceData(this).putString("apppackage", "com.whatsapp.w4b")
                    myMenu?.getItem(1)?.isChecked = true
                    navController?.navigate(R.id.status)

                    val whatsAppType = SharedPreferenceData(this).getString("apppackage")
                    if (whatsAppType == "com.whatsapp") {
                        openWhat.setBackgroundResource(R.drawable.whats_app_icon)
                    } else if (whatsAppType == "com.whatsapp.w4b") {
                        openWhat.setBackgroundResource(R.drawable.w_business_icon)
                    }

                } else Toast.makeText(
                    applicationContext,
                    "WhatsApp Business is not installed",
                    Toast.LENGTH_LONG
                ).show()
                true
            }

            R.id.action_wa -> {
                if (isAppInstalled(applicationContext, "com.whatsapp")) {
                    WAoptions.appPackage = "com.whatsapp"
                    SharedPreferenceData(this).putString("apppackage", "com.whatsapp")
                    myMenu?.getItem(0)?.isChecked = true
                    navController?.navigate(R.id.status)

                    val whatsAppType = SharedPreferenceData(this).getString("apppackage")
                    if (whatsAppType == "com.whatsapp") {
                        openWhat.setBackgroundResource(R.drawable.whats_app_icon)
                    } else if (whatsAppType == "com.whatsapp.w4b") {
                        openWhat.setBackgroundResource(R.drawable.w_business_icon)
                    }

                } else Toast.makeText(
                    applicationContext,
                    "WhatsApp is not installed",
                    Toast.LENGTH_LONG
                ).show()
                true
            }

            R.id.folder -> {
                openActivity<CollageFilesActivity>()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        Log.e(
            "atg**",
            "${navController?.currentDestination}"
        )
        when (navController?.currentDestination?.id) {
            R.id.status -> {
                exitDialogFragment = ExitDialogFragment()
                exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")
            }
            /*R.id.LiveVideosFragment, R.id.SavedStatusesFragment,
                //R.id.stickerMakerFragment, R.id.photoCollageFragment
            -> {
                navController?.navigate(R.id.LiveImagesFragment)
//                navController?.popBackStack(R.id.LiveImagesFragment, true)
            }*/
            R.id.galleryFragment -> {
                navController?.navigate(R.id.photoCollageFragment)
            }

            else -> super.onBackPressed()
        }
    }

    fun onMultiSelectMode(
        notifyAdapter: () -> Unit,
        deleteListener: () -> Unit,
        shareListener: () -> Unit
    ) {

        openWhat.visibility = View.GONE
        val multiSelectLay = findViewById<ConstraintLayout>(R.id.multiselect_lay)
        val closeAction = findViewById<ImageView>(R.id.close_action)
        val shareBtn = findViewById<ImageView>(R.id.share_btn)
        val download = findViewById<ImageView>(R.id.downloadbtn)
        download.setBackgroundResource(R.drawable.ic_download__1_)
        multiSelectLay.visibility = View.VISIBLE

        closeAction.setOnClickListener {
            hideSelectorLayout()
            notifyAdapter.invoke()
        }

        shareBtn.setOnClickListener {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                shareImagesToOtherApps29(this, selectedStatusList29)
            } else {
                shareImagesToOtherApps(this, selectedStatusList)
            }

            hideSelectorLayout()
            notifyAdapter.invoke()

        }
        download.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                saveStatusFromViewPager29(selectedStatusList29)
            } else {
                saveStatusFromViewPager(selectedStatusList)
            }
            hideSelectorLayout()
            notifyAdapter.invoke()
        }

    }

    fun onMultiVideoSelectMode(
        notifyAdapter: () -> Unit,
        deleteListener: () -> Unit,
        shareListener: () -> Unit
    ) {

        openWhat.visibility = View.GONE
        val multiSelectLay = findViewById<ConstraintLayout>(R.id.multiselect_lay)
        val closeAction = findViewById<ImageView>(R.id.close_action)
        val shareBtn = findViewById<ImageView>(R.id.share_btn)
        val download = findViewById<ImageView>(R.id.downloadbtn)
        download.setBackgroundResource(R.drawable.ic_download__1_)
        multiSelectLay.visibility = View.VISIBLE

        closeAction.setOnClickListener {
            hideSelectorLayout()
            notifyAdapter.invoke()
        }

        shareBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                shareVideosToOtherApps29(this,savedSelectedVideoStatusList29)
            } else {
                shareVideosToOtherApps(this,savedSelectedVideoStatusList)
            }
            hideSelectorLayout()
            notifyAdapter.invoke()

        }
        download.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                saveStatusFromViewPager29(savedSelectedVideoStatusList29)
            } else {
                saveStatusFromViewPager(savedSelectedVideoStatusList)
            }
            hideSelectorLayout()
            notifyAdapter.invoke()
        }


    }

    fun hideSelectorLayout() {
        val multiSelectLay = findViewById<ConstraintLayout>(R.id.multiselect_lay)
        multiSelectLay?.visibility = View.GONE
        isMultiSelect = false
        isSavedMultiSelect = false

        openWhat.visibility = View.VISIBLE
        selectedStatusList.clear()
        savedSelectedStatusList.clear()
        savedSelectedVideoStatusList.clear()
    }

    fun onSavedMultiSelectMode(
        notifyAdapter: () -> Unit,
        deleteListener: () -> Unit,
        shareListener: () -> Unit
    ) {
        openWhat.visibility = View.GONE
        val multiSelectLay = findViewById<ConstraintLayout>(R.id.multiselect_lay)
        val closeAction = findViewById<ImageView>(R.id.close_action)
        val shareBtn = findViewById<ImageView>(R.id.share_btn)
        val delete = findViewById<ImageView>(R.id.downloadbtn)
        delete.setBackgroundResource(R.drawable.baseline_delete_24)
        multiSelectLay.visibility = View.VISIBLE

        closeAction.setOnClickListener {
            hideSelectorLayout()

            notifyAdapter.invoke()
        }

        shareBtn.setOnClickListener {
            shareSavedImagesToOtherApps(this, savedSelectedStatusList)
            hideSelectorLayout()
            notifyAdapter.invoke()

        }
        delete.setOnClickListener {
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.delete_dialog)
            val okButton = dialog.findViewById<TextView>(R.id.grant_permission)
            val cancelDialog = dialog.findViewById<TextView>(R.id.button)
            okButton.setOnClickListener {
                deleteImages(savedSelectedStatusList, { notifyAdapter.invoke() })
                dialog.dismiss()
            }
            cancelDialog.setOnClickListener {
                hideSelectorLayout()
                notifyAdapter.invoke()
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    private fun shareSavedImagesToOtherApps(
        context: Context,
        selectedStatusList: MutableList<Status?>
    ) {
        val imageUris = ArrayList<Uri>()
        selectedStatusList.forEach {
            it?.path?.let { it1 -> getUriPath(it1) }?.let { it2 -> imageUris.add(it2) }
        }

        val link = "http://play.googlee.com/store/apps/details?id=${packageName}"
        val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()

        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null, {

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                setPackage("com.whatsapp")
                this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            }
            context.startActivity(Intent.createChooser(intent, "Share images via"))
        },
            {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                }
                context.startActivity(Intent.createChooser(intent, "Share images via"))
            })
        exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")
    }

    private fun deleteImages(selectedStatusList: MutableList<Status?>, updateRecycler: () -> Unit) {
        selectedStatusList.forEach { status ->
            status?.file?.let { filePath ->
                if (filePath.exists()) {
                    val deleted = filePath.delete()
                    if (deleted) {
                        showToast("Images Deleted")
                    } else {
                        showToast("Failed To Delete Images")
                    }
                }
            }
        }
        updateRecycler.invoke()
        hideSelectorLayout()
    }


    private fun shareImagesToOtherApps29(context: Context, selectedStatusList: List<Status>) {
        val imageUris = ArrayList<Uri>()
        selectedStatusList.forEach {
            it.path?.let { it1 -> getUriPath(it1) }?.let { it2 -> imageUris.add(it2) }
        }

        val link = "http://play.googlee.com/store/apps/details?id=${packageName}"
        val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()

        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null, {

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                setPackage("com.whatsapp")
                this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            }
            context.startActivity(Intent.createChooser(intent, "Share images via"))
        },
            {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                }
                context.startActivity(Intent.createChooser(intent, "Share images via"))
            })
        exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")
    }

    private fun shareImagesToOtherApps(context: Context, selectedStatusList: List<StatusDocFile>) {
        val link = "http://play.googlee.com/store/apps/details?id=${packageName}"
        val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()
        val imageUris = ArrayList<Uri>()
        selectedStatusList.forEach {
            imageUris.add(it.file.uri)
        }
        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null, {

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                setPackage("com.whatsapp")
                this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            }
            context.startActivity(Intent.createChooser(intent, "Share images via"))
        },
            {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                   this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                }
                context.startActivity(Intent.createChooser(intent, "Share images via"))
            })
        exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")


    }
    private fun shareVideosToOtherApps29(context: Context, selectedStatusList: List<Status>) {
        val videoUri = ArrayList<Uri>()
        selectedStatusList.forEach {
            it.path?.let { it1 -> getUriPath(it1) }?.let { it2 -> videoUri.add(it2) }
        }

        val link = "http://play.googlee.com/store/apps/details?id=${packageName}"
        val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()

        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null, {

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "video/*"
                setPackage("com.whatsapp")
                this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, videoUri)
            }
            context.startActivity(Intent.createChooser(intent, "Share images via"))
        },
            {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "video/*"
                    this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, videoUri)
                }
                context.startActivity(Intent.createChooser(intent, "Share images via"))
            })
        exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")
    }

    private fun shareVideosToOtherApps(context: Context, selectedStatusList: List<StatusDocFile>) {
        val link = "http://play.googlee.com/store/apps/details?id=${packageName}"
        val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()
        val videosUri = ArrayList<Uri>()
        selectedStatusList.forEach {
            videosUri.add(it.file.uri)
        }
        var exitDialogFragment: ShareFragment? = null
        exitDialogFragment = ShareFragment(null, {

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "video/*"
                setPackage("com.whatsapp")
                this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, videosUri)
            }
            context.startActivity(Intent.createChooser(intent, "Share images via"))
        },
            {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    this.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, videosUri)
                }
                context.startActivity(Intent.createChooser(intent, "Share images via"))
            })
        exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")


    }

    private fun saveStatusFromViewPager(selectedStatusList: List<StatusDocFile>) {
        selectedStatusList.forEach {
            if (Common.getSavedFile(it.file.name)) {
                // binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                // showSnackBar(binding.viewPager, "Already downloaded.")
                // return
            } else {
                val justDirOut = File(Common.APP_DIR)
                if (!justDirOut.exists()) {
                    justDirOut.mkdir()
                }
                val outDirCopy = File(justDirOut, it.file?.name!!)
                it.file?.uri?.let {
                    saveInToPath(it, outDirCopy)
                    //binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                }
            }
        }
        showSnackBar(binding.drawerLayout, "Files downloaded successfully.")
    }

    private fun saveStatusFromViewPager29(selectedStatusList: List<Status>) {
        selectedStatusList.forEach {
            if (Common.getSavedFile(it.file.name)) {
                // binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                // showSnackBar(binding.viewPager, "Already downloaded.")
                // return
            } else {
                val justDirOut = File(Common.APP_DIR)
                if (!justDirOut.exists()) {
                    justDirOut.mkdir()
                }
                val outDirCopy = File(justDirOut, it.file?.name!!).apply {

                    saveInToPath(getUriPath(it.path), this)
                }
                //binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)

            }
        }
        showSnackBar(binding.drawerLayout, "Files downloaded successfully.")
    }
}

