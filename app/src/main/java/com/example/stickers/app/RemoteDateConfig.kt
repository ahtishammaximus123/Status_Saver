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
    @SerializedName("admob_InterID1")
    val admob_Inter_ID1: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_InterID2")
    val admob_Inter_ID2: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_InterID3")
    val admob_Inter_ID3: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_InterID4")
    val admob_Inter_ID4: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_InterID5")
    val admob_Inter_ID5: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_open_id")
    val admob_open_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_inter_splash_id")
    val admob_inter_splash_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_first_piroity_native_id")
    val admob_first_piroity_native_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_second_piroity_native_id")
    val admob_second_piroity_native_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_splash_native_id")
    val admob_splash_native_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_small_banner_id")
    val admob_small_banner_id: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_adaptive_banner_id")
    val admob_adaptive_banner_id: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_splash_ad")
    val admob_native_splash_ad: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_native_dashboard_ad")
    val admob_native_dashboard_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_download_link_native_ad")
    val admob_native_download_link_native_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_videos_downloaded_native_ad")
    val admob_native_videos_downloaded_native_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_splash_inter_ad")
    val admob_splash_inter_ad: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_allow_permission_inter_ad")
    val admob_allow_permission_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_create_sticker_save_btn_inter_ad")
    val admob_create_sticker_save_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_create_sticker_create_btn_inter_ad")
    val admob_create_sticker_create_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_create_sticker_done_btn_inter_ad")
    val admob_create_sticker_done_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_create_collage_done_btn_inter_ad")
    val admob_create_collage_done_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_mirroring_scr_inter_ad")
    val admob_mirroring_scr_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_create_collage_save_btn_inter_ad")
    val admob_create_collage_save_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_select_whats_app_ad")
    val admob_native_select_whats_app_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_allow_permission_bottom_sheet_ad")
    val admob_native_allow_permission_bottom_sheet_ad: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_download_btn_inter_ad")
    val admob_download_btn_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_video_downloader_player_back_inter_ad")
    val admob_video_downloader_player_back_inter_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("collapseAble_banner_ID")
    val collapseAble_banner_ID: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_collapsable_banner_ad")
    val admob_collapsable_banner_ad: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_adaptive_image_full_scr_banner_ad")
    val admob_adaptive_image_full_scr_banner_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_adaptive_video_full_scr_banner_ad")
    val admob_adaptive_video_full_scr_banner_ad: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_adaptive_create_sticker_banner_ad")
    val admob_adaptive_create_sticker_banner_ad: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_adaptive_create_collage_banner_ad")
    val admob_adaptive_create_collage_banner_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_adaptive_dashboard_banner_ad")
    val admob_adaptive_dashboard_banner_ad: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("base_url_link")
    val base_url_link: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("base_url_anything")
    val base_url_anything: RemoteAdDetails = RemoteAdDetails(),


    @SerializedName("admob_adaptive_mirroring_scr_banner_ad")
    val admob_adaptive_mirroring_scr_banner_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_full_screen_image_ad")
    val admob_native_full_screen_image_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_full_screen_video_ad")
    val admob_native_full_screen_video_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_share_screen_ad")
    val admob_native_share_screen_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_share_bottom_sheet_ad")
    val admob_native_share_bottom_sheet_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_mirroring_ad")
    val admob_native_mirroring_ad: RemoteAdDetails = RemoteAdDetails(),
    @SerializedName("admob_native_sticker_maker_ad")
    val admob_native_sticker_maker_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_save_files_ad")
    val admob_native_save_files_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("admob_native_exit_dialog_ad")
    val admob_native_exit_dialog_ad: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("appOpenAd")
    val appOpenAd: RemoteAdDetails = RemoteAdDetails(),

    @SerializedName("call_to_action_btn_color")
    val call_to_action_btn_color: RemoteAdDetails = RemoteAdDetails(),
    )
{
    fun getAdmobAppOpen() =
        appOpenAd.value

}

@Keep
data class RemoteAdDetails(
    @SerializedName("value")
    val value: String = "",
)