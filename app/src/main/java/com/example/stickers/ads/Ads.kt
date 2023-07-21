package com.example.stickers.ads


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout

class Ads {


    fun showBannerAd(
        appContext: Context,
        adLyt1: ConstraintLayout?,
        adProgressBar1: ProgressBar?,
        adContent1: LinearLayout?
    ) {
        adLyt1?.visibility = View.GONE
    }

    companion object {
        fun loadBannerAd(appContext: Context) {
        }
    }
}

class RemoteValues {
    companion object {
        var am: String = "on"
        var off: String = "off"
        var inter: String = "inter"
        var open: String = "appopen"
        var applovin: String = "applovin"
        var admob: String = "admob"
    }
}
