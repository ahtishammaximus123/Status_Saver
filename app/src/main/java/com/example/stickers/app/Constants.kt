package com.example.stickers.app

import com.example.stickers.BuildConfig

class Constants {
    companion object {
        const val FIRST_TIME = "FIRST_TIME"
        const val KEY_IS_PURCHASED = "KEY_IS_PURCHASED"
        const val KEY_IS_PRIVACY = "KEY_IS_PRIVACY"
        private const val TestId = "android.test.purchased"
        private const val RealId = "com.status.video.saver.for.whatsapp.video.downloader.download.satatuses"

        @JvmField
        val subsList = listOf(
            if (BuildConfig.DEBUG)
                TestId else RealId
        )

        const val providerWhatsApp = BuildConfig.APPLICATION_ID


    }
}