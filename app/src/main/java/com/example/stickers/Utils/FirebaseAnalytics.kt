package com.example.stickers.Utils

import android.content.Context
import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.annotation.Size
import com.example.stickers.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

@Keep
class FirebaseAnalytics(context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    /**
     * Command to Enable Analytics in Debug Mode
     * Command : adb shell setprop debug.firebase.analytics.app packageName
     * */

    fun sendEvent(@NonNull @Size(min = 1L, max = 30L) eventName: String) {
        val identifier = "SS_" + BuildConfig.VERSION_CODE + "_"
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.VALUE, "true")
        firebaseAnalytics.logEvent("$identifier$eventName", bundle)
    }

    fun sendEvent(@NonNull @Size(min = 1L, max = 30L) eventName: String, eventValue: String) {
        val identifier = "SS_" + BuildConfig.VERSION_CODE + "_"
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.VALUE, eventValue)
        firebaseAnalytics.logEvent("$identifier$eventName", bundle)
    }

}