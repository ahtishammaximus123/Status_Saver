package com.example.stickers.Activities

import ai.lib.billing.BillingClientConnectionListener
import ai.lib.billing.DataWrappers
import ai.lib.billing.IapConnector
import ai.lib.billing.PurchaseServiceListener
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.sticker.LoadSticker
import com.example.stickers.R
import com.example.stickers.Utils.FirebaseAnalytics
import com.example.stickers.ads.*
import com.example.stickers.app.*
import com.example.stickers.app.Constants.Companion.subsList
import com.example.stickers.dialog.ProgressDialog
import com.example.stickers.fcm.FcmLib
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.gson.Gson
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BillingBaseActivity() {

    companion object {
        @JvmStatic
        var fbAnalytics: FirebaseAnalytics? = null
        var isActivityShown = false
        var isNativeAdShowed1 = false
        var isNativeAdLoaded1 = false

    }


    var skiped = false
    var duration = false


    private val timer = Timer()
    private var isRemoteFetched = false
    private var isMoveToNext = false
    private var remoteTime = 0L


    override fun onResume() {
        super.onResume()
        isActivityShown = true

        if (remoteTime > 3500)
            gotoNextScreen()
    }

    override fun onPause() {
        super.onPause()
        isActivityShown = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        isActivityShown = true
        MainDashActivity.isInterShown = false
        initBillingListener()
        FcmLib.setupFCM(this, "/topics/$packageName")
        fbAnalytics = FirebaseAnalytics(this)
        fbAnalytics?.sendEvent("SplashActivity_Open")
        LoadSticker.loadStickers(this)
        initMax {
//            AppLovinSdk.getInstance(this).showMediationDebugger()
        }

        resetAds()

        Ads.loadBannerAd(applicationContext)
        MobileAds.initialize(this)

        val firstTime = SharedPreferenceData(this).getBoolean(
            Constants.FIRST_TIME,
            true
        )
        isRemoteFetched = false
        isMoveToNext = false
        remoteTime = 0
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                remoteTime += 1000
                if (remoteTime > 12000 && !isRemoteFetched) {
                    if (firstTime) {
                        val layout = findViewById<ConstraintLayout>(R.id.layout)
                        val loading = findViewById<LottieAnimationView>(R.id.splash_loading_bar)
                        runOnUiThread {
                            layout.beVisible()
                            loading.beGone()
                        }
                    } else
                        gotoNextScreen()
                    timer.cancel()
                    Log.e("RemoteConfig2", "remoteTime; $remoteTime")
                }
            }
        }, 1000, 1000)


        remoteConfig.getSplashRemoteConfig { remoteAdSettings: RemoteAdSettings? ->
            fbAnalytics?.sendEvent("remote_fetch$remoteTime")
            val data = Gson().toJson(remoteAdSettings)
            Log.e("RemoteConfig", data)
            AppOpen(application)


            RemoteDateConfig.remoteAdSettings.apply {
                if (inter_splash_ad.value != "off"
                ) {
//                    InterAdmobClass.getInstance().loadAndShowInter(this@SplashActivity, RemoteDateConfig.remoteAdSettings.getAdmobSplashInterId(),ProgressDialog(this@SplashActivity)) {}

                }


            }

            if (firstTime) {
                val layout = findViewById<ConstraintLayout>(R.id.layout)
                val loading = findViewById<LottieAnimationView>(R.id.splash_loading_bar)
                runOnUiThread {
                    layout.beVisible()
                    loading.beGone()
                }
            } else
                gotoNextScreen()

        }
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.animationView)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val layout = findViewById<ConstraintLayout>(R.id.layout)
        val btnStart = findViewById<Button>(R.id.btnStart)
        checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                btnStart.alpha = 1.0f
            } else {
                btnStart.alpha = 0.65f
            }
        }
        btnStart.setOnClickListener { view: View? ->
            /*if (checkBox.isChecked) {
                SharedPreferenceData(this).putBoolean(Constants.KEY_IS_PRIVACY, true)
            } else this.showToast("Kindly Accept the Terms.")*/

            SharedPreferenceData(this).putBoolean(
                Constants.FIRST_TIME,
                false
            )
            gotoNextScreen()
        }
        val txtTerms = findViewById<TextView>(R.id.txtTerms)
        val txtPolicy = findViewById<TextView>(R.id.txtPolicy)
        txtPolicy.setOnClickListener { view: View? ->
            try {
                val intentRateApp =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy)))
                startActivity(intentRateApp)
            } catch (e: Exception) {
            }
        }
        txtTerms.setOnClickListener { view: View? -> }
//        Handler().postDelayed({
//            duration = true
//            if (skiped) {
//                lottieAnimationView.visibility = View.GONE
//                layout.visibility = View.VISIBLE
//                val isChecked =
//                    SharedPreferenceData(this).getBoolean(Constants.KEY_IS_PRIVACY, false)
//                checkBox.isChecked = isChecked
//                if (isChecked) btnStart.alpha = 1.0f
//            }
//        }, 6000)
/*
        val rb = findViewById<RadioButton>(R.id.spinnerc)
        val rb1 = findViewById<RadioButton>(R.id.spinnerc2)
        val spinner = findViewById<ConstraintLayout>(R.id.spinner)
        val rv = findViewById<ConstraintLayout>(R.id.spinnerList)
        val skip = findViewById<TextView>(R.id.textView28)
        val lang = findViewById<ConstraintLayout>(R.id.lang)
        val titlesplash = findViewById<TextView>(R.id.textView11)
        val icon = findViewById<ImageView>(R.id.imageView2)
        val s = sharedPref.getString("apppackage", null)
        if (s == null) {
            WAoptions.appPackage = "com.whatsapp"
            SharedPreferenceData(this).putString(
                "apppackage",
                WAoptions.appPackage
            )
        } else {
            skiped = true
            lang.visibility = View.GONE
            icon.visibility = View.VISIBLE
            titlesplash.visibility = View.VISIBLE
            lottieAnimationView.visibility = View.GONE
            layout.visibility = View.VISIBLE
            val isChecked =
                SharedPreferenceData(this).getBoolean(Constants.KEY_IS_PRIVACY, false)
            checkBox.isChecked = isChecked
            if (isChecked) btnStart.alpha = 1.0f
        }
        skip.setOnClickListener {
            skiped = true
            lang.visibility = View.GONE
            icon.visibility = View.VISIBLE
            titlesplash.visibility = View.VISIBLE
            if (duration) {
                lottieAnimationView.visibility = View.GONE
                layout.visibility = View.VISIBLE
                val isChecked =
                    SharedPreferenceData(this).getBoolean(Constants.KEY_IS_PRIVACY, false)
                checkBox.isChecked = isChecked
                if (isChecked) btnStart.alpha = 1.0f
            }
        }
        spinner.setOnClickListener {
            skiped = true
            rb.isChecked = true
            val toast = Toast.makeText(this, "WA Selected!", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            if (duration) {
                lottieAnimationView.visibility = View.GONE
                layout.visibility = View.VISIBLE
                val isChecked =
                    SharedPreferenceData(this).getBoolean(Constants.KEY_IS_PRIVACY, false)
                checkBox.isChecked = isChecked
                if (isChecked) btnStart.alpha = 1.0f
            }
        }
        rv.setOnClickListener {
            rb1.isChecked = true
            rb.isChecked = false
        }
        spinner.setOnClickListener {
            rb1.isChecked = false
            rb.isChecked = true
        }
        rb1.setOnCheckedChangeListener { compoundButton, b ->
            rb.isChecked = !b
            if (b) {
                val toast = Toast.makeText(this, "WB Selected!", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                WAoptions.appPackage = "com.whatsapp.w4b"
                SharedPreferenceData(this).putString(
                    "apppackage",
                    WAoptions.appPackage
                )
            }
            skip.performClick()
        }
        rb.setOnCheckedChangeListener { compoundButton, b ->
            rb1.isChecked = !b
            if (b) {
                val toast = Toast.makeText(this, "WA Selected!", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                WAoptions.appPackage = "com.whatsapp"
                SharedPreferenceData(this).putString(
                    "apppackage",
                    WAoptions.appPackage
                )
            }
            skip.performClick()
        }
*/
        layout.visibility = View.GONE
    }

    private fun resetAds() {
        InterAdmobClass.getInstance().admobInterAd = null
        MainDashActivity.nativeAdNew2=null
        MainDashActivity.nativeAdNew1=null
    }

    override fun gotoNextScreen() {
        val firstTime = SharedPreferenceData(this).getBoolean(
            Constants.FIRST_TIME,
            true
        )
        if (firstTime) {
            val layout = findViewById<ConstraintLayout>(R.id.layout)
            val loading = findViewById<LottieAnimationView>(R.id.splash_loading_bar)
            runOnUiThread {
                layout.beVisible()
                loading.beGone()
            }
        } else {
            if (!isActivityShown)
                return
            if (isMoveToNext)
                return
            isMoveToNext = true

            val time = if (remoteTime < 3000) 500L else 0
            afterDelay(time) {
                if (isActivityShown) {
                    runOnUiThread {
                        val mainIntent = Intent(this@SplashActivity, MainDashActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
        }
    }

    private lateinit var iapConnector: IapConnector
    private var isBillingClientConnected = false

    private fun initBillingListener() {
        iapConnector = IapConnector(
            context = this,
            consumableKeys = subsList,
            enableLogging = true
        )

        iapConnector.addBillingClientConnectionListener(object : BillingClientConnectionListener {
            override fun onConnected(status: Boolean, billingResponseCode: Int) {
                Log.d(
                    "KSA",
                    "This is the status: $status and response code is: $billingResponseCode"
                )
                isBillingClientConnected = status
            }
        })


        iapConnector.addPurchaseListener(object : PurchaseServiceListener {

            override fun onEmptyPurchasedList() {
                Log.e("tagV4*", "onEmptyPurchasedList")
                SharedPreferenceData(this@SplashActivity).putBoolean(
                    Constants.KEY_IS_PURCHASED,
                    false
                )
            }

            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.SkuDetails>) {
                // list of available products will be received here, so you can update UI with prices if needed
                Log.e("tagV4*", "iapKeyPrices $iapKeyPrices")
                val prices = HashMap<String, String>()
                subsList.forEach {
                    val currency = iapKeyPrices[it]?.priceCurrencyCode ?: ""
                    val price = iapKeyPrices[it]?.price ?: ""
                    prices[it] = "$currency $price"
                }
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {

            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered upon fetching owned subscription upon initialization
                Log.e("tagV4*", "onSubscriptionRestored $purchaseInfo")
                when (purchaseInfo.sku) {
                    subsList[0] -> {
                        Log.e("tagV4*", "onSubscriptionRestored $purchaseInfo")
                        SharedPreferenceData(this@SplashActivity).putBoolean(
                            Constants.KEY_IS_PURCHASED,
                            true
                        )
                    }
                }
            }
        })
    }
}