package com.example.stickers.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast


import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import com.example.stickers.BuildConfig

import com.example.stickers.R
import com.example.stickers.app.RemoteDateConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView


@Keep
class NativeAdmobClass {
    private var isAdClicked = false
    companion object {
         var isNativeAdShowed1 = false
         var isNativeAdLoaded1 = false
        var splashNativeAd: NativeAd? = null
        var dashboardNativeAd: NativeAd? = null
        var innerNativeAd: NativeAd? = null


        @Volatile
        private var instance: NativeAdmobClass? = null
        fun getInstance() = instance ?: synchronized(this)
        { instance ?: NativeAdmobClass().also { instance = it } }
    }


    @SuppressLint("ResourceAsColor", "CutPasteId")
    fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)
        val adBodyText = adView.findViewById(R.id.ad_body)as TextView
        adBodyText.ellipsize = TextUtils.TruncateAt.MARQUEE
        adBodyText.isSingleLine = true
        adBodyText.marqueeRepeatLimit = -1 // repeat indefinitely
        adBodyText.isSelected = true
        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        if (RemoteDateConfig.remoteAdSettings.call_to_action_btn_color.value != "off") {
            val btn: Button = adView.findViewById(R.id.ad_call_to_action)
            btn.backgroundTintList = getCallActionBtnColor()
        }


        adView.iconView = adView.findViewById(R.id.ad_app_icon)
//        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
//        adView.storeView = adView.findViewById(R.id.ad_store)
//        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
      //  adView.mediaView?.mediaContent = nativeAd.mediaContent!!

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.GONE
        }
        else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }

//        if (nativeAd.advertiser == null) {
//            adView.advertiserView!!.visibility = View.INVISIBLE
//        }
//        else {
//            (adView.advertiserView as TextView).text = nativeAd.advertiser
//            adView.advertiserView!!.visibility = View.VISIBLE
//        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
//        val vc = nativeAd
    }

    private fun getCallActionBtnColor() = ColorStateList.valueOf(Color.parseColor(RemoteDateConfig.remoteAdSettings.call_to_action_btn_color.value))

    @SuppressLint("InflateParams", "VisibleForTests")
    fun loadSplashNative(
        activity: Activity,
        showListener: (NativeAd) -> Unit,
        failedListener: () -> Unit
    ) {


        val nativeAdId = RemoteDateConfig.remoteAdSettings.admob_splash_native_id.value
        if (nativeAdId.isEmpty()) {
            failedListener.invoke()
            return
        }
        val builder = AdLoader.Builder(activity, nativeAdId)
        builder.forNativeAd {
            splashNativeAd?.destroy()
            splashNativeAd = it
            Log.e("NativeAd", "loadAdNative: $it")
        }
        val videoOptions = VideoOptions.Builder()
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                splashNativeAd = null
                if (!isAdClicked) {
                    isAdClicked = true
                    showToastInter("loadSplashNative ad clicked 1", activity)
                    //firebaseAnalytic?.sendEventAnalytic("QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobClick1", "onAdClicked:")
                    loadSplashNative(
                        activity,
                        {
                            showListener.invoke(it)
                        },
                        failedListener
                    )
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                isNativeAdLoaded1 = true
                splashNativeAd?.let { showListener.invoke(it) }
                showToastInter(" loadSplashNative ad loaded 1", activity)
                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobLoad1","onAdLoaded:" )
            }

            override fun onAdImpression() {
                super.onAdImpression()
                isAdClicked = false
                showToastInter("loadSplashNative ad impression 1", activity)

                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobImpression1","onAdImpression:")
                isNativeAdShowed1 = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    "domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
                Log.e("NativeAd", "failAdNative: $error")
                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobFailed1","onAdFailedToLoad:")
                isNativeAdLoaded1 = false
                failedListener.invoke()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    @SuppressLint("InflateParams")
    fun loadDashboardNative(
        activity: Activity,
        showListener: (NativeAd) -> Unit,
        failedListener: () -> Unit
    ) {


        val nativeAdId = RemoteDateConfig.remoteAdSettings.admob_dash_native_id.value
        if (nativeAdId.isEmpty()) {
            failedListener.invoke()
            return
        }
        val builder = AdLoader.Builder(activity, nativeAdId)
        builder.forNativeAd {
            dashboardNativeAd?.destroy()
            dashboardNativeAd = it
            Log.e("NativeAd", "loadAdNative: $it")
        }
        val videoOptions = VideoOptions.Builder()
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                dashboardNativeAd = null
                if (!isAdClicked) {
                    isAdClicked = true
                    showToastInter("ad clicked loadDashboardNative", activity)
                    //firebaseAnalytic?.sendEventAnalytic("QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobClick1", "onAdClicked:")
                    loadDashboardNative(
                        activity,
                        {
                            showListener.invoke(it)
                        },
                        failedListener
                    )
                }
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                isNativeAdLoaded1 = true
                dashboardNativeAd?.let { showListener.invoke(it) }
                showToastInter("ad loaded loadDashboardNative", activity)
                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobLoad1","onAdLoaded:" )
            }

            override fun onAdImpression() {
                super.onAdImpression()
                isAdClicked = false
                showToastInter("ad impression loadDashboardNative", activity)

                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobImpression1","onAdImpression:")
                isNativeAdShowed1 = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    "domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
                Log.e("NativeAd", "failAdNative: $error")
                //firebaseAnalytic?.sendEventAnalytic( "QRS_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_NativeAdmobFailed1","onAdFailedToLoad:")
                isNativeAdLoaded1 = false
                failedListener.invoke()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    fun showNative(
        nativeAd: NativeAd,
        frameLayout: FrameLayout,
        layoutInflater: LayoutInflater,
        @LayoutRes layoutRes: Int,
        listener: (NativeAd) -> Unit
    ) {
        val adView = layoutInflater.inflate(layoutRes, null) as NativeAdView
        populateUnifiedNativeAdView(nativeAd, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
        listener.invoke(nativeAd)
    }

    /** create for show toasts in test mode.
     * Note: --> comment before release **/
    var toast: Toast? = null
    private fun showToastInter(value: String, context: Context) {
        toast?.cancel()
        toast = Toast.makeText(context, "Native $value", Toast.LENGTH_LONG)
        toast?.setGravity(Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, 0, 0)
        if (BuildConfig.DEBUG) {
            toast?.show()
        }
    }
}