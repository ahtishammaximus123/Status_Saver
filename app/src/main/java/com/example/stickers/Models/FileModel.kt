package com.example.stickers.Models

import androidx.annotation.Keep

@Keep
data class FileModel(
    var fileName: String = "",
    var filePath: String = "",
)  {
    override fun toString(): String {
        return "$fileName - $filePath"
    }
}