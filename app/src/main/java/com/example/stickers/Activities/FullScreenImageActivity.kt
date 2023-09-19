package com.example.stickers.Activities

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.downloadClicked
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.clickedPosition
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.openSaved
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons.Companion.ShowWAppDialog
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.saveInToPath
import com.example.stickers.Utils.showSnackBar
import com.example.stickers.ads.AdmobCollapsibleBanner
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadAdaptiveBanner
import com.example.stickers.ads.loadNativeAd

import com.example.stickers.app.AppClass.Companion.file30List
import com.example.stickers.app.AppClass.Companion.fileList
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import com.example.stickers.databinding.ActivityFullScreenImageBinding
import com.example.stickers.dialog.ProgressDialog
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import java.io.File

class FullScreenImageActivity : BillingBaseActivity() {
    private var imgTag: String? = ""
    private lateinit var binding: ActivityFullScreenImageBinding
    private var is30Plus = false
    var status: Status? = null
    var pos = 0
    var na = 0

    var position = 0
    private val savedStatusList = mutableListOf<Boolean>()

    private val savedStatusListFiles = mutableListOf<StatusDocFile>()
    companion object{
         val savedStatusListFiles29 = mutableListOf<Status>()
        val savedVideoStatusListFiles29 = mutableListOf<Status>()
    }
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null

    override fun onPause() {
        super.onPause()
        isActivityRunning=false
    }
    override fun onResume() {
        super.onResume()
        isActivityRunning=true

        showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_download_btn_inter_ad.value){   }
        val frame = findViewById<FrameLayout>(R.id.full_screen_image_native)
       loadNativeAd(this,frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_full_screen_image_ad.value,layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        val frameBanner = findViewById<FrameLayout>(R.id.banner_adview)
     loadAdaptiveBanner(
            this,
            frameBanner,
            RemoteDateConfig.remoteAdSettings.admob_adaptive_image_full_scr_banner_ad.value
        )
    }
    private fun showInterAd(activity: Activity, status:String, functionalityListener: () -> Unit) {

        if (status=="on"&& adisready=="notshowed"&& InterAdsClass.currentInterAd !=null && downloadClicked) {

            loadingDialog?.dialogShow()
            Handler(Looper.getMainLooper()).postDelayed({
                if(isActivityRunning)
                {
                    downloadClicked=false
                    InterAdsClass.getInstance().showInterAd123(activity,
                        { functionalityListener.invoke()
                        }, {}, {

                            adisready="showed"
                            loadingDialog?.dismiss()
                        })
                }


            }, 900)
        }
        else{
            functionalityListener.invoke()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        binding = ActivityFullScreenImageBinding.inflate(
            layoutInflater
        )
        val view: View = binding.root
        setContentView(view)
        loadingDialog = ProgressDialog(this, "Loading...")
        SplashActivity.fbAnalytics?.sendEvent("FullImageActivity_Open")

        savedStatusListFiles.clear()
//        savedStatusListFiles29.clear()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (image in ImagesFragment.imagesList.reversed()) {
                val savedFileName = image.file?.name
                savedStatusList.add(Common.getSavedFile(savedFileName))
                if (savedFileName != null && Common.getSavedFile(savedFileName)) {
                    if (!savedStatusListFiles.contains(image)) {
                        savedStatusListFiles.add(image)
                    }
                }
            }
        } else {
            for (image in ImagesFragment.imagesList29.reversed()) {
                val savedFileName = image.file?.name
                savedStatusList.add(Common.getSavedFile(savedFileName))
                if (savedFileName != null && Common.getSavedFile(savedFileName)) {
                    if (!savedStatusListFiles29.contains(image)) {
                    //    savedStatusListFiles29.add(image)
                    }
                }
            }
        }

        imgTag = intent.getStringExtra("img_tag")
        val imageList = intent.getSerializableExtra("imageList") as? Array<StatusDocFile>

        val getIntent = intent
        val tag = intent.extras?.getString("img_tag")
        is30Plus = intent.getBooleanExtra("is30Plus", false)

        if (imgTag.isNullOrEmpty()) {
            binding!!.imgDownload.setImageResource(R.drawable.ic_delete_ic)
        }

        if (clickedPosition != null) {
            setupViewPager()
        }
        if (is30Plus) {
        } else {
            // status = getIntent.getSerializableExtra("status") as Status?
        }

        val list = ArrayList<String>()
        if (fileList != null && !fileList!!.isEmpty()) {
            for (a in fileList!!) {
                status?.let {
                    if (status!!.file === a.file) pos = na
                    list.add(Uri.fromFile(status!!.file).toString())
                    na++
                }
            }
        } else if (file30List != null && !file30List!!.isEmpty()) {
            for (a in file30List!!) {
                if (ItemsViewModel?.file === a.file) pos = na
                list.add(a.file.uri.toString())
                na++
            }
        }


        binding!!.imgDownload.setOnClickListener {
            if (!imgTag.isNullOrEmpty()) {
                if (is30Plus) {
                    //       saveStatus(binding.container, ItemsViewModel!!)
                    (ImagesFragment.imagesList.reversed()[binding.viewPager.currentItem]).apply {
                        saveStatusFromViewPager(this)
                    }

                } else {
                    (ImagesFragment.imagesList29.reversed()[binding.viewPager.currentItem]).apply {
                        saveStatusFromViewPager29(this)
                    }

                }
            } else {
                deleteSavedStatus()

            }
        }


        binding.imageView25.setOnClickListener(
            View.OnClickListener {
                if (list.size > position + 1) {
                    position += 1

                    Picasso.with(applicationContext).load(Uri.parse(list[position]))
                        .into(binding.imgFull);
                }
            }
        )

        binding.imgPost.setOnClickListener(View.OnClickListener {
          //  Log.e("WhatsAppicon", "onCreate: Clicked" )

            val uri: Uri?
                if (openSaved)
            {
                if (is30Plus) {
//                ItemsViewModel?.file?.uri?.let { it1 -> shareFile(it1) }
                    val imageUrl = savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.toUri()
                  uri=imageUrl
                } else {
                    val imageUrl = savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.toUri()
                    Log.e("share303", "$imageUrl: " )
                    uri=imageUrl
                }
            }
            else{
                if (is30Plus) {
//                ItemsViewModel?.file?.uri?.let { it1 -> shareFile(it1) }
                    val imageUrl = ImagesFragment.imagesList.reversed()[binding.viewPager.currentItem].file.uri
                    uri=imageUrl
                } else {
                    val imageUrl = ImagesFragment.imagesList29.reversed()[binding.viewPager.currentItem].file.toUri()
                    Log.e("share303", "$imageUrl: " )
                    uri=imageUrl

                }
            }

            if (uri != null) {
                ShowWAppDialog(binding.imgPost, uri, false)
            }

        })
//        binding.imageView26.setOnClickListener(
//            View.OnClickListener {
//                if (position - 1 >= 0) {
//                    position = position - 1
//
//                    Picasso.with(getApplicationContext()).load(Uri.parse(list[position])).into(binding.imgFull);
//                }
//            }
//        )
        binding.imgMirror.setOnClickListener {
            try {
                startActivity(Intent("android.settings.CAST_SETTINGS"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Casting is not supported!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.imgBackFullScreen.setOnClickListener { onBackPressed() }

        binding.imgShare.setOnClickListener {
            if (openSaved)
            {
                if (is30Plus) {
//                ItemsViewModel?.file?.uri?.let { it1 -> shareFile(it1) }
                    val imageUrl = savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.toUri()
                    shareFile(imageUrl, supportFragmentManager)
                } else {
                    val imageUrl = savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.toUri()
                    Log.e("share303", "$imageUrl: " )
                    shareFile(imageUrl,supportFragmentManager)

                }
            }
            else{
                if (is30Plus) {
//                ItemsViewModel?.file?.uri?.let { it1 -> shareFile(it1) }
                    val imageUrl = ImagesFragment.imagesList.reversed()[binding.viewPager.currentItem].file.uri
                    shareFile(imageUrl, supportFragmentManager)
                } else {
                    val imageUrl = ImagesFragment.imagesList29.reversed()[binding.viewPager.currentItem].file
                    Log.e("share303", "$imageUrl: " )
                    shareFile(ImagesFragment.imagesList29.reversed()[binding.viewPager.currentItem].file.toUri(),
                        supportFragmentManager
                    )

                }
            }
            }


    }

    private fun deleteSavedStatus() {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.delete_dialog)
        val okButton = dialog.findViewById<TextView>(R.id.grant_permission)
        val cancelDialog = dialog.findViewById<TextView>(R.id.button)
        okButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Common.deleteSavedFile(savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.name)
            }
            else{
                Common.deleteSavedFile(savedStatusListFiles29.reversed()[binding.viewPager.currentItem].file.name)
            }
            finish()
            dialog.dismiss()
        }
        cancelDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupViewPager() {
        val adapter = FullScreenImagePagerAdapter()
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit=0
        binding.viewPager.currentItem = clickedPosition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (binding.viewPager.currentItem >= 0&& ImagesFragment.imagesList.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val currentImage =
                        ImagesFragment.imagesList.reversed()[binding.viewPager.currentItem].file?.name.toString()
                    if (Common.getSavedFile(currentImage)) {
                        binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    } else {
                        binding.imgDownload.setImageResource(R.drawable.ic_download__1_)
                    }
                }, 50)
            }
        }else{
            if (binding.viewPager.currentItem >= 0 && ImagesFragment.imagesList29.isNotEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val currentImage =
                        ImagesFragment.imagesList29.reversed()[binding.viewPager.currentItem].file?.name.toString()
                    if (Common.getSavedFile(currentImage)) {
                        binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    } else {
                        binding.imgDownload.setImageResource(R.drawable.ic_download__1_)
                    }
                }, 50)
            }
        }
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var currentImage=""
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                var savedImageName =""
                if(!openSaved)
                {

                    savedImageName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                         currentImage = ImagesFragment.imagesList.reversed()[position].file?.name.toString()
                        currentImage
                    } else{
                         currentImage = ImagesFragment.imagesList29.reversed()[position].file?.name.toString()
                        currentImage
                    }
                    Common.getSavedFile(savedImageName)
                    if (!imgTag.isNullOrEmpty()) {

                        if ( Common.getSavedFile(currentImage)) {
                            binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                        } else {
                            binding.imgDownload.setImageResource(R.drawable.ic_download__1_)
                        }
                    } else {
                        binding!!.imgDownload.setImageResource(R.drawable.ic_delete_ic)
                    }
                }
                else{

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val currentImage = savedStatusListFiles.reversed()[position]
                        currentImage.file?.name.toString()
                    } else{
                        val currentImage = savedStatusListFiles29.reversed()[position]
                        currentImage.file?.name.toString()
                    }

                }


            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    override fun onBackPressed() {
        file30List = null
        fileList = null
            super.onBackPressed()
    }

    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    private fun saveStatusFromViewPager(currentImage: StatusDocFile) {
        if (Common.getSavedFile(currentImage.file.name)) {
            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
            showSnackBar(binding.viewPager, "Already downloaded.")
            return
        } else {
            val justDirOut = File(Common.APP_DIR)
            if (!justDirOut.exists()) {
                justDirOut.mkdir()
            }
            val outDirCopy = File(justDirOut, currentImage.file?.name!!)
            currentImage.file?.uri?.let {
                saveInToPath(it, outDirCopy)
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                showSnackBar(binding.viewPager, "File downloaded successfully.")
            }

        }
    }
    private fun saveStatusFromViewPager29(currentImage: Status) {
        if (Common.getSavedFile(currentImage.file.name)) {
            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
            showSnackBar(binding.viewPager, "Already downloaded.")
            return
        } else {
            val justDirOut = File(Common.APP_DIR)
            if (!justDirOut.exists()) {
                justDirOut.mkdir()
            }
            val outDirCopy = File(justDirOut, currentImage.file?.name!!)

            saveInToPath(currentImage.file.toUri(), outDirCopy)
            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
            showSnackBar(binding.viewPager, "File downloaded successfully.")


        }
    }

    inner class FullScreenImagePagerAdapter() : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.item_image, container, false)
            val imageView = view.findViewById<ZoomageView>(R.id.img_full)
            var imageUrl = ""

            if(!openSaved)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        val imageList = ImagesFragment.imagesList.reversed()
                        val currentImage = imageList[position]
                        imageUrl = currentImage.file?.uri.toString()
                   /* if ( Common.getSavedFile(imageList[position].file.name)) {
                        binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    } else {
                        binding.imgDownload.setImageResource(R.drawable.ic_download__1_)
                    }*/
                }

                else {
                    val imageList = ImagesFragment.imagesList29.reversed()
                    val currentImage = imageList[position]
                    imageUrl = currentImage.file?.toUri().toString()
                   /* if ( Common.getSavedFile(imageList[position].file.name)) {
                        binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    } else {
                        binding.imgDownload.setImageResource(R.drawable.ic_download__1_)
                    }*/
                }

            }
            else{

                    val imageList =savedStatusListFiles29
                    val currentImage = imageList.reversed()[position]
                    imageUrl = currentImage.file.toUri().toString()

            }

            Picasso.with(container.context).load(Uri.parse(imageUrl)).into(imageView)
            container.addView(view)
            return imageView
        }

        // Other overridden methods...
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {

            return if (!openSaved) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ImagesFragment.imagesList.size ?: 0
                } else {
                    ImagesFragment.imagesList29.size ?: 0
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    savedStatusListFiles29.size ?: 0
                } else {
                    savedStatusListFiles29.size ?: 0
                }
            }

        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }

}





