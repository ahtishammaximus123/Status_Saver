package com.example.stickers.ads

//
//import android.app.Activity
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.annotation.LayoutRes
//import com.applovin.mediation.MaxAd
//import com.applovin.mediation.MaxError
//import com.applovin.mediation.nativeAds.MaxNativeAdListener
//import com.applovin.mediation.nativeAds.MaxNativeAdLoader
//import com.applovin.mediation.nativeAds.MaxNativeAdView
//import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
//import com.example.stickers.Activities.SplashActivity
//import com.example.stickers.R
//import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
//
//open class MaxNativeAd {
//
//    var maxNativeAd0: MaxNativeAdView? = null
//    var maxNativeAd1: MaxNativeAdView? = null
//    private var adFailedCounter = 1
//
//    private fun getNativeId() = remoteAdSettings.getMaxNativeId()
//
//    companion object {
//        @Volatile
//        private var instance: MaxNativeAd? = null
//        @Volatile
//        var isNativeAdmobFailed = false
//        fun getInstance() =
//            instance ?: synchronized(this) {
//                instance ?: MaxNativeAd().also { instance = it }
//            }
//    }
//
//    private fun Activity.loadMaxNativeAd(@LayoutRes layoutRes: Int): MaxNativeAdView {
//        val binder: MaxNativeAdViewBinder =
//            MaxNativeAdViewBinder.Builder(layoutRes)
//                .setTitleTextViewId(R.id.title_text_view)
//                .setBodyTextViewId(R.id.body_text_view)
//                .setAdvertiserTextViewId(R.id.advertiser_textView)
//                .setIconImageViewId(R.id.icon_image_view)
//                .setMediaContentViewGroupId(R.id.media_view_container)
////                .setOptionsContentViewGroupId(R.id.options_view)
//                .setCallToActionButtonId(R.id.cta_button)
//                .build()
//        return MaxNativeAdView(binder, this)
//    }
//
//    fun loadMaxNativeAd0(
//        activity: Activity,
//        @LayoutRes layoutRes: Int,
//        adId: String,
//        nativeAdContainerView: ViewGroup? = null
//    ) {
//        if (activity.isAlreadyPurchased() || adId.isEmpty()) {
//            nativeAdContainerView?.removeAllViews()
//            nativeAdContainerView?.visibility = View.GONE
//            return
//        }
//        Log.e("NativeMax", "loadMaxNativeAd")
//        val nativeAdLoader = MaxNativeAdLoader(adId, activity)
//        nativeAdLoader.setRevenueListener { }
//        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
//            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd) {
//                Log.e("NativeMax", "Loaded")
//                SplashActivity.fbAnalytics?.sendEvent("_Max_onNativeAdLoaded")
//                SplashActivity.fbAnalytics?.sendEvent("_Max_Network", nativeAd.toString())
//                nativeAdView?.callToActionButton?.backgroundTintList =
//                    getCallActionBtnColor()
//                maxNativeAd0 = nativeAdView
//
//                nativeAdContainerView?.visibility = View.VISIBLE
//                nativeAdContainerView?.removeAllViews()
//                nativeAdContainerView?.addView(nativeAdView)
//            }
//
//            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
//                Log.e("NativeMax", "error : $error")
//                SplashActivity.fbAnalytics?.sendEvent("_Max_onNativeAdLoadFailed", error.toString())
//                maxNativeAd0 = null
//                nativeAdContainerView?.visibility = View.GONE
//                adFailedCounter++
//                // Native ad load failed.
//                // AppLovin recommends retrying with exponentially higher delays up to a maximum delay.
//                showNativeAd0(activity, nativeAdContainerView)
//            }
//
//            override fun onNativeAdClicked(nativeAd: MaxAd) {
//                Log.e("NativeMax", "onNativeAdClicked")
//                SplashActivity.fbAnalytics?.sendEvent("_Max_onNativeAdClicked")
//                loadMaxNativeAd0(activity, layoutRes, adId, nativeAdContainerView)
//            }
//        })
//        nativeAdLoader.loadAd(activity.loadMaxNativeAd(layoutRes))
//    }
//
//    fun loadMaxNativeAd1(
//        activity: Activity,
//        @LayoutRes layoutRes: Int,
//        adId: String
//    ) {
//        if (activity.isAlreadyPurchased() || adId.isEmpty()) {
//            return
//        }
//        Log.e("NativeMax", "loadMaxNativeAd")
//        val nativeAdLoader = MaxNativeAdLoader(adId, activity)
//        nativeAdLoader.setRevenueListener { }
//        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
//            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd) {
//                Log.e("NativeMax", "Loaded")
//                activity.javaClass
//                SplashActivity.fbAnalytics?.sendEvent("Max_onNativeAdLoaded")
//                SplashActivity.fbAnalytics?.sendEvent("Max_N1","$nativeAd")
//                nativeAdView?.callToActionButton?.backgroundTintList =
//                    getCallActionBtnColor()
//                maxNativeAd1 = nativeAdView
//            }
//
//            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
//                Log.e("NativeMax", "error : $error")
//                SplashActivity.fbAnalytics?.sendEvent("Max_onNativeAdLoadFailed", error.toString())
//                maxNativeAd1 = null
//                adFailedCounter++
//                // Native ad load failed.
//                // AppLovin recommends retrying with exponentially higher delays up to a maximum delay.
//                showNativeAd1(activity, null)
//            }
//
//            override fun onNativeAdClicked(nativeAd: MaxAd) {
//                Log.e("NativeMax", "onNativeAdClicked")
//                SplashActivity.fbAnalytics?.sendEvent("Max_onNativeAdClicked")
//                loadMaxNativeAd1(activity, layoutRes, adId)
//            }
//        })
//        nativeAdLoader.loadAd(activity.loadMaxNativeAd(layoutRes))
//    }
//
//    fun showNativeAd1(
//        activity: Activity, nativeAdView: ViewGroup?
//    ) {
//        if (activity.isAlreadyPurchased()) {
//            nativeAdView?.removeAllViews()
//            nativeAdView?.visibility = View.GONE
//            return
//        }
//        maxNativeAd1?.let { native ->
//            (native.parent as ViewGroup?)?.removeView(native)
//            nativeAdView?.removeAllViews()
//            nativeAdView?.addView(native)
//        } ?: kotlin.run {
//            if (adFailedCounter < 1)
//                loadMaxNativeAd1(
//                    activity,
//                    R.layout.max_native_small,
//                    getNativeId(),
//                )
//            else {
//                nativeAdView?.removeAllViews()
//                nativeAdView?.visibility = View.GONE
//            }
//        }
//    }
//
//    fun showNativeAd0(
//        activity: Activity, nativeAdView: ViewGroup?
//    ) {
//        if (activity.isAlreadyPurchased()) {
//            nativeAdView?.removeAllViews()
//            nativeAdView?.visibility = View.GONE
//            return
//        }
//        maxNativeAd0?.let { native ->
//            (native.parent as ViewGroup?)?.removeView(native)
//            nativeAdView?.removeAllViews()
//            nativeAdView?.addView(native)
//        } ?: kotlin.run {
//            if (adFailedCounter < 1)
//                loadMaxNativeAd0(
//                    activity,
//                    R.layout.max_native_small,
//                    getNativeId(),
//                    nativeAdView
//                )
//            else {
//                nativeAdView?.removeAllViews()
//                nativeAdView?.visibility = View.GONE
//            }
//        }
//    }
//
//    fun showNative(
//        activity: Activity,
//        adFrameLarge: FrameLayout,
//        @LayoutRes layoutRes: Int, type: Int
//    ) {
//        SplashActivity.fbAnalytics?.sendEvent("Max_onNativeShow")
//        adFrameLarge.visibility = View.VISIBLE
//        if (type == 1) {
//            showNativeAd0(activity, adFrameLarge)
////            loadMaxNativeAd1(activity, layoutRes, getNativeId())
//        } else if (type == 2) {
//            showNativeAd1(activity, adFrameLarge)
////            loadMaxNativeAd0(activity, layoutRes, getNativeId())
//        }
//    }
//}