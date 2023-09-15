package com.example.stickers.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.example.stickers.ads.InterAdsClass.Companion.interIsShowing
import com.example.stickers.ads.InterAdsClass.Companion.isInterstitialShown
import com.example.stickers.app.RemoteDateConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.appopen.AppOpenAd


class AppOpenClass(private val appClass:Application): Application.ActivityLifecycleCallbacks, LifecycleObserver,LifecycleEventObserver {
    companion object {
        var isShowingOpenAppAd = false
        var applicationInPause = false
        var applicationClosed = ""
    }

    var isAdShowAlways = false
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var appOpenActivity: Activity? = null
    var permit=true
    init {
        appClass.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    fun setAdShownStatus(status: Boolean) {
        isAdShowAlways = status
    }
    fun fetchAd(listener: (Boolean) -> Unit) {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            Log.e("appOpen", "fetchAd " + isAdAvailable())
            return
        }

        val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.e("appOpen", "onAdLoaded: ")
                    //    Splash.firebaseAnalytic?.sendEventAnalytic("admob_app_open_onadloaded ${BuildConfig.VERSION_CODE}", "request_id2")
                    appOpenAd = ad
                    listener.invoke(true)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("appOpen", "onAdFailedToLoad: ")
                    //   Splash.firebaseAnalytic?.sendEventAnalytic("admob_app_open_onadfailedtoload ${BuildConfig.VERSION_CODE}", "request_id2")
                    appOpenAd = null
                    listener.invoke(false)
                }

            }
        val request: AdRequest = getAdRequest()
        AppOpenAd.load(
            appClass, RemoteDateConfig.remoteAdSettings.admob_open_id.value, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    private var fullScreenContentCallback: FullScreenContentCallback? = null

    fun showAdIfAvailable(listener: (Boolean) -> Unit) {
        if (isAdAvailable()) {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    //Splash.firebaseAnalytic?.sendEventAnalytic("admob_app_opn_onadismissedFullscrCont ${BuildConfig.VERSION_CODE}", "request_id2")



                    Log.e("appOpen", "onAdDismissedFullScreenContent: ")
                    appOpenAd = null
                    isShowingOpenAppAd = false
                    if (isAdShowAlways) {
                        fetchAd {}
                    }
                    listener.invoke(true)
                }

                override fun onAdClicked() {
                    super.onAdClicked()

                }

                override fun onAdShowedFullScreenContent() {
                    Log.e("appOpen", "onAdShowedFullScreenContent: ")
                //    Splash.firebaseAnalytic?.sendEventAnalytic("admob_app_open_onadshowfullscr_cont_ ${BuildConfig.VERSION_CODE}", "request_id2")

                    isShowingOpenAppAd = true
                }
            }

            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
            appOpenActivity?.let {
                appOpenAd?.show(it)
            } ?: kotlin.run {
                listener.invoke(false)
            }
        } else {
            if (isAdShowAlways) {
                fetchAd {}
            }
            listener.invoke(false)
        }
    }

    private fun isAdAvailable(): Boolean {
        return !isShowingOpenAppAd && appOpenAd != null && !isInterstitialShown
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }


    override fun onActivityStarted(p0: Activity) {
        Log.e("appOpen", "onActivityStarted: ")
        currentActivity = p0

        applicationClosed =p0.toString()
    }

    override fun onActivityResumed(p0: Activity) {
        Log.e("appOpen", "onActivityResumed: ")
        currentActivity = p0
        applicationInPause =false
        appOpenActivity=p0
        applicationClosed =p0.toString()

    }

    override fun onActivityPostResumed(activity: Activity) {
        super.onActivityPostResumed(activity)


    }
    override fun onActivityPaused(p0: Activity) {
        applicationInPause =true
        appOpenActivity=p0
        currentActivity=null
        Log.e("appOpen", "onActivityPaused: ")
    }

    override fun onActivityStopped(p0: Activity) {
        isInterstitialShown=false
        appOpenActivity=p0
        currentActivity=null
        Log.e("appOpen", "onActivityStopped: ")

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }
    override fun onActivityDestroyed(activity: Activity)
    {
        Log.e("appOpen", "onActivityDestroyed: ")
      //  isInterstitialShown=false
    }





    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
    {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (event == Lifecycle.Event.ON_START) {
            appOpenActivity?.let {
                applicationInPause =false
             //   it !is SplashActivity
                if ( !isInterstitialShown&& interIsShowing==false) {
                    applicationInPause =false//
                    if (isAdShowAlways && RemoteDateConfig.remoteAdSettings.appOpenAd.value == "on"&& !isInterstitialShown)
                    {
                        showAdIfAvailable {}
                    }
                    else {
                        isShowingOpenAppAd =false
                    }
               }
            }
        } else if(event == Lifecycle.Event.ON_PAUSE) {
                applicationInPause =true
                }
            else if(event == Lifecycle.Event.ON_STOP) {
                applicationInPause =true
            }

            },50)




    }
}