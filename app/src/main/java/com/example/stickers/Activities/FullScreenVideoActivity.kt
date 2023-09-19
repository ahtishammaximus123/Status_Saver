package com.example.stickers.Activities

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.stickers.Activities.SplashActivity.Companion.fbAnalytics
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.downloadClicked
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Models.Status
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons.Companion.ShowWAppDialog
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.SaveHelperFull
import com.example.stickers.Utils.saveStatus
import com.example.stickers.ads.AdmobCollapsibleBanner
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadAdaptiveBanner
import com.example.stickers.ads.loadNativeAd


import com.example.stickers.app.AppClass.Companion.file30List
import com.example.stickers.app.AppClass.Companion.fileList
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import com.example.stickers.databinding.ActivityFullScreenVideoBinding
import com.example.stickers.dialog.ProgressDialog
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import java.io.File

class FullScreenVideoActivity() : BillingBaseActivity() {
    private var binding: ActivityFullScreenVideoBinding? = null
    private var is30Plus = false
    var status: Status? = null
    var player: ExoPlayer? = null
    var fullscreen = false
    var lock = false
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null
    override fun onPause() {
        super.onPause()
        isActivityRunning=false
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityRunning=true
        showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_download_btn_inter_ad.value){   }
        val frame = findViewById<FrameLayout>(R.id.full_screen_video_native)
        loadNativeAd(this,frame!!,
            remoteAdSettings.admob_native_full_screen_video_ad.value,layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        var hasVid = false
        if (is30Plus) {
            hasVid = true
        } else {
            if (status != null) hasVid = true
        }
        if (is30Plus) {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(VideosFragment.ItemsViewModel.getFile().getUri());
            // Attach player to the view.
            if (ItemsViewModel != null) mediaItem = MediaItem.fromUri(
                ItemsViewModel!!.file.uri
            )
        } else {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
            mediaItem = MediaItem.fromUri(Uri.fromFile(status!!.file))
        }
        if (hasVid && player == null) {
            player = ExoPlayer.Builder(this).build()
            binding!!.videoFull.player = player
            if (mediaItem != null) player!!.setMediaItem(mediaItem!!)
            binding!!.controls.player = player
            player!!.prepare()

        }

        val frameBanner = findViewById<FrameLayout>(R.id.banner_adview)
        loadAdaptiveBanner(
            this,
            frameBanner,
            remoteAdSettings.admob_adaptive_video_full_scr_banner_ad.value
        )
    }
    private fun showInterAd(activity: Activity, status:String, functionalityListener: () -> Unit) {

        if (status=="on"&& adisready=="notshowed"&& InterAdsClass.currentInterAd !=null && downloadClicked ) {

            loadingDialog?.dialogShow()
            Handler(Looper.getMainLooper()).postDelayed({
                if(isActivityRunning)
                {
                    downloadClicked=false
                    InterAdsClass.getInstance().showInterAd123(activity,
                        {
                            if (player != null) {
                                player!!.prepare()
                                player?.play()

                            }
                        }, {}, {

                            adisready="showed"
                            loadingDialog?.dismiss()
                        })
                }


            }, 900)
        }
        else{
            if (player != null) {
                player!!.prepare()
                player?.play()

            }
            functionalityListener.invoke()
        }
    }
    //    int position = 0;
    var mediaItem: MediaItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        binding = ActivityFullScreenVideoBinding.inflate(
            layoutInflater
        )
        val view: View = binding!!.root
        setContentView(view)
        loadingDialog = ProgressDialog(this, "Loading...")
        //setAdLyt1(binding.lytBanner);
        //setAdContent1(binding.adView);
        //setAdProgressBar1(binding.progressBar7);
        if (fbAnalytics != null) fbAnalytics!!.sendEvent("FullVideoActivity_Open")

        binding!!.imgPost.setOnClickListener(View.OnClickListener {
            try {
                val uri: Uri
                if (is30Plus) {
                    uri = ItemsViewModel!!.file.uri
                } else {
                    uri = Uri.parse("file://" + status!!.file.absolutePath)
                }
                this@FullScreenVideoActivity.ShowWAppDialog(binding!!.imgPost, uri, true)
            } catch (ignored: Exception) {
            }
        })
        binding!!.imageView22.setOnClickListener({ view1: View? -> binding!!.imgMirror.performClick() })
        binding!!.imgMirror.setOnClickListener { view1: View? ->
            try {
                startActivity(Intent("android.settings.CAST_SETTINGS"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    getApplicationContext(),
                    "Casting is not supported!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val getIntent = intent
        if (intent != null) is30Plus = intent.getBooleanExtra("is30Plus", false)
        if (is30Plus) {
        } else {
            status = getIntent!!.getSerializableExtra("status") as Status?
        }
        val tag = intent.extras!!.getString("img_tag")
        Log.e("img_tag", "$tag     jkjkkj")
        try {
            if ((tag == "saved")) {
                Log.e("img_tag", tag + "")
                binding!!.imgDownload.visibility = View.VISIBLE
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                binding!!.imgDownload.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        Toast.makeText(
                            applicationContext,
                            "Video Already Downloaded",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic)
                binding!!.imgDownload.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if (is30Plus) {
                            applicationContext.saveStatus(binding!!.container, (ItemsViewModel)!!)
                            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                        } else {
                            Common.copyFile(status, applicationContext, binding!!.container)
                            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Video ALready Downloaded", Toast.LENGTH_SHORT)
                .show()
        }
        binding!!.imgBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })
        binding!!.imgShare.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                if (is30Plus) {
                    ItemsViewModel?.file?.uri?.let { this@FullScreenVideoActivity.shareFile(
                        it,
                        supportFragmentManager
                    ) }
                } else {
                    this@FullScreenVideoActivity.shareFile(this@FullScreenVideoActivity.
                        status!!.file.toUri(),
                        supportFragmentManager
                    )
                }
            }
        })
        if (getIntent != null && getIntent.action != null) {
            if ((getIntent.action == "download")) {
                binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic)
                binding!!.imgDownload.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        Log.d("hshs", "onClick: hereeee")
                        if (is30Plus) {
                            val root = Environment.getExternalStorageDirectory().toString()
                            val justDirOut = File(Common.APP_DIR)
                            if (!justDirOut.exists()) {
                                justDirOut.mkdir()
                            }
                            Log.d("hshs", "onClick: hereeee11")
                            val outDirCopy = File(justDirOut, ItemsViewModel!!.file.name)
                            Log.d("hshs", "onClick: hereeee22")
                            val saveHelper = SaveHelperFull()
                            Log.d("hshs", "onClick: hereeee33")
                            saveHelper.saveintopathFull(
                                ItemsViewModel!!.file.uri,
                                outDirCopy,
                                this@FullScreenVideoActivity
                            )
                            Log.d("hshs", "onClick: hereeee44")
                            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                        } else {
                            Common.copyFile(status, applicationContext, binding!!.container)
                            binding!!.imgDownload.setImageResource(R.drawable.ic_download_ic__1_)
                        }
                    }
                })
            } else if ((getIntent.action == "delete")) {
                binding!!.imgDownload.setImageResource(R.drawable.ic_delete_ic)
                binding!!.imgDownload.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                      deleteSavedStatus()
                    }
                })
            }
        }
        player = ExoPlayer.Builder(this).build()
        binding!!.videoFull.player = player
        if (is30Plus) {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(VideosFragment.ItemsViewModel.getFile().getUri());
            // Attach player to the view.
            if (ItemsViewModel != null) mediaItem = MediaItem.fromUri(
                ItemsViewModel!!.file.uri
            )
        } else {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
            mediaItem = MediaItem.fromUri(Uri.fromFile(status!!.file))
        }
        if (mediaItem != null) player!!.setMediaItem(mediaItem!!)
        binding!!.controls.player = player
        player!!.prepare()

        binding!!.videoFull.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (fullscreen) {
                    if (binding!!.controls.isVisible) binding!!.controls.hide() else {
                        binding!!.controls.show()
                    }
                }
            }
        })
        val fullscreenButton = binding!!.controls.findViewById<ImageView>(R.id.exo_fullscreen_icon)
        val lockButton = binding!!.controls.findViewById<ImageView>(R.id.exo_lock_icon)
        fullscreenButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (!lock) {
                    if (fullscreen) {

                        binding!!.imageView22.visibility = View.GONE
                        binding!!.fullScreenVideoNative.visibility=View.VISIBLE
                        binding!!.bannerAdview.visibility=View.VISIBLE
                        fullscreenButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@FullScreenVideoActivity,
                                R.drawable.ic_full
                            )
                        )
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        if (supportActionBar != null) {
                            supportActionBar!!.show()
                        }
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        val params =
                            binding!!.constraintLayout3.layoutParams as ConstraintLayout.LayoutParams
                        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                        params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                        params.topMargin =
                            (13 * applicationContext.resources.displayMetrics.density).toInt()
                        params.bottomMargin = 0
                        params.leftMargin =
                            (13 * applicationContext.resources.displayMetrics.density).toInt()
                        params.rightMargin =
                            (13 * applicationContext.resources.displayMetrics.density).toInt()
                        binding!!.constraintLayout3.layoutParams = params
                        fullscreen = false
                        binding!!.videoFull.resizeMode =
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        //player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
                        binding!!.controls.show()
                    } else {

                        binding!!.imageView22.visibility = View.VISIBLE
                        binding!!.fullScreenVideoNative.visibility=View.GONE
                        binding!!.bannerAdview.visibility=View.GONE
                        fullscreenButton.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@FullScreenVideoActivity,
                                R.drawable.ic_potrait
                            )
                        )
                        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                        if (supportActionBar != null) {
                            supportActionBar!!.hide()
                        }
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        val params =
                            binding!!.constraintLayout3.layoutParams as ConstraintLayout.LayoutParams
                        params.topMargin = 0
                        params.bottomMargin = 0
                        params.leftMargin = 0
                        params.rightMargin = 0
                        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                        binding!!.constraintLayout3.layoutParams = params
                        binding!!.videoFull.resizeMode =
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                        // player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(binding!!.constraintLayout3)
                        constraintSet.connect(
                            R.id.controls,
                            ConstraintSet.BOTTOM,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM,
                            0
                        )
                        //constraintSet.connect(R.id.video_full,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0);
                        constraintSet.applyTo(binding!!.constraintLayout3)
                        binding!!.controls.hide()
                        fullscreen = true
                    }
                }
            }
        })
        lockButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                lock = !lock
                if (lock) {
                    lockButton.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.main_bg_end_color, theme))
                    val currentOrientation = resources.configuration.orientation
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    } else {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    }
                } else {
                    lockButton.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.black, theme))
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                }
            }
        })
        //        ArrayList<String> list = new ArrayList<>();
//        if( AppClass.Companion.getFileList() != null && !AppClass.Companion.getFileList().isEmpty()){
//            for(Status a: AppClass.Companion.getFileList()){
//                list.add(Uri.fromFile(a.getFile()).toString());
//            }
//        }
//        else if( AppClass.Companion.getFile30List() != null && !AppClass.Companion.getFile30List().isEmpty()){
//            for(StatusDocFile a: AppClass.Companion.getFile30List()){
//
////                String[] listt = a.getFile().getUri().getPath().split("/document/primary:");
////
////                String toUse = listt[1];
////                Log.e("tree", "read30SDKWithUri: toUse : " + toUse);
////
////                toUse = "/storage/emulated/0/" + toUse;
////                File f = new File(toUse);
//
//                list.add(a.getFile().getUri().toString());
//            }
//        }

//        binding.imageView23.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(list.size() > position+1) {
//                            position = position + 1;
//
//                            binding.videoFull.setMediaController(mediaController);
//                            binding.videoFull.setVideoURI(Uri.parse(list.get(position)));
//                        }
//                    }
//                }
//        );
//        binding.imageView24.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(position-1 >= 0){
//                            position = position - 1;
//
//                            binding.videoFull.setMediaController(mediaController);
//                            binding.videoFull.setVideoURI(Uri.parse(list.get(position)));
//                        }
//                    }
//                }
//        );
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video1_jetay3.mp4");
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video2_sn3sek.mp4");
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video3_jcrsb3.mp4");
//
        //  loadMediaSliderView(list,"video",false,true,false,"Video-Slider","#000000",null,0);

//        final MediaController mediaController = new MediaController(FullScreenVideoActivity.this, true);
//
//        binding.videoFull.setOnPreparedListener(mp -> {
//            mp.start();
//            mediaController.show(0);
//            //mp.setLooping(true);
//        });
//
//        binding.videoFull.setMediaController(mediaController);
//        mediaController.setMediaPlayer(binding.videoFull);
//        binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
//        binding.videoFull.requestFocus();
//
//        ((ViewGroup) mediaController.getParent()).removeView(mediaController);
//
//        if (FullScreenVideoActivity.this.binding.videoViewWrapper.getParent() != null) {
//            FullScreenVideoActivity.this.binding.videoViewWrapper.removeView(mediaController);
//        }
//
//        FullScreenVideoActivity.this.binding.videoViewWrapper.addView(mediaController);
    }

    //    public static Uri getImageContentUri(Context context, File imageFile) {
    //        String filePath = imageFile.getAbsolutePath();
    //        Cursor cursor = context.getContentResolver().query(
    //                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    //                new String[] { MediaStore.Video.Media._ID },
    //                MediaStore.Video.Media.DATA + "=? ",
    //                new String[] { filePath }, null);
    //        if (cursor != null && cursor.moveToFirst()) {
    //            int a = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
    //            if(a >= 0) {
    //                int id = cursor.getInt(a);
    //                cursor.close();
    //                return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
    //            } else return null;
    //        } else {
    //            if (imageFile.exists()) {
    //                ContentValues values = new ContentValues();
    //                values.put(MediaStore.Video.Media.DATA, filePath);
    //                return context.getContentResolver().insert(
    //                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    //            } else {
    //                return null;
    //            }
    //        }
    //    }
    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
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
            if (status!!.file.delete()) {

            } else Toast.makeText(
                this@FullScreenVideoActivity,
                "Unable to Delete File",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            dialog.dismiss()
        }
        cancelDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onBackPressed() {
        file30List = null
        fileList = null

            finish()
            null
    }
}