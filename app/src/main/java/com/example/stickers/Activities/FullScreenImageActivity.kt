package com.example.stickers.Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Models.Status
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons.Companion.ShowWAppDialog
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.SaveHelperFull
import com.example.stickers.Utils.saveStatus
import com.example.stickers.ads.Ads
import com.example.stickers.ads.loadNativeAdmob
import com.example.stickers.ads.showInterAd
import com.example.stickers.ads.showInterDemandAdmob
import com.example.stickers.app.AppClass.Companion.file30List
import com.example.stickers.app.AppClass.Companion.fileList
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import com.example.stickers.databinding.ActivityFullScreenImageBinding
import com.squareup.picasso.Picasso
import java.io.File

class FullScreenImageActivity : BillingBaseActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    private var is30Plus = false
    var status: Status? = null
    var pos = 0
    var na = 0
    override fun onResume() {
        super.onResume()



        with(binding.nativeLayout) {
            loadNativeAdmob(
                root, adFrameLarge,
                R.layout.admob_native_small,
                R.layout.max_native_small, 2,
                remoteAdSettings.native_inner,{},{},  adId = remoteAdSettings.getAdmobSplashNativeId2()
            )
        }

        Ads().showBannerAd(
            applicationContext,
            binding.lytBanner,
            binding.progressBar7,
            binding.adView
        )
    }

    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        binding = ActivityFullScreenImageBinding.inflate(
            layoutInflater
        )
        val view: View = binding.root
        setContentView(view)

        SplashActivity.fbAnalytics?.sendEvent("FullImageActivity_Open")


        val getIntent = intent
        val tag = intent.extras?.getString("img_tag")

        is30Plus = intent.getBooleanExtra("is30Plus", false)

        if (is30Plus) {
        } else {
            status = getIntent.getSerializableExtra("status") as Status?
        }
        try {
            binding.imgDownload.visibility = View.VISIBLE
            if (tag == "saved") {
                binding.imgDownload.visibility = View.VISIBLE
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
            } else {
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic)
                binding!!.imgDownload.setOnClickListener {
                    if (is30Plus) {
                        saveStatus(binding.container, ItemsViewModel!!)
                        binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    } else {
                        Common.copyFile(status, applicationContext, binding!!.container)
                        binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                    }
                    MainDashActivity.isActivityShown=true
                    if(remoteAdSettings.admob_inter_download_btn_id.value.isNotEmpty())
                    {
                        showInterDemandAdmob(remoteAdSettings.admob_inter_download_btn_ad,remoteAdSettings.admob_inter_download_btn_id.value,{})

                    }

                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }

//        if (is30Plus) {
//            Picasso.with(getApplicationContext()).load(ImagesFragment.ItemsViewModel.getFile().getUri()).into(binding.imgFull);
//        } else {
//            Picasso.with(getApplicationContext()).load(status.getFile()).into(binding.imgFull);
//        }
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
        if (is30Plus) {

            binding.imgFull.visibility = View.VISIBLE
            if(ItemsViewModel?.getFile()?.getUri()!=null)
            {
                try {
                    Picasso.with(applicationContext).load(ItemsViewModel?.getFile()?.getUri())
                        .into(binding.imgFull);
                }
                catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

            }

        } else {
            if(status?.getFile()!=null)
            {
                try {
                    binding.imgFull.visibility = View.VISIBLE
                    Picasso.with(applicationContext).load(status?.getFile()).into(binding.imgFull);
                }
                catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

            }

        }
        binding.imageView25.setOnClickListener(
            View.OnClickListener {
                if (list.size > position + 1) {
                    position = position + 1

                    Picasso.with(applicationContext).load(Uri.parse(list[position]))
                        .into(binding.imgFull);
                }
            }
        )
        binding.imgPost.setOnClickListener(View.OnClickListener {
            val uri: Uri? = if (is30Plus) {
                ItemsViewModel?.file?.uri
            } else {
                Uri.parse("file://" + status!!.file.absolutePath)
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
            if (is30Plus) {

                ItemsViewModel?.file?.uri?.let { it1 -> shareFile(it1) }
            } else {

                shareFile(getUriPath(status!!.file.absolutePath))
            }
        }
        if (getIntent.action == "download") {
            binding!!.imgDownload.setImageResource(R.drawable.ic_download__1_)
            binding!!.imgDownload.setOnClickListener {
                Log.d("hshs", "onClick: hereeee")
                if (is30Plus) {
                    val root = Environment.getExternalStorageDirectory().toString()
                    Log.d("hshs", "onClick: hereeee11")
                    val justDirOut = File(Common.APP_DIR)
                    if (!justDirOut.exists()) {
                        justDirOut.mkdir()
                    }
                    Log.d("hshs", "onClick: hereeee22")
                    val outDirCopy = File(justDirOut, ItemsViewModel?.file?.name)
                    Log.d("hshs", "onClick: hereeee33")
                    val saveHelper = SaveHelperFull()
                    Log.d("hshs", "onClick: hereeee44")
                    ItemsViewModel?.file?.uri?.let { it1 ->
                        saveHelper.saveintopathFull(
                            it1,
                            outDirCopy,
                            this@FullScreenImageActivity
                        )
                    }
                    Log.d("hshs", "onClick: hereeee5555")
                } else {
                    Common.copyFile(status, applicationContext, binding!!.container)
                    binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                }


            }
        } else if (getIntent.action == "delete") {
            binding!!.imgDownload.setImageResource(R.drawable.ic_delete_ic)
            binding!!.imgDownload.setOnClickListener {
                if (status!!.file.delete()) {
                    Toast.makeText(this@FullScreenImageActivity, "File Deleted", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else Toast.makeText(
                    this@FullScreenImageActivity,
                    "Unable to Delete File",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        file30List = null
        fileList = null
        showInterAd(remoteAdSettings.inter_view_image) {
            super.onBackPressed()
        }
    }

    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }


}