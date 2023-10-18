package com.example.stickers.Activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.stickers.Activities.SplashActivity.Companion.fbAnalytics
import com.example.stickers.R
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.databinding.ActivityShareBinding

class ShareActivity : BillingBaseActivity() {
    private var binding: ActivityShareBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        binding = ActivityShareBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        if (fbAnalytics != null) fbAnalytics!!.sendEvent("SharedActivity: Open")
        val link = "http://play.google.com/store/apps/details?id=$packageName"
        binding!!.shareback.setOnClickListener { onBackPressed() }
        binding!!.imgGmailShare.setOnClickListener { shareURL("com.google.android.gm") }
        binding!!.imgSkypeShare.setOnClickListener { shareURL("com.skype.raider") }
        binding!!.imgShareShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
            )
            startActivity(Intent.createChooser(intent, "Share APP"))
        }
    }

    fun shareURL(packageName: String) {
        if (fbAnalytics != null) fbAnalytics!!.sendEvent("Share_$packageName")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage(packageName)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val link = "http://play.google.com/store/apps/details?id=" + getPackageName()
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
        )
        startActivity(Intent.createChooser(intent, "Share APP"))
    }

    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    override fun onBackPressed() {
        finish()
        //        InterstitialAdAppLovin.Companion.showCallback(this,  new AdCallback() {
//
//            @Override
//            public void onAdFailed() {
//                finish();
//            }
//
//            @Override
//            public void onAdClosed() {
//                finish();
//            }
//        },RemoteDateConfig.getRemoteAdSettings().getInter_share_back().getValue());
    }

    override fun onResume() {
        super.onResume()
        val frame = findViewById<FrameLayout>(R.id.share_activity_native)
        loadNativeAd(this,frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_share_screen_ad.value,layoutInflater,R.layout.gnt_medium_template_view,{ },{})
    }
}