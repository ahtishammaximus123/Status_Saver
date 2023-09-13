package com.example.stickers.Utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.example.stickers.R
import com.example.stickers.app.SharedPreferenceData
import java.io.File

class AppCommons {
    companion object {
//        fun NavController.refreshCurrentFragment(){
//            val id = currentDestination?.id
//            popBackStack(id!!,true)
//            navigate(id)
//        }

        fun Context.ShowWAppDialog(view: View, uri: Uri, isVideo: Boolean){
            val wrapper: Context = ContextThemeWrapper(this, R.style.PopupMenu2)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.inflate(R.menu.status_menu_inside)
            val link = "http://play.googlee.com/store/apps/details?id=$packageName"
            val shareMessage = "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_ba ->
                    if (isAppInstalled(applicationContext, "com.whatsapp.w4b")) {
                        Log.e("WhatsAppicon", "1: Clicked" )
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        if(isVideo)
                        shareIntent.type = "video/*"
                        else     shareIntent.type = "image/*"
                        shareIntent.setPackage("com.whatsapp.w4b");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(shareIntent)
                        try {
                            Log.e("WhatsAppicon", "2: Clicked" )
                       //     val launchIntent = packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")
                            startActivity(shareIntent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                "Whatsapp Business not installed!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                else{
                        Toast.makeText(
                            applicationContext,
                            "Whatsapp Business not installed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                R.id.action_wa -> if (isAppInstalled(applicationContext, "com.whatsapp")) {
                    try {
                        Log.e("WhatsAppicon", "3: Clicked" )
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        if(isVideo)
                            shareIntent.type = "video/*"
                        else     shareIntent.type = "image/*"
                        shareIntent.setPackage("com.whatsapp")
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(shareIntent)
                        //val launchIntent = packageManager.getLaunchIntentForPackage("com.whatsapp")
                       // startActivity(launchIntent)
                    } catch (e: Exception) {
                        Log.e("WhatsAppicon", "4: Clicked" )
                        Toast.makeText(
                            applicationContext,
                            "Whatsapp not installed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        "Whatsapp not installed!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            false
        })
        view!!.post(object : Runnable {
            override fun run() {
                popupMenu.show()
            }
        })
        }
        fun Context.initDash(sharedPref : SharedPreferenceData){
            val s = sharedPref.getString("apppackage", null)
            if(s == null) {
                if (isAppInstalled(applicationContext, "com.whatsapp")) {
                    WAoptions.appPackage = "com.whatsapp"
                } else if (isAppInstalled(applicationContext, "com.whatsapp.w4b")) {
                    WAoptions.appPackage = "com.whatsapp.w4b"
                }
            }else{
                if (s == "com.whatsapp") {
                    WAoptions.appPackage = "com.whatsapp"
                } else if (s =="com.whatsapp.w4b") {
                    WAoptions.appPackage = "com.whatsapp.w4b"
                }
            }
            if (WAoptions.appPackage === "com.whatsapp") {
                if (Build.VERSION.SDK_INT == 29) {
                    Common.STATUS_DIRECTORY = Common.STATUS_DIRECTORY_NEW
                } else Common.STATUS_DIRECTORY = Common.STATUS_DIRECTORY_1
            } else {
                if (Build.VERSION.SDK_INT == 29) {
                    Common.STATUS_DIRECTORY = Common.STATUS_DIRECTORY_NEW_2
                } else Common.STATUS_DIRECTORY = Common.STATUS_DIRECTORY_2
            }
            Common.APP_DIR = getExternalFilesDir("StatusDownloader")!!
                .absolutePath + File.separator + "wa"
            if (WAoptions.appPackage == "com.whatsapp") {
                Common.APP_DIR = getExternalFilesDir("StatusDownloader")!!
                    .absolutePath + File.separator + "wa"
            } else {
                Common.APP_DIR = getExternalFilesDir("StatusDownloader")!!
                    .absolutePath + File.separator + "wab"
            }
        }
        fun isAppInstalled(ctx: Context, packageName: String): Boolean {
            val pm = ctx.packageManager
            val appInstalled: Boolean = try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            return appInstalled
        }
    }
}