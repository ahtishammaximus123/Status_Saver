package com.example.stickers.ads

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.example.stickers.BuildConfig
import com.example.stickers.app.Constants
import com.example.stickers.app.SharedPreferenceData


var toast: Toast? = null

fun Context.showToast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast?.show()
}

fun Context.verifyInstallerId(): Boolean {
    if (BuildConfig.DEBUG)
        return true
    val validInstallers = listOf("com.android.vending", "com.google.android.feedback")
    return getInstallerPackageName() != null && validInstallers.contains(getInstallerPackageName())
}

@Suppress("DEPRECATION")
fun Context.getInstallerPackageName(): String? {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return packageManager.getInstallSourceInfo(packageName).installingPackageName
        return packageManager.getInstallerPackageName(packageName)
    }
    return null
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

fun afterDelay(delayInTime: Long, listener: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        listener.invoke()
    }, delayInTime)
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun singleClick(view: View) {
    view.disable()
    afterDelay(1500) {
        view.enable()
    }
}

fun Context.isAlreadyPurchased(): Boolean {
    return SharedPreferenceData(this).getBoolean(
        Constants.KEY_IS_PURCHASED,
        false
    )
}

fun Context.isPrivacyChecked(): Boolean {
    return SharedPreferenceData(this).getBoolean(
        Constants.KEY_IS_PRIVACY,
        false
    )
}