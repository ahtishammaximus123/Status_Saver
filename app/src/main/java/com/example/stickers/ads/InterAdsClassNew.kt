package com.example.stickers.ads

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class InterAdsClassNew {


    var adloaded=false

    companion object {
        @Volatile
        private var instance: InterAdsClassNew? = null
        fun getInstance() = instance ?: synchronized(this)
        { instance ?: InterAdsClassNew().also { instance = it } }
        public var isInterstitialShown=false
         var isInterstitialShownApp=false
         var mInterstitialAd: InterstitialAd? = null
    }

    fun initializeAdmob(activity: Activity) {
        MobileAds.initialize(activity) {}
    }


    fun loadInterAds(activity: Activity, interAdId: String,loadListener : ()-> Unit, failListener : ()-> Unit,)
    {
        if(activity.isNetworkAvailable() && interAdId.isNotEmpty()&& !activity.isAlreadyPurchased() && mInterstitialAd==null  )
        {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(activity,interAdId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d(TAG, it) }
                    Log.d("interAd", "Intr Ad failed .")
                    mInterstitialAd = null
                    isInterstitialShownApp=false
                    isInterstitialShown=false
                    loadListener.invoke()

                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("interAd", "Intr Ad was loaded.")
                    isInterstitialShown=true
                    isInterstitialShownApp=true
                    mInterstitialAd = interstitialAd
                     adloaded = true
                    loadListener.invoke()

                }

            })

        }
        else{
            failListener.invoke()
        isInterstitialShown=false
        }

    }


    fun showInterAd(activity: Activity,funcListener : ()-> Unit, failListener : ()-> Unit,showedListener:()-> Unit)
    {
        if(activity.isNetworkAvailable() && mInterstitialAd!=null )
        {
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("interAd", "Ad was clicked.")
                isInterstitialShown=false


            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("interAd", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
                isInterstitialShown=false
                funcListener.invoke()
                Handler(Looper.getMainLooper()).postDelayed({ isInterstitialShown=false},200)

            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e("inter", "Ad failed to show fullscreen content.")
                mInterstitialAd = null
                isInterstitialShown=false
                funcListener.invoke()
                Handler(Looper.getMainLooper()).postDelayed({ isInterstitialShown=false},200)

            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("interAd", "Ad recorded an impression.")
                showedListener.invoke()
                isInterstitialShown=true

            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("interAd", "Ad showed fullscreen content.")
                showedListener.invoke()

//                if(mediaPlayer!=null)
//                {
//                    mediaPlayer?.stop()
//                    mediaPlayer=null
//                }
                isInterstitialShown=true

            }

        }

            if (adloaded ) {

                mInterstitialAd?.show(activity)

            } else {
                funcListener.invoke()
            }


        }
        else{
            funcListener.invoke()
            Handler(Looper.getMainLooper()).postDelayed({ isInterstitialShown=false},200)
        }

    }



    }
