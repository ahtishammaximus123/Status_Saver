package com.example.stickers.ads


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.app.RemoteDateConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


open class InterAdmobClass {

    companion object {
        const val TAG = "Admob_Inter"

        @Volatile
        private var instance: InterAdmobClass? = null

        @JvmStatic
        var waitingTimeForAd = 85000L

        @JvmStatic
        var isInterstitialShown = false

        @JvmStatic
        var adFailedAttempts = 3

        @JvmStatic
        var adLoadAuto = true

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: InterAdmobClass().also { instance = it }
            }
    }

    var admobInterAd: InterstitialAd? = null
    private var adFailedCounter = 0
    private var isAdLoaded = false

    private fun isAdLoaded() = (isAdLoaded && (admobInterAd != null))


    fun loadInterstitialAd(
        context: Context,
        adInterId: String,
        loadListener: () -> Unit,
        listener: (Boolean) -> Unit
    ) {
        SplashActivity.fbAnalytics?.sendEvent("ADMOB_loadInterstitialAd")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adInterId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(ad: LoadAdError) {
                    val err = "ADMOB_Fail" + ad.message
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_onAdFailedToLoad", err)
                    Log.e("InterAd", "onAdFailedToLoad - $ad")
                    admobInterAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    adFailedCounter++
                    if (adFailedCounter < adFailedAttempts) {
                        loadInterstitialAd(context, adInterId,{}) {}
                    }
                    listener.invoke(false)
                    loadListener.invoke()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_onAdLoaded")
                    admobInterAd = ad
                    isAdLoaded = true
                    isInterstitialShown = false
                    Log.e("InterAd", "Loaded")
                    listener.invoke(true)
                }

            })
    }

    fun showInterstitialAd(
        activity: Activity,
        adInterId: String,
        listener: (Boolean) -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        admobInterAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_AdDismissed")
                    Log.e("InterAd", "onAdDismissedFullScreenContent")
                    admobInterAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    if (adLoadAuto) {
                        loadInterstitialAd(
                            activity,
                            RemoteDateConfig.remoteAdSettings.getAdmobInterId(),{}
                        ) {}
                    }
                    listener.invoke(true)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    val err = "ADMOB_Fail" + p0.message
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_onAdFailed2Show", err)
                    Log.e("InterAd", "onAdDismissedFullScreenContent")
                    isAdLoaded = false
                    isInterstitialShown = false
                    super.onAdFailedToShowFullScreenContent(p0)
                    listener.invoke(false)
                }

                override fun onAdImpression() {
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_IMP")
                    Log.e("InterAd", "onAdImpression")
                    super.onAdImpression()
                    isInterstitialShown = true
                    listenerImp?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_IMP")
                    Log.e("InterAd", "onAdShowedFullScreenContent")
                    super.onAdShowedFullScreenContent()

                    isInterstitialShown = false
                }

                override fun onAdClicked() {
                    SplashActivity.fbAnalytics?.sendEvent("ADMOB_Inter_AD_CLICK")
                    super.onAdClicked()
                }
            }

        if (isAdLoaded) {
            admobInterAd?.show(activity)
        } else {
            SplashActivity.fbAnalytics?.sendEvent("ADMOB_NOT_LOADED")
            listener.invoke(false)
        }
    }

    fun loadAndShowInter(
        activity: Activity,
        adInterId: String,
        dialog: Dialog? = null,
        listener: (Boolean) -> Unit
    ) {
        var isTimeUp = false
        var isAdShow = false
        if (activity.isNetworkAvailable()) {
            afterDelay(waitingTimeForAd) {
                dialog?.dismissDialog(activity)
                isTimeUp = true
                if (!isAdShow)
                    listener.invoke(false)
            }
            Log.e(TAG, "isAdLoaded ${isAdLoaded()}")
            if (isAdLoaded()) {
                dialog?.show()
                afterDelay(1500){
                    Log.e(TAG, "Already Loaded")
                    if (!isTimeUp && MainDashActivity.isActivityShown)
                        showInterstitialAd(activity, adInterId, {
                            SplashActivity.fbAnalytics?.sendEvent("ADMOB_SP_Show_$it")
                            isAdShow = true
                            listener.invoke(it)
                            dialog?.dismissDialog(activity)
                        }, {
                            isAdShow = true
                        })
                    else
                        listener.invoke(false)
                }
            } else {
                SplashActivity.fbAnalytics?.sendEvent("ADMOB_SP_NOT_LOADED")
                dialog?.show()
                loadInterstitialAd(activity, adInterId,{}) {
                    Log.e(TAG, "Load Ad $it")
                    val time = if(it) 500L else 2500L
                    afterDelay(time) {
                        dialog?.dismissDialog(activity)
                        if (!isTimeUp && MainDashActivity.isActivityShown)
                            showInterstitialAd(activity, adInterId, { s ->
                                Log.e(TAG, "isAdShown $s")
                                listener.invoke(s)

                            }, {
                                isAdShow = true
                            })
                        else
                            listener.invoke(false)
                    }
                }
            }
        } else {
            listener.invoke(false)
        }
    }

    private fun Dialog.dismissDialog(activity: Activity) {
        if (!activity.isDestroyed && !activity.isFinishing)
            if (isShowing) {
                try {
                    dismiss()
                } catch (e: IllegalArgumentException) {
                    // Do nothing.
                } catch (e: Exception) {
                    // Do nothing.
                }
            }
    }
}