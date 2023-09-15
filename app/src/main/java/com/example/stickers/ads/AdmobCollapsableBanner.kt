package com.example.stickers.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout

import androidx.annotation.Keep
import com.example.stickers.app.RemoteDateConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds


@Keep
class AdmobCollapsibleBanner {

    companion object {

        var isAdLoadedCollapsible = false
        var adViewBanner: AdView? = null
        var adViewCollapsible: AdView? = null

        var adaptiveBannerAd:AdView?=null

        var smallBanner:AdView?=null

        private var instance: AdmobCollapsibleBanner? = null
        fun getInstance() = instance ?: synchronized(this)
        { instance ?: AdmobCollapsibleBanner().also { instance = it } }
    }


    fun loadAdmobCollapsible(
        activity: Activity,
        adContainer: FrameLayout,
        collapsibleBannerId: String,
        frameLayoutForBanner: FrameLayout,
        loadListener: () -> Unit,
        failListener: () -> Unit,
        onAdClosed: () -> Unit,
        onAdOpened: () -> Unit
    ) {

        if (collapsibleBannerId.isNotEmpty()) {


            adViewCollapsible = AdView(activity)
            adViewCollapsible?.adUnitId = collapsibleBannerId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                adViewCollapsible!!.setAdSize(adSize(activity, adContainer))
            else adViewCollapsible!!.setAdSize(getAdSize(activity, adContainer))


            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            val adRequest = AdRequest.Builder().addNetworkExtrasBundle(
                com.google.ads.mediation.admob.AdMobAdapter::class.java, extras
            ).build()


            adViewCollapsible?.loadAd(adRequest)
            adViewCollapsible?.adListener = object : AdListener() {
                override fun onAdClicked() {
                    isAdLoadedCollapsible=false
                    adViewCollapsible=null
                    Log.e("AdmobCollapsibleBanner", "Ad Clicked")
                    loadAdmobCollapsible(
                        activity,
                        adContainer,
                        collapsibleBannerId,
                        frameLayoutForBanner,
                        loadListener,
                        failListener,
                        onAdClosed,
                        onAdOpened
                    )
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_clicked",
//                        "onAdLoaded:"
//                    )

                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.

                    Log.e("AdmobCollapsibleBanner", "Ad Closed")
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_closed",
//                        "onAdLoaded:"
//                    )

                    onAdClosed.invoke()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.
                    adViewCollapsible=null
                    isAdLoadedCollapsible=false
                    Log.e("AdmobCollapsibleBanner", "Ad Error: ${adError.message}")
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_fail",
//                        "onAdLoaded:"
//                    )



                    failListener.invoke()
                }

                override fun onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                    Log.e("AdmobCollapsibleBanner", "Ad Impression")
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_impression",
//                        "onAdLoaded:"
//                    )


                }

                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    isAdLoadedCollapsible = true
                    loadListener.invoke()
                    Log.e("AdmobCollapsibleBanner", "Ad loaded")
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_loaded",
//                        "onAdLoaded:"
//                    )
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.

                    Log.e("AdmobCollapsibleBanner", "onAdOpened")
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "AVD_${BuildConfig.VERSION_CODE}_${activity::class.java.simpleName}_collapsible_opened",
//                        "onAdLoaded:"
//                    )
                    onAdOpened.invoke()

                }
            }
        }

    }

    private fun adSize(activity: Activity, adContainer: FrameLayout): AdSize {
        var adWidth = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds

            var adWidthPixels = adContainer.width.toFloat()

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = bounds.width().toFloat()
            }

            val density = activity.resources.displayMetrics.density
            adWidth = (adWidthPixels / density).toInt()

        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)

    }

    private fun getAdSize(mActivity: Activity, adContainer: FrameLayout): AdSize {
        val display = mActivity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth)
    }



    fun showCollapsibleAd(frameLayout: FrameLayout) {
        try {
            if (adViewCollapsible != null) {
                frameLayout.visibility = View.VISIBLE

                frameLayout.removeAllViews()

                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )

                val adViewParent = adViewCollapsible?.parent as? ViewGroup
                adViewParent?.removeView(adViewCollapsible)

                frameLayout.addView(adViewCollapsible, params)
            }
            else{
                frameLayout.visibility=View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun loadAdmobAdaptiveBanner(
        activity: Activity,
        bannerAdview: FrameLayout,
        failListener: () -> Unit
    )
    {
        MobileAds.initialize(activity) { }
        adaptiveBannerAd = AdView(activity)
        val adRequest = AdRequest.Builder().build()
        val adSize = getAdSizeAdaptive(activity)
        adaptiveBannerAd?.setAdSize(adSize)
        val adID=RemoteDateConfig.remoteAdSettings.admob_adaptive_banner_id.value
        adaptiveBannerAd?.adUnitId =  adID
        adaptiveBannerAd?.loadAd(adRequest)

        adaptiveBannerAd?.adListener = object : AdListener() {
            override fun onAdClicked() {
                adaptiveBannerAd = null
                loadAdmobAdaptiveBanner(activity,  bannerAdview){failListener.invoke()}
                Log.e("BannerAdLoad", "onAdClicked:AdaptiveBanner ")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adaptiveBannerAd = null
                if(smallBanner==null)
                {
                    loadAdmobSmallBanner(activity, bannerAdview)
                }
                else{
                    showSmallBanner(bannerAdview)
                }

                Log.e("BannerAdLoad", "AdFailedToLoad:AdaptiveBanner ")
            }

            override fun onAdImpression() {
                Log.e("BannerAdLoad", "onAdImpression:AdaptiveBanner ")
            }

            override fun onAdLoaded() {
                showAdaptiveBanner(bannerAdview)

            }

        }
    }
    fun loadAdmobSmallBanner(
        activity: Activity,
        bannerAdview: FrameLayout,
    )
    {
        MobileAds.initialize(activity) { }
        smallBanner = AdView(activity)
        val adRequest = AdRequest.Builder().build()
        smallBanner?.setAdSize(AdSize.BANNER)
        val adID=RemoteDateConfig.remoteAdSettings.admob_small_banner_id.value
        smallBanner?.adUnitId = adID
        smallBanner?.loadAd(adRequest)

        smallBanner?.adListener = object : AdListener() {
            override fun onAdClicked() {
                smallBanner = null
                loadAdmobSmallBanner(activity,bannerAdview)
                Log.e("BannerAdLoad", "onAdClicked: SmallBanner ")
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("BannerAdLoad", "onAdFailedToLoad:  SmallBanner")

                smallBanner = null
            }

            override fun onAdImpression() {
                Log.e("BannerAdLoad", "onAdImpression: SmallBanner ")
            }

            override fun onAdLoaded() {

                showSmallBanner(bannerAdview)
                Log.e("BannerAdLoad", "onAdLoaded:  SmallBanner")

            }

        }


    }
    private fun showSmallBanner(frameLayout: FrameLayout) {

        try {
            frameLayout.visibility = View.VISIBLE
            frameLayout.removeAllViews()
            val adViewParent = smallBanner?.parent as? ViewGroup
            adViewParent?.removeView(smallBanner)
            frameLayout.addView(smallBanner)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun showAdaptiveBanner(frameLayout: FrameLayout) {

        try {
            frameLayout.visibility = View.VISIBLE
            frameLayout.removeAllViews()
            val adViewParent = adaptiveBannerAd?.parent as? ViewGroup
            adViewParent?.removeView(adaptiveBannerAd)
            frameLayout.addView(adaptiveBannerAd)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun getAdSizeAdaptive(activity: Activity): AdSize {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val widthPixels = outMetrics.widthPixels
        val density = outMetrics.density

        val adWidth = (widthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}