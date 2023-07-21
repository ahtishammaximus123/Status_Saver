package com.example.stickers.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class SaveHelperFull{


    public fun saveintopathFull(imgUri: Uri, file: File,context : Context) {
        Log.e("hshs","Something went wrong.  starttt")
        val chunkSize = 1024 // We'll read in one kB at a time

        val imageData = ByteArray(chunkSize)

        var `in`: InputStream? = null
        var out: OutputStream? = null


        try {
            `in` = context.getContentResolver().openInputStream(imgUri)
            val out =
                FileOutputStream(file) // I'm assuming you already have the File object for where you're writing to
            var bytesRead: Int
            while (`in`?.read(imageData).also { bytesRead = it!! }!! > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)))
            }
        } catch (ex: Exception) {
            Log.e("hshs","Something went wrong."+ ex.toString())
        } finally {
            `in`?.close()
            out?.close()
        }
        Log.e("hshs","Something went wrong.  enddd")

    }
}