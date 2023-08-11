package com.example.stickers.ads

import android.app.Activity
import android.os.SystemClock
import android.view.View
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.R
import com.example.stickers.app.RemoteAdDetails
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.dialog.ProgressDialog

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beInVisible() {
    visibility = View.INVISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

var lastClickTime: Long = 0
fun Activity.showInterAd(
    remote: RemoteAdDetails,
    listener: () -> Unit
) {
    if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
        return
    }
    lastClickTime = SystemClock.elapsedRealtime()

    if (isAlreadyPurchased() || AppOpen.isShowingAd)
        listener.invoke()
    else
        when (remote.value) {
            "on", "admob_applovin" -> {
                InterAdmobClass.getInstance()
                    .showInterstitialAd(this, remoteAdSettings.getAdmobInterId(), {
                        if (!it)
                           // showInterMax(remoteAdSettings.getMaxInterId(), listener)
                        else
                            listener.invoke()
                    })
            }
            "admob" -> {
                InterAdmobClass.getInstance()
                    .showInterstitialAd(this, remoteAdSettings.getAdmobInterId(), {
                        listener.invoke()
                    })
            }

            else -> {
                listener.invoke()
            }
        }
}

fun Activity.showInterDemandAdmob(
    remote: RemoteAdDetails,AdID:String,
    listener: () -> Unit
) {
    if (isAlreadyPurchased() || AppOpen.isShowingAd)
        listener.invoke()
    else
        when (remote.value) {
            "on", "admob_applovin" -> {
                InterAdmobClass.getInstance()
                    .loadAndShowInter(this, AdID,ProgressDialog(this)
                    ) {
                        if (!it && MainDashActivity.isActivityShown) {

                        } else
                            listener.invoke()
                    }
            }
            "admob" -> {
                InterAdmobClass.getInstance()
                    .loadAndShowInter(
                        this, AdID
                    ) {
                        listener.invoke()
                    }
            }

            else -> {
                InterAdmobClass.getInstance()
                    .loadInterstitialAd(this,AdID,{}) {}
                listener.invoke()
            }
        }
}