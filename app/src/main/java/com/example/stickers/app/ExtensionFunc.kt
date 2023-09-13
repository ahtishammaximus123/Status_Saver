package com.example.stickers.app

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.example.stickers.app.Constants.Companion.providerWhatsApp
import com.example.stickers.dialog.ShareFragment
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
fun Context.shareFile(path: Uri, supportFragmentManager: FragmentManager) {


    var exitDialogFragment: ShareFragment? = null
    exitDialogFragment = ShareFragment(path)
    exitDialogFragment!!.show(supportFragmentManager, "exit_dialog_tag")


}
