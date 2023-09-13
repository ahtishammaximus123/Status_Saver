package com.example.stickers.Activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stickers.Activities.PhotoCollage.FileUtils
import com.example.stickers.Adapter.CollageFilesAdaptor
import com.example.stickers.Models.FileModel
import com.example.stickers.app.AppClass
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.databinding.ActivityCollageFilesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CollageFilesActivity : BillingBaseActivity() {

    private val binding by lazy {
        ActivityCollageFilesBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: CollageFilesAdaptor
    private val fileList = arrayListOf<FileModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        SplashActivity.fbAnalytics?.sendEvent("CollageFileActivity_Open")

        initViews()
        initAdapter()
        CoroutineScope(Dispatchers.Default)
            .launch { getAllFilesFrom(FileUtils.getFolderName("Collage")) }
            .invokeOnCompletion {
                runOnUiThread { initAdapter() }
            }
        //
//        deletePackBTNCV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deletePack();
//            }
//        });


    }

    fun Activity.deleteFileByCursor(uri: Uri): Int {
        return contentResolver.delete(uri, null, null)
    }


    var delPos = -1
    var fyFileModel = FileModel()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (delPos != -1) {
                fileList.remove(fyFileModel)
                adapter.notifyItemRemoved(delPos)
                adapter.notifyDataSetChanged()
                checkFiles()
            }
        }
    }

    fun delete(context: Context, file: File): Boolean {
        SplashActivity.fbAnalytics?.sendEvent("CollageFileActivity_Del_File")
        val where: String = MediaStore.MediaColumns.DATA.toString() + "=?"
        val selectionArgs = arrayOf(
            file.absolutePath
        )
        val contentResolver: ContentResolver = context.contentResolver
        val filesUri: Uri = MediaStore.Files.getContentUri("external")
        contentResolver.delete(filesUri, where, selectionArgs)
        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs)
        }
        return !file.exists()
    }

    private fun initAdapter() {
        checkFiles()
        adapter = CollageFilesAdaptor(this, supportFragmentManager, fileList, object : CollageFilesAdaptor.FileListener {
            override fun onFileClick(fileModel: FileModel) {
                AppClass.fileListCollage = fileList
                val intent = Intent(
                    this@CollageFilesActivity,
                    ViewCollageImageActivity::class.java
                )
                intent.putExtra("path", fileModel.filePath)
                startActivity(intent)
            }

            override fun onFileDelete(fileModel: FileModel, position: Int) {
                delPos = position
                val file = File(fileModel.filePath)
                if (file.exists()) {
                    if (file.delete()) {
                        fileList.remove(fileModel)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyDataSetChanged()
                        checkFiles()
                    } else {
//                        if (deleteFileByCursor(getUriPath(file.absolutePath)) > 0) {
                        if(delete(this@CollageFilesActivity, file)){
                            fileList.remove(fileModel)
                            adapter.notifyItemRemoved(position)
                            adapter.notifyDataSetChanged()
                            checkFiles()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "File does not exist!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
        with(binding) {
            recyclerViewImage.layoutManager = GridLayoutManager(this@CollageFilesActivity, 2)
            recyclerViewImage.adapter = adapter
        }
    }

    private fun checkFiles() {
        with(binding) {
            if (fileList.isEmpty())
                messageTextImage.visibility = View.VISIBLE
            else
                messageTextImage.visibility = View.GONE
        }
    }

    private fun initViews() {
        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun getAllFilesFrom(path: String): ArrayList<FileModel> {
        fileList.clear()
        val dir = File(path)
        try {
            if (!dir.exists()) {
                dir.mkdir()
            }
            if (dir.isDirectory) {
                val directory = File(path)
                val files: Array<File>? = directory.listFiles()
                for (file in files!!) {
                    if (file.isFile) {
                        val fileModel = FileModel(
                            file.name,
                            path + "/" + file.name,
                        )
                        fileList.add(fileModel)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fileList.reverse()
        return fileList
    }

    override fun onBackPressed() {
        AppClass.fileListCollage = null
        finish()
//        InterstitialAdAppLovin.showCallback(this, object : AdCallback {
//            override fun onAdClosed() {
//                finish()
//            }
//            override fun onAdFailed() {
//                finish()
//            }
//        }, remoteAdSettings.inter_collage_back.value)

    }

}