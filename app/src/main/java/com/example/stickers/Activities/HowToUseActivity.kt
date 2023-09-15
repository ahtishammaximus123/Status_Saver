package com.example.stickers.Activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.stickers.Activities.SplashActivity.Companion.fbAnalytics
import com.example.stickers.R
import com.example.stickers.ads.AdmobCollapsibleBanner
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadAdaptiveBanner
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.dialog.ProgressDialog

class HowToUseActivity : BillingBaseActivity() {
    private lateinit var imgBackArrow: ImageView
    private lateinit var connect: ConstraintLayout
    private var txtconnect: TextView? = null
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = ProgressDialog(this, "Loading...")
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_how_to_use)
        if (fbAnalytics != null) fbAnalytics!!.sendEvent("how2UseActivity_Open")

         imgBackArrow = findViewById<ImageView>(R.id.back_arrow)
         connect = findViewById<ConstraintLayout>(R.id.connect_screen_mirroring)
         txtconnect = findViewById<TextView>(R.id.txtconnect)

        connect.setOnClickListener {
            try {
                startActivity(Intent("android.settings.CAST_SETTINGS"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Casting is not supported!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        imgBackArrow.setOnClickListener { finish() }
    }


    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    private fun checkDisplay() {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        if (displayManager.displays.size >= 2) {
            txtconnect!!.text = getString(R.string.connected)
        } else {
            txtconnect!!.text = getString(R.string.connect)
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityRunning=false
    }
    override fun onResume() {
        super.onResume()
        isActivityRunning=true
        showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_mirroring_scr_inter_ad.value){}
        checkDisplay()
        val frame = findViewById<FrameLayout>(R.id.how_to_use_native_small)
        loadNativeAd(this,frame!!,
            remoteAdSettings.admob_native_mirroring_ad.value,layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        val frameBanner = findViewById<FrameLayout>(R.id.banner_adview)
        loadAdaptiveBanner(
            this,
            frameBanner,
            remoteAdSettings.admob_adaptive_mirroring_scr_banner_ad.value
        )

    }
    private fun showInterAd(activity: Activity, status:String, functionalityListener: () -> Unit) {


        if (status=="on"&& adisready=="notshowed"&& InterAdsClass.currentInterAd !=null) {

            loadingDialog?.show()
            Handler(Looper.getMainLooper()).postDelayed({
                if(isActivityRunning)
                {
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


}