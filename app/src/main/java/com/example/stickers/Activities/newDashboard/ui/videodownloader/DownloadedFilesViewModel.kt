package com.example.stickers.Activities.newDashboard.ui.videodownloader


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.withContext
import java.io.File


class DownloadedFilesViewModel : ViewModel() {
    private var repo: VideoRep? = null
    fun init(context: Context?) {
        repo = VideoRep(context)
    }
    suspend fun getFiles(folder: File?) = withContext(viewModelScope.coroutineContext) {
        repo?.getVideos(folder)
    }



}