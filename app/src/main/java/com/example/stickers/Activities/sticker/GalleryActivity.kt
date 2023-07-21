package com.example.stickers.Activities.sticker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.gallery.RadioFile
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.ads.showToast
import com.example.stickers.app.AppClass
import com.example.stickers.databinding.FragmentGalleryItemListBinding

class GalleryActivity : AppCompatActivity(), ImageGalleryCallBack {

    private val binding by lazy {
        FragmentGalleryItemListBinding.inflate(layoutInflater)
    }
    private var galleryImages = arrayListOf<RadioFile>()
    private var adapter: SingleGalleryAdapter? = null
    private var filePath: String? = ""

    private val imagesViewModel: ImagesViewModel by viewModels() {
        ImagesViewModelFactory((application as AppClass).photosRep)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imagesViewModel.loadImages()
        // Set the adapter
        adapter = SingleGalleryAdapter(this)
        with(binding) {

            back.setOnClickListener {
                onBackPressed()
            }
            done.setOnClickListener {
                if (!filePath.isNullOrEmpty()) {
                    val result = Intent()
                    result.putExtra("path", filePath)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                } else
                    showToast("Kindly Select File First!")
            }
            list.layoutManager = GridLayoutManager(this@GalleryActivity, 2)
            list.adapter = adapter
        }

        imagesViewModel.photos.observe(this) {
            if (it.isEmpty()) {
                binding.done.visibility = View.GONE
                binding.textView4.visibility = View.VISIBLE
            } else {
                galleryImages.clear()
                it.forEach { file ->
                    galleryImages.add(RadioFile(file, false))
                }
                adapter?.submitList(galleryImages)
            }
        }

    }

    override fun onImageViewClicked(file: String?, tag: Int) {
        binding.imgTrayIcon.visibility = View.GONE
        binding.textView.visibility = View.GONE
        galleryImages.forEach {
            it.selected = (it.file.absolutePath == file)
        }
        galleryImages[tag].selected = true
        filePath = file
        Glide.with(this)
            .load(file)
            .into(binding.imgSelect)
        adapter?.submitList(galleryImages)
        adapter?.notifyDataSetChanged()
    }
}