package com.example.stickers.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Utils.FirebaseAnalytics
import com.google.android.gms.ads.nativead.NativeAd

abstract class BillingBaseActivity : AppCompatActivity() {
    // private lateinit var iapConnector: IapConnector


    @JvmField
    protected val remoteConfig = RemoteDateConfig()

    // private var isBillingClientConnected = false
    val sharedPref by lazy { SharedPreferenceData(this) }

    companion object {

        var admobNativeADNext: NativeAd? = null
        var isAdmobClicked = false
        var isApplovinClicked = false
        var isAnyVisible = false
        var wasAppinBack = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SplashActivity.fbAnalytics = FirebaseAnalytics(this)
    }
    var isCurrentVisible = false



    protected fun initMax(listener: () -> Unit) {
        //FB Ads

                listener.invoke()

    }


    override fun onResume() {
        isAnyVisible = true
        isCurrentVisible = true
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        isAnyVisible = false
        isCurrentVisible = false
        //billingClient?.endConnection()
    }


    protected open fun gotoNextScreen() {

    }


}