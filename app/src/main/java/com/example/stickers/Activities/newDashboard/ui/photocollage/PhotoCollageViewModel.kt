package com.example.stickers.Activities.newDashboard.ui.photocollage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.repositories.PhotosRep

class PhotoCollageViewModel(private val repository: PhotosRep) : ViewModel() {
    val type : MutableLiveData<Int> = MutableLiveData()
    val pieceSize : MutableLiveData<Int> = MutableLiveData()
    val themeId : MutableLiveData<Int> = MutableLiveData()
    val bitmapPaint : MutableLiveData<List<String>?> = MutableLiveData()
}
class PhotoCollageViewModelFactory(private val repository: PhotosRep) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoCollageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoCollageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}