package com.example.stickers.Activities.repositories

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.WAoptions
import com.example.stickers.app.AppClass
import com.example.stickers.app.getUriPath
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class PhotosRep(private var context: Context?)  {

    suspend fun getPhotos(completion: (ArrayList<File>, Error?) -> Unit) = withContext(Dispatchers.IO) {
        // Heavy work
        val files: ArrayList<File> = ArrayList<File>()
        try {
            val cr: ContentResolver? = context?.contentResolver
//            val uri = MediaStore.Files.getContentUri("external")
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var selection: String? = null
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
//            }
            val selectionArgs: Array<String>? = null // there is no ? in selection so null here
            val orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
            var cursor = cr?.query(uri, projection, selection, selectionArgs, orderBy)

            if (cursor != null) {
                while (cursor != null && cursor.moveToNext()) {
                    val a = cursor.getString(0)
                    val f = File(a)
                    if(f.exists() && f.isFile)
                    files.add(f)
                    if (!isActive) {
                        cursor.close()
                        cursor = null
                    }
                }
                cursor?.close()
            }
        }
        catch (ex: Exception){}
        completion(files, null)
    }
    private suspend fun getImages30(completion: (MutableList<StatusDocFile>?, Error?) -> Unit)  = withContext(Dispatchers.IO) {
            //for 11
            Log.d("tree", "getStatus: 30 or above")
            val sharedPreferences = context?.getSharedPreferences("uriTreePref", Context.MODE_PRIVATE)
            var ut = "uriTree"
            if (WAoptions.appPackage == "com.whatsapp.w4b") ut = "uriTree1"
            if (sharedPreferences?.getString(ut, "not present") != "not present") {
                Log.d("tree", "getStatus:  30 or above perm yes")
                val uriTree = sharedPreferences?.getString(ut, "null")

                val docFilePath = context?.let { DocumentFile.fromTreeUri(it, Uri.parse(uriTree)) }
                val imagesList30plus: MutableList<StatusDocFile> = java.util.ArrayList()
                if (docFilePath != null && docFilePath.listFiles().isNotEmpty()) {
                    var i = 0
                    val size = docFilePath.listFiles().size

                    while (isActive && i < size) {
                        val docFile = docFilePath.listFiles()[i]
                        val statusDocFile = StatusDocFile(docFile, docFile.name, docFile.uri.path)
                        if (!statusDocFile.isVideo && statusDocFile.title.endsWith(".jpg") && !imagesList30plus.contains(statusDocFile)) {
                            imagesList30plus.add(statusDocFile)
                            val check = getSavedFile(statusDocFile.title)
                            statusDocFile.setSavedStatus(check)
                        }
                        i++
                    }
                }

                completion(imagesList30plus, null)

            } else {
                completion(null, null)
            }

    }
    private suspend fun getImages(completion: (MutableList<Status>?, Error?) -> Unit)  = withContext(Dispatchers.IO) {
        var wAFolder: File? = null
        val imagesList: MutableList<Status> = java.util.ArrayList()
        try {
            if (WAoptions.appPackage == "com.whatsapp") {
                if (Common.STATUS_DIRECTORY_1.listFiles() != null) {
                    if (Objects.requireNonNull(Common.STATUS_DIRECTORY_1.listFiles())
                            .isNotEmpty()
                    ) {
                        wAFolder = Common.STATUS_DIRECTORY_1
                    }
                }
                if (Common.STATUS_DIRECTORY_NEW.listFiles() != null) {
                    if (Objects.requireNonNull(Common.STATUS_DIRECTORY_NEW.listFiles())
                            .isNotEmpty()
                    ) {
                        wAFolder = Common.STATUS_DIRECTORY_NEW
                    }
                }
            }
            else {
                if (Common.STATUS_DIRECTORY_2.listFiles() != null) {
                    if (Objects.requireNonNull(Common.STATUS_DIRECTORY_2.listFiles())
                            .isNotEmpty()
                    ) {
                        wAFolder = Common.STATUS_DIRECTORY_2
                    }
                }
                if (Common.STATUS_DIRECTORY_NEW_2.listFiles() != null) {
                    if (Objects.requireNonNull(Common.STATUS_DIRECTORY_NEW_2.listFiles())
                            .isNotEmpty()
                    ) {
                        wAFolder = Common.STATUS_DIRECTORY_NEW_2
                    }
                }
            }
            val statusFiles: Array<File>? = wAFolder?.listFiles()
            if (statusFiles != null && statusFiles.isNotEmpty()) {
                var i = 0
                val size = statusFiles.size
                Arrays.sort(statusFiles)
                while (isActive && i < size) {
                    val file = statusFiles[i]
                    try {
                        val status = Status(file, file.name, file.absolutePath)
                        if (!status.isVideo && status.title.endsWith(".jpg") && !imagesList.contains(status)) {
                            imagesList.add(status)
                            val check = getSavedFile(status.title)
                            status.setSavedStatus(check)
                        }
                    } catch (ex: Exception) {
                    }
                    i++
                }
            }
        } catch (ex: Exception) {
            Log.e("sdds","dds")
        }
        completion(imagesList, null)

    }
    private fun getSavedFile(name: String): Boolean {

        if(Common.APP_DIR != null){
            val app_dir = File(Common.APP_DIR)
//            Log.e("checkinggsaved", "Saved Time    " + app_dir.absolutePath + "")
            if (app_dir.exists()) {
                val savedFiles: Array<File>? = app_dir.listFiles()
                if (savedFiles != null && savedFiles.isNotEmpty()) {
                    Arrays.sort(savedFiles)
                    for (file in savedFiles) {
                        val status = Status(file, file.name, file.absolutePath)
                        if (!status.isVideo) {
//                            Log.e("checkinggsaved", "Saved Time    " + status.title)
//                            Log.e("checkinggsaved", "Saved Time    $name")
                            if (name == status.title) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }



}