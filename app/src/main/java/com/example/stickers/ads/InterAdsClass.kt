package com.example.stickers.ads

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.stickers.ads.AppOpenClass.Companion.isShowingOpenAppAd
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class InterAdsClass {
    private var counter: Int=0
    private var adLoadCounter: Int=0
    var adloaded = false

    companion object {



        @Volatile
        private var instance: InterAdsClass? = null
        fun getInstance() = instance ?: synchronized(this)
        { instance ?: InterAdsClass().also { instance = it } }

        var isInterstitialShown = false
        @kotlin.jvm.JvmField
        var currentInterAd: InterstitialAd? = null

        var mInterstitialAd1: InterstitialAd? = null
        var mInterstitialAd2: InterstitialAd? = null
        var mInterstitialAd3: InterstitialAd? = null
        var mInterstitialAd4: InterstitialAd? = null
        var mInterstitialAd5: InterstitialAd? = null
        @Volatile
        var interIsShowing: Boolean? = false
    }

    fun initializeAdmob(activity: Activity) {
        MobileAds.initialize(activity) {}
    }


    fun loadInterAdWithId1(
        activity: Activity,
        loadListener: () -> Unit,
        failListener: () -> Unit,
    ) {
        if (activity.isNetworkAvailable() && remoteAdSettings.admob_Inter_ID1.value.isNotEmpty() && !isShowingOpenAppAd && activity.verifyInstallerId(
            ) && activity.isAlreadyPurchased() != true
        ) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                remoteAdSettings.admob_Inter_ID1.value,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(TAG, it) }
                        Log.d("interAd", "Inter Ad failed with ID1.")
                        mInterstitialAd1 = null
                        isInterstitialShown = false
                        counter++
                        adRefill()
                        failListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_failed_ID1_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("interAd", "Inter Ad Loaded with ID1.")
                        counter++
                        mInterstitialAd1 = interstitialAd
                        adRefill()
                        adloaded = true
                        loadListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_loaded_ID1_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                })

        } else {
            mInterstitialAd1=null
            counter++
            failListener.invoke()
            isInterstitialShown = false
        }

    }

    fun loadInterAdWithId2(
        activity: Activity,
        loadListener: () -> Unit,
        failListener: () -> Unit,
    ) {
        if (activity.isNetworkAvailable() && remoteAdSettings.admob_Inter_ID2.value.isNotEmpty() && !isShowingOpenAppAd && activity.verifyInstallerId(

            ) && !activity.isAlreadyPurchased()
        ) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                remoteAdSettings.admob_Inter_ID2.value,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(TAG, it) }
                        Log.d("interAd", "Inter Ad failed with ID2.")
                        mInterstitialAd2 = null
                        isInterstitialShown = false
                        counter++
                        adRefill()
                        failListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_failed_ID2_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("interAd", "Inter Ad Loaded with ID2.")
                        counter++
                        mInterstitialAd2 = interstitialAd
                        adRefill()
                        adloaded = true
                        loadListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_loaded_ID2_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                })

        } else {
            mInterstitialAd2=null
            counter++
            failListener.invoke()
            isInterstitialShown = false
        }

    }


    fun loadInterAdWithId3(
        activity: Activity,
        loadListener: () -> Unit,
        failListener: () -> Unit,
    ) {
        if  (activity.isNetworkAvailable() && remoteAdSettings.admob_Inter_ID2.value.isNotEmpty() && !isShowingOpenAppAd && activity.verifyInstallerId(

            ) && !activity.isAlreadyPurchased()
        ) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                remoteAdSettings.admob_Inter_ID3.value,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(TAG, it) }
                        Log.d("interAd", "Inter Ad failed with ID3.")
                        mInterstitialAd3 = null
                        isInterstitialShown = false
                        counter++
                        adRefill()
                        failListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_failed_ID3_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("interAd", "Inter Ad Loaded with ID3.")
                        counter++
                        mInterstitialAd3 = interstitialAd
                        adRefill()
                        adloaded = true
                        loadListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_loaded_ID3_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                })

        } else {

            mInterstitialAd3=null
            counter++
            failListener.invoke()
            isInterstitialShown = false
        }

    }

    fun loadInterAdWithId4(
        activity: Activity,
        loadListener: () -> Unit,
        failListener: () -> Unit,
    ) {
        if  (activity.isNetworkAvailable() && remoteAdSettings.admob_Inter_ID2.value.isNotEmpty() && !isShowingOpenAppAd && activity.verifyInstallerId(

            ) && !activity.isAlreadyPurchased()
        ) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                remoteAdSettings.admob_Inter_ID4.value,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(TAG, it) }
                        Log.d("interAd", "Inter Ad failed with ID4.")
                        mInterstitialAd4 = null
                        isInterstitialShown = false
                        loadInterAdWithId5(activity,{},{})
                        counter++
                        adRefill()
                        failListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_failed_ID4_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("interAd", "Inter Ad Loaded with ID4.")
                        mInterstitialAd4 = interstitialAd
                        adloaded = true
                        counter++
                        adRefill()
                        loadListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_loaded_ID4_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                })

        } else {
            failListener.invoke()
            isInterstitialShown = false
        }

    }

    fun loadInterAdWithId5(
        activity: Activity,
        loadListener: () -> Unit,
        failListener: () -> Unit,
    ) {
        if  (activity.isNetworkAvailable() && remoteAdSettings.admob_Inter_ID2.value.isNotEmpty() && !isShowingOpenAppAd && activity.verifyInstallerId(

            ) && !activity.isAlreadyPurchased()
        ) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                remoteAdSettings.admob_Inter_ID5.value,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d(TAG, it) }
                        Log.d("interAd", "Inter Ad failed with ID5.")
                        mInterstitialAd5 = null
                        isInterstitialShown = false
                        if(adLoadCounter<1)
                        {
                            adLoadCounter++
                            loadInterAdWithId5(activity,{},{})
                        }

                        counter++
                        adRefill()
                        failListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_failed_ID5_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("interAd", "Inter Ad Loaded with ID5.")
                        mInterstitialAd5 = interstitialAd
                        counter++
                        adRefill()
                        adloaded = true
                        loadListener.invoke()
//                        firebaseAnalytic?.sendEventAnalytic(
//                            "inter_loaded_ID5_${BuildConfig.VERSION_CODE}",
//                            "request_id"
//                        )
                    }

                })
        } else {
            failListener.invoke()
            isInterstitialShown = false
        }
    }

    fun showInterAd123(
        activity: Activity,
        onAdDismissedListener: () -> Unit,
        failedListener: () -> Unit,
        showedListener: () -> Unit
    ) {
        interIsShowing=true
        if (currentInterAd != null && !isShowingOpenAppAd) {
            currentInterAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d("interAd", "Ad was clicked.")
                    isInterstitialShown = false
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "inter_AdClicked_${BuildConfig.VERSION_CODE}",
//                        "request_id2"
//                    )
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d("interAd", "Ad dismissed fullscreen content.")
                    interIsShowing=false
                    currentInterAd = null
                    isInterstitialShown = false
                    if(mInterstitialAd3==null)
                    {
                        loadInterAdWithId4(activity,{},{})
                    }

                    adRefill()
                    onAdDismissedListener.invoke()
                    Handler(Looper.getMainLooper()).postDelayed(
                        { isInterstitialShown = false },
                        200
                    )
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "inter_AdDismissed_${BuildConfig.VERSION_CODE}",
//                        "request_id2"
//                    )

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    interIsShowing=false
                    Log.e("inter", "Ad failed to show fullscreen content.")
                 //   currentInterAd = null
                    isInterstitialShown = false
                   // adRefill()
                    failedListener.invoke()
                    Handler(Looper.getMainLooper()).postDelayed(
                        { isInterstitialShown = false },
                        200
                    )
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "inter_failed_Show_${BuildConfig.VERSION_CODE}",
//                        "request_id2"
//                    )
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d("interAd", "Ad recorded an impression.")
                    showedListener.invoke()
                    isInterstitialShown = true
//                    firebaseAnalytic?.sendEventAnalytic(
//                        "inter_AdImpression_${BuildConfig.VERSION_CODE}",
//                        "request_id2"
//                    )
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d("interAd", "Ad showed fullscreen content.")
                    showedListener.invoke()

                    isInterstitialShown = true
               //     firebaseAnalytic?.sendEventAnalytic(
//                        "inter_onAdShowed_${BuildConfig.VERSION_CODE}",
//                        "request_id2"
//                    )
                }

            }

            if (adloaded && !isShowingOpenAppAd) {
                Log.d("interAd", "AppOpen Ad Visibility ${isShowingOpenAppAd}")
                currentInterAd?.show(activity)

            } else {
                currentInterAd = null

                adRefill()
                failedListener.invoke()
            }


        } else {
            currentInterAd = null
            adRefill()
            onAdDismissedListener.invoke()
            Handler(Looper.getMainLooper()).postDelayed({ isInterstitialShown = false }, 200)
        }

    }
    fun adRefill()
    {
        if(currentInterAd==null&& counter>=3)
        {
            if(mInterstitialAd1!=null)
            {
                currentInterAd = mInterstitialAd1
                mInterstitialAd1=null
                Log.e("interAdRefill", "Inter Refilled with InterID1")

            }
            else if(mInterstitialAd2!=null)
            {
                currentInterAd= mInterstitialAd2
                mInterstitialAd2=null
                Log.e("interAdRefill", "Inter Refilled with InterID2")
            }
            else if(mInterstitialAd3!=null)
            {
                currentInterAd= mInterstitialAd3
                mInterstitialAd3=null
                Log.e("interAdRefill", "Inter Refilled with InterID3")
            }
            else if(mInterstitialAd4!=null)
            {
                currentInterAd= mInterstitialAd4
                mInterstitialAd4=null
                Log.e("interAdRefill", "Inter Refilled with InterID4")
            }
            else if(mInterstitialAd5!=null)
            {
                currentInterAd= mInterstitialAd5
                mInterstitialAd5=null
                Log.e("interAdRefill", "Inter Refilled with InterID5")
            }
            else{
                currentInterAd=null
            }


        }

    }


}
