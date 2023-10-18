package com.example.stickers.Activities.newDashboard.ui.videodownloader

import com.google.gson.annotations.SerializedName


data class VideoData(
    @SerializedName("quality") val quality: String,
    @SerializedName("url") val url: String
)
