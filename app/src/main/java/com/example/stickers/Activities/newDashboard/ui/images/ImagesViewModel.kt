package com.example.stickers.Activities.newDashboard.ui.images

import androidx.lifecycle.*
import com.example.stickers.Activities.repositories.PhotosRep
import kotlinx.coroutines.launch
import java.io.File

class ImagesViewModel(private val repository: PhotosRep) : ViewModel() {
    val photos = MutableLiveData<ArrayList<File>>()

    /**
     * Heavy operation that cannot be done in the Main Thread
     */
    fun loadImages() {

        viewModelScope.launch {
            repository.getPhotos() { files, message ->
                photos.postValue(files)
            }

        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    val selected = MutableLiveData<Int>()
    val getAllFiles = MutableLiveData<Boolean>()

    fun select(item: Int) {
        selected.value = item
    }

    fun getAllFiles() {
        getAllFiles.value = true
    }

}

class ImagesViewModelFactory(private val repository: PhotosRep) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImagesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImagesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}