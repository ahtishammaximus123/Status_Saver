package com.example.stickers.app

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import com.example.stickers.app.Constants.Companion.providerWhatsApp
import java.io.File
import java.io.FileNotFoundException


fun length(contentResolver: ContentResolver, uri: Uri)
        : Long {

    val assetFileDescriptor = try {
        contentResolver.openAssetFileDescriptor(uri, "r")
    } catch (e: FileNotFoundException) {
        null
    }
    // uses ParcelFileDescriptor#getStatSize underneath if failed
    val length = assetFileDescriptor?.use { it.length } ?: -1L
    if (length != -1L) {
        return length
    }

    // if "content://" uri scheme, try contentResolver table
    if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        return contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex == -1) {
                    return@use -1L
                }
                cursor.moveToFirst()
                return try {
                    cursor.getLong(sizeIndex)
                } catch (_: Throwable) {
                    -1L
                }
            } ?: -1L
    } else {
        return -1L
    }
}

fun Context.getUriPath(path: String): Uri {
    return FileProvider.getUriForFile(
        this,
        providerWhatsApp,
        File(path)
    )
}

fun Context.getUriPathNew(path: String): Uri {
    return FileProvider.getUriForFile(
        this,
        packageName,
        File(path)
    )
}

fun Context.shareFile(uri: Uri) {
    try {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "*/*"
        val link = "http://play.google.com/store/apps/details?id=$packageName"
        share.putExtra(
            Intent.EXTRA_TEXT,
            "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
        )
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val chooser = Intent.createChooser(share, "Share image")
        chooser.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivity(share)
    } catch (e: Exception) {
        Log.e("error__", e.toString())
    }
}