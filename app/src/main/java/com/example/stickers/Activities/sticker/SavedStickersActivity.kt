package com.example.stickers.Activities.sticker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stickers.Activities.PhotoCollage.FileUtils
import com.example.stickers.Activities.ViewCollageImageActivity
import com.example.stickers.Adapter.CollageFilesAdaptor
import com.example.stickers.Models.FileModel
import com.example.stickers.databinding.ActivitySavedStickersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SavedStickersActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySavedStickersBinding.inflate(layoutInflater) }

    private lateinit var adapter: CollageFilesAdaptor
    private val fileList = arrayListOf<FileModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initAdapter()
        CoroutineScope(Dispatchers.Default)
            .launch { getAllFilesFrom(FileUtils.getFolderName("StickerMaker")) }
            .invokeOnCompletion {
                runOnUiThread { initAdapter() }
            }

    }

    private fun initAdapter() {
        checkFiles()
        adapter = CollageFilesAdaptor(this,supportFragmentManager, fileList, object : CollageFilesAdaptor.FileListener {
            override fun onFileClick(fileModel: FileModel) {
                val intent = Intent(
                    this@SavedStickersActivity,
                    ViewCollageImageActivity::class.java
                )
                intent.putExtra("path", fileModel.filePath)
                startActivity(intent)
            }

            override fun onFileDelete(fileModel: FileModel, position: Int) {
                val file = File(fileModel.filePath)
                if (file.exists()) {
                    if (file.delete()) {
                        fileList.remove(fileModel)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyDataSetChanged()
                        checkFiles()
                    }
                }
            }
        })
        with(binding) {
            recyclerSticker.layoutManager = GridLayoutManager(this@SavedStickersActivity, 2)
            recyclerSticker.adapter = adapter
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
        return fileList
    }

}