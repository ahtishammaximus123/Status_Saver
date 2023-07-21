package com.example.stickers.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import org.jetbrains.annotations.NotNull


class AppOpen(private val appClass: Application) :
    Application.ActivityLifecycleCallbacks, LifecycleObserver, LifecycleEventObserver {

    private val tag = "Admob_AppOpen"

    companion object {
        @Volatile
        var appOpenAd: AppOpenAd? = null
        var isShowingAd = false
        var interPreLoadAd = true
    }

    private var currentActivity: Activity? = null
    private var fullScreenContentCallback: FullScreenContentCallback? = null


    init {
        appClass.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (remoteAdSettings.appOpen.value == "on")
            fetchAd {}
    }

    /**
     * Request an ad
     */
    fun fetchAd(listener: (Boolean) -> Unit) {
        Log.e(tag, "fetchAd " + isAdAvailable())
        // Have unused ad, no need to fetch another.
        if (appOpenAd != null)
            return
        if (isAdAvailable()) {
            return
        }

        /*
          Called when an app open ad has failed to load.
          @param loadAdError the error.
         */
        // Handle the error.
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */

            override fun onAdLoaded(ad: AppOpenAd) {
                SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Loaded")
                appOpenAd = ad
                listener.invoke(true)
                Log.e(tag, "loaded")
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Failed2Load")
                appOpenAd = null
                listener.invoke(false)
                Log.e(tag, "error $loadAdError")
            }
        }
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            appClass, remoteAdSettings.getAdmobAppOpen(), request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }


    private fun showAdIfAvailable(listener: (Boolean) -> Unit) {
        if (interPreLoadAd)
            return
        if (InterAdmobClass.isInterstitialShown)
            return
        if (isAdAvailable()) {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Imp")
                    appOpenAd = null
                    isShowingAd = false
                    if (remoteAdSettings.appOpen.value == "on")
                        fetchAd {}
                    listener.invoke(true)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Failed2Show")
                    isShowingAd = false
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdImpression() {
                    SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Imp")
                    isShowingAd = true
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Show")
                    isShowingAd = true
                }

                override fun onAdClicked() {
                    SplashActivity.fbAnalytics?.sendEvent("AppOPEN_Click")
                    super.onAdClicked()
                }
            }

            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            currentActivity?.let {
                appOpenAd?.show(it)
            } ?: kotlin.run {
                listener.invoke(false)
            }
        } else {

            if (remoteAdSettings.appOpen.value == "on")
                fetchAd {}
            listener.invoke(false)
        }
    }

    private fun isAdAvailable(): Boolean {
        return !isShowingAd && appOpenAd != null
    }

    /**
     * Creates and returns ad request.
     */
    @NotNull
    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onStateChanged(p0: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            currentActivity?.let {
                if (it !is SplashActivity && it !is FullScreenVideoActivity)
                    showAdIfAvailable {}
            }
        }
    }
}