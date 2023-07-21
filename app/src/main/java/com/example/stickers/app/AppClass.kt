package com.example.stickers.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.stickers.Activities.repositories.PhotosRep
import com.example.stickers.Models.FileModel
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class AppClass : MultiDexApplication(), Application.ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null
    var anyActivity: Activity? = null
    val photosRep by lazy { PhotosRep(applicationContext) }

    companion object {
        var fileListCollage: List<FileModel>? = ArrayList()
        var fileList: List<Status>? = ArrayList()
        var file30List: List<StatusDocFile>? = ArrayList()

        var applicationScope = MainScope()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("onLowMemory() called by system")
        applicationScope = MainScope()
    }

    lateinit var sharedPref: SharedPreferenceData
    override fun onCreate() {
        super.onCreate()
        sharedPref = SharedPreferenceData(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        registerActivityLifecycleCallbacks(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        anyActivity = p0
        currentActivity = p0
    }

    override fun onActivityStarted(p0: Activity) {
        anyActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
        currentActivity = null

    }

    override fun onActivityStopped(p0: Activity) {
        anyActivity = null

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        anyActivity = null
    }
}