package com.example.stickers.ads

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.nativeAdNew1
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.nativeAdNew2
import com.example.stickers.R
import com.example.stickers.app.RemoteAdDetails
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView


const val TAG = "Admob_Native"
fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

    adView.apply {
//        mediaView = findViewById(R.id.ad_media)
        // Set other ad assets.
        headlineView = findViewById(R.id.ad_headline)
        bodyView = findViewById(R.id.ad_body)
        callToActionView = findViewById(R.id.ad_call_to_action)
        iconView = findViewById(R.id.ad_app_icon)
//        priceView = findViewById(R.id.ad_price)
        starRatingView = findViewById(R.id.ad_stars)
//        storeView = findViewById(R.id.ad_store)
       // advertiserView = findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (headlineView as TextView?)?.text = nativeAd.headline
        nativeAd.mediaContent?.let {
            mediaView?.setMediaContent(it)
//            mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        }

        val adBodyText = adView.findViewById(R.id.ad_body)as TextView
        adBodyText.ellipsize = TextUtils.TruncateAt.MARQUEE
        adBodyText.isSingleLine = true
        adBodyText.marqueeRepeatLimit = -1 // repeat indefinitely
        adBodyText.isSelected = true
    }

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    nativeAd.body?.let { body ->
        adView.bodyView?.beVisible()
        (adView.bodyView as? TextView?)?.text = body
    } ?: run {
        adView.bodyView?.beInVisible()
    }
    nativeAd.callToAction?.let { callToAction ->
        adView.callToActionView?.beVisible()
        (adView.callToActionView as? Button?)?.text = callToAction
        (adView.callToActionView as? Button?)?.backgroundTintList =
            getCallActionBtnColor()
    } ?: run {
        adView.callToActionView?.beInVisible()
    }

    nativeAd.icon?.let {
        adView.iconView?.beVisible()
        (adView.iconView as? ImageView?)?.setImageDrawable(it.drawable)
    } ?: run {
        adView.iconView?.beInVisible()
    }

    nativeAd.price?.let { price ->
        adView.priceView?.beVisible()
        (adView.priceView as? TextView?)?.text = price
    } ?: run {
        adView.priceView?.beInVisible()
    }

    nativeAd.store?.let { store ->
        adView.storeView?.beVisible()
        (adView.storeView as? TextView?)?.text = store
    } ?: run {
        adView.storeView?.beGone()
    }


    nativeAd.starRating?.let { starRating ->
        adView.starRatingView?.beVisible()
        (adView.starRatingView as? RatingBar?)?.rating = starRating.toFloat()
    } ?: run {
        adView.starRatingView?.beGone()
    }

//    nativeAd.advertiser?.let { advertiser ->
//        adView.advertiserView?.beVisible()
//        (adView.advertiserView as? TextView?)?.text = advertiser
//    } ?: run {
//        adView.advertiserView?.beGone()
//    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeAd.mediaContent?.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    vc?.apply {
        if (hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }
    }
}



fun Activity.loadNativeAdmob(
    adFrame: FrameLayout?,
    adNativeId: String,
    @LayoutRes layoutRes: Int,
    successListener: ((NativeAd) -> Unit)? = null,
    failedListener: ((error: String) -> Unit)? = null
) {

    if (adNativeId.isEmpty()){
        failedListener?.invoke("Ad Id is Empty")
        return
    }
    val builder = AdLoader.Builder(this, adNativeId)
    builder.forNativeAd { nativeAd ->
        // OnUnifiedNativeAdLoadedListener implementation.
        // If this callback occurs after the activity is destroyed, you must call
        // destroy and return or you may get a memory leak.
        val activityDestroyed: Boolean = isDestroyed
        if (activityDestroyed || isFinishing || isChangingConfigurations) {
            nativeAd.destroy()

            return@forNativeAd
        }
        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
//        currentNativeAd?.destroy()
//        currentNativeAd = nativeAd
        val adView = layoutInflater.inflate(layoutRes, null, false) as NativeAdView
        populateNativeAdView(nativeAd, adView)
        adFrame?.removeAllViews()
        adFrame?.addView(adView)
        nativeAdNew1=nativeAd
        successListener?.invoke(nativeAd)
        Log.e(TAG, "NativeAd loaded.")
    }

    val videoOptions = VideoOptions.Builder()
        .setStartMuted(true)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {

            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Failed2Load", loadAdError.toString())
            val error =
                "${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
            Log.e(TAG, error)
            nativeAdNew1=null
            failedListener?.invoke(error)
        }

        override fun onAdLoaded() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_load")
            Log.e("NativeAd", "onAdLoaded: " )
            super.onAdLoaded()
        }

        override fun onAdImpression() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_Imp")
            super.onAdImpression()
        }

        override fun onAdClicked() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_click")
            super.onAdClicked()
            nativeAdNew1=null
            loadNativeAdmob(adFrame, adNativeId, layoutRes)
        }
    }).build()

    val testDevices: MutableList<String> = ArrayList()
    testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

    val requestConfiguration = RequestConfiguration.Builder()
        .setTestDeviceIds(listOf("0fff05d1-36df-4310-b68b-10ca1ad1abe1"))
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    adLoader.loadAd(AdRequest.Builder().build())
}

fun Activity.loadNativeAdmob2(
    adFrame: FrameLayout?,
    adNativeId: String,
    @LayoutRes layoutRes: Int,
    successListener: ((NativeAd) -> Unit)? = null,
    failedListener: ((error: String) -> Unit)? = null
) {

    if (adNativeId.isEmpty()){
        failedListener?.invoke("Ad Id is Empty")
        return
    }
    val builder = AdLoader.Builder(this, adNativeId)
    builder.forNativeAd { nativeAd ->
        // OnUnifiedNativeAdLoadedListener implementation.
        // If this callback occurs after the activity is destroyed, you must call
        // destroy and return or you may get a memory leak.
        val activityDestroyed: Boolean = isDestroyed
        if (activityDestroyed || isFinishing || isChangingConfigurations) {
            nativeAd.destroy()

            return@forNativeAd
        }
        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
//        currentNativeAd?.destroy()
//        currentNativeAd = nativeAd
        val adView = layoutInflater
            .inflate(layoutRes, null, false) as NativeAdView
        populateNativeAdView(nativeAd, adView)
        adFrame?.removeAllViews()
        adFrame?.addView(adView)
        nativeAdNew2=nativeAd
        successListener?.invoke(nativeAd)
        Log.e(TAG, "NativeAd loaded.")
    }

    val videoOptions = VideoOptions.Builder()
        .setStartMuted(true)
        .build()

    val adOptions = NativeAdOptions.Builder()
        .setVideoOptions(videoOptions)
        .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {

            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Failed2Load", loadAdError.toString())
            val error =
                "${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}"
            Log.e(TAG, error)
            nativeAdNew2=null
            failedListener?.invoke(error)
        }

        override fun onAdLoaded() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_load")
            Log.e("NativeAd", "onAdLoaded: " )
            super.onAdLoaded()
        }

        override fun onAdImpression() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_Imp")
            super.onAdImpression()
        }

        override fun onAdClicked() {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_Native_Ad_click")
            super.onAdClicked()
            nativeAdNew2=null
            loadNativeAdmob2(adFrame, adNativeId, layoutRes)
        }
    }).build()

    val testDevices: MutableList<String> = ArrayList()
    testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

    val requestConfiguration = RequestConfiguration.Builder()
        .setTestDeviceIds(listOf("0fff05d1-36df-4310-b68b-10ca1ad1abe1"))
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    adLoader.loadAd(AdRequest.Builder().build())
}






fun Activity.loadNativeAdmob(
    view: ViewGroup,
    adFrame: FrameLayout,
    @LayoutRes layoutRes: Int,
    @LayoutRes maxLayoutRes: Int,
    type: Int,
    model: RemoteAdDetails,
    successListener: ((NativeAd) -> Unit)? = null,
    failedListener: (() -> Unit)? = null,
    adId: String = remoteAdSettings.getAdmobNativeId()
) {
    if (isAlreadyPurchased()) {
        view.removeAllViews()
        view.beGone()
        return
    }
    when (model.value) {
        "admob", "on" -> {
            view.beVisible()

            if(type==2)
            {
                if(remoteAdSettings.admob_native_id_2.value.isNotEmpty())
                {
                    if(nativeAdNew2==null)
                    {
                        loadNativeAdmob2(adFrame, adId, layoutRes, {
                            successListener?.invoke(it)
                            nativeAdNew2=it
                        }, {
                            failedListener?.invoke()
//                MaxNativeAd.getInstance().showNative(
//                    this, adFrame, maxLayoutRes, type
//                )
                        })
                    }
                    else{
                        val adView = layoutInflater.inflate(layoutRes, null, false) as NativeAdView
                        populateNativeAdView(nativeAdNew2!!, adView)
                        adFrame.removeAllViews()
                        adFrame.addView(adView)
                    }
                }
                else{
                    view.removeAllViews()
                    view.beGone()
                }


            }
            else{
                if(remoteAdSettings.admob_native_id_1.value.isNotEmpty()) {
                    if (nativeAdNew1 == null) {
                        loadNativeAdmob(adFrame, adId, layoutRes, {
                            successListener?.invoke(it)
                            nativeAdNew1 = it
                        }, {
                            failedListener?.invoke()
//                MaxNativeAd.getInstance().showNative(
//                    this, adFrame, maxLayoutRes, type
//                )
                        })
                    } else {
                        val adView = layoutInflater
                            .inflate(layoutRes, null, false) as NativeAdView
                        populateNativeAdView(nativeAdNew1!!, adView)
                        adFrame.removeAllViews()
                        adFrame.addView(adView)

                    }
                }
                else{
                    view.removeAllViews()
                    view.beGone()
                }


            }

        }

        else -> {
            view.removeAllViews()
            view.beGone()
        }
    }
}

fun getCallActionBtnColor() =
    ColorStateList.valueOf(
        Color.parseColor(
            remoteAdSettings.call_to_action_btn_color.value.ifEmpty { "#FFAA20" })
    )