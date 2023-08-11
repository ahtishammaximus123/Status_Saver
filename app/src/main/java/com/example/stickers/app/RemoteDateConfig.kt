package com.example.stickers.app

import android.util.Log
import androidx.annotation.Keep
import com.example.stickers.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


class RemoteDateConfig {

    private var remoteConfig: FirebaseRemoteConfig? = null
    private val timeInMillis: Long = if (BuildConfig.DEBUG) 0L else 3600L
    private val remoteTopic = "status_v9"

    companion object {
        @JvmStatic
        var remoteAdSettings = RemoteAdSettings()
    }


    private fun getInstance(): FirebaseRemoteConfig? {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSetting = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(timeInMillis)
            .build()
        remoteConfig?.setConfigSettingsAsync(configSetting)
        remoteConfig?.setDefaultsAsync(
            mapOf(remoteTopic to Gson().toJson(RemoteAdSettings()))
        )
        return remoteConfig
    }


    private fun getRemoteConfig(): RemoteAdSettings {
        return Gson().fromJson(
            getInstance()?.getString(remoteTopic),
            RemoteAdSettings::class.java
        )
    }

    fun getSplashRemoteConfig(listener: (RemoteAdSettings?) -> Unit) {
        getInstance()?.fetchAndActivate()
            ?.addOnCompleteListener { task ->
                Log.e("RemoteConfig", "task : ${task.isSuccessful}")
                if (task.isSuccessful) {
                    val value = getRemoteConfig()
                    remoteAdSettings = value
                    listener.invoke(value)
                } else {
                    listener.invoke(null)
                }
            }
    }
}

@Keep
data class RemoteAdSettings(
    /**AD KEYS**/
    @SerializedName("admob_open_id")
    val admob_open_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_inter_id")
    val admob_inter_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_native_id")
    val admob_native_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_inter_splash_id")
    val admob_inter_splash_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_native_id_1")
    val admob_native_id_1: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_native_id_2")
    val admob_native_id_2: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_inter_download_btn_ad")
    val admob_inter_download_btn_ad: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_inter_download_btn_id")
    val admob_inter_download_btn_id: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("appOpen")
    val appOpen: RemoteAdDetails = RemoteAdDetails(),

    /**INTERSTITIALS KEYS**/
    @SerializedName("inter_splash_ad")
    val inter_splash_ad: RemoteAdDetails = RemoteAdDetails(),
    //Status
    @SerializedName("inter_view_video")
    val inter_view_video: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_view_image")
    val inter_view_image: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_download_status")
    val inter_download_status: RemoteAdDetails = RemoteAdDetails(),
    //Sticker
    @SerializedName("inter_create_sticker")
    val inter_create_sticker: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_sticker_added")
    val inter_sticker_added: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_sticker_save")
    val inter_sticker_save: RemoteAdDetails = RemoteAdDetails(),
    //Collage
    @SerializedName("inter_collage_photos")
    val inter_collage_photos: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_collage_view_photo")
    val inter_collage_view_photo: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("inter_collage_save_photo")
    val inter_collage_save_photo: RemoteAdDetails = RemoteAdDetails(),

    /**NATIVES KEYS**/
    @SerializedName("native_dashboard")
    val native_dashboard: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("native_inner")
    val native_inner: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("native_sticker")
    val native_sticker: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("native_collage")
    val native_collage: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("native_video_view")
    val native_video_view: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("native_image_view")
    val native_image_view: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("call_to_action_btn_color")
    val call_to_action_btn_color: RemoteAdDetails = RemoteAdDetails(""),
    @SerializedName("maxNativeId")
    val maxNativeId: RemoteAdDetails = RemoteAdDetails(""),
    @SerializedName("maxInterId")
    val maxInterId: RemoteAdDetails = RemoteAdDetails(""),
    @SerializedName("maxDashboardNativeId")
    val maxDashboardNativeId: RemoteAdDetails = RemoteAdDetails(""),
    @SerializedName("maxDashboardInterId")
    val maxDashboardInterId: RemoteAdDetails = RemoteAdDetails(""),
) {
    fun getAdmobAppOpen() =
        admob_open_id.value

    fun getAdmobInterId() =
        admob_inter_id.value

    fun getAdmobNativeId() =
        admob_native_id.value

    fun getNativeDashboardAd() =
        native_dashboard.value

    fun getAdmobDownloadBtnInterId() =
        admob_inter_download_btn_ad.value

    fun getAdmobSplashNativeId1() =
        admob_native_id_1.value


    fun getAdmobSplashNativeId2() =
        admob_native_id_2.value



    /**
     * TEST ID'S
     * cc5d92d7be74d0b7 Inter
     * 5f24543b8cf51a75 Banner
     * f8a7c5bc9b3e47fd Native
     *
     * Live ID'S For TESTING
     * 57260bcab4e4e31f
     * Native Testing AD IDS
     * 9e7931178c2f7d2e
     * Banner Testing AD IDS
     * 0536633a072cca85	*/
}

@Keep
data class RemoteAdDetails(
    @SerializedName("value")
    val value: String = "",
)