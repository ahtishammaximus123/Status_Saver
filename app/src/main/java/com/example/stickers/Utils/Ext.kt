package com.example.stickers.Utils

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.ads.showToast
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

fun Context.saveStatus(view: View, model: StatusDocFile) {
    if (Common.getSavedFile(model.title)) {
        showSnackBar(view, "Already downloaded.")
        return
    }
    val justDirOut = File(Common.APP_DIR)
    if (!justDirOut.exists()) {
        justDirOut.mkdir()
    }
    val outDirCopy = File(justDirOut, model.file?.name!!)
    model.file?.uri?.let {
        saveInToPath(it, outDirCopy)
        showSnackBar(view, "File downloaded successfully.")
    }
}

fun Context.showSnackBar(snackView: View, text: String) {
    /*val snack: Snackbar = Snackbar.make(snackView, text, Snackbar.LENGTH_LONG)
    val view = snack.view
    val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.BOTTOM
//    params.setMargins(10, 10, 10, 10)
    view.layoutParams = params
//    snack.anchorView = snackView
    snack.show()*/
    showToast(text)
}

inline fun <reified T> Context.openActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T> Context.openActivity(extras: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    extras(intent)
    startActivity(intent)
}

 fun Context.saveInToPath(imgUri: Uri, file: File) {

    val chunkSize = 1024 // We'll read in one kB at a time

    val imageData = ByteArray(chunkSize)

    var `in`: InputStream? = null
    val out: OutputStream? = null


    try {
        `in` = contentResolver.openInputStream(imgUri)
        val out =
            FileOutputStream(file) // I'm assuming you already have the File object for where you're writing to
        var bytesRead: Int
        while (`in`?.read(imageData).also { bytesRead = it!! }!! > 0) {
            out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)))
        }
    } catch (ex: Exception) {
        Log.e("Something went wrong.", ex.toString())
    } finally {
        `in`?.close()
        out?.close()
    }
}


fun TextView.setTextViewDrawableColor(color: Int) {
    for (drawable in this.compoundDrawablesRelative) {
        drawable?.mutate()
        drawable?.setTintList(context.getColorTint(color))
    }
}

fun Context.getColorTint(@ColorRes id: Int): ColorStateList {
    return ColorStateList.valueOf(getMyColor(id))
}

fun Context.getMyColor(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, theme)
}

fun Context.getMyDrawable(@DrawableRes id: Int): Drawable? {
    return ResourcesCompat.getDrawable(resources, id, theme)
}