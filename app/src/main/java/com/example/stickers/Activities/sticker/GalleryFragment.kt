package com.example.stickers.Activities.sticker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.gallery.RadioFile
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.Utils.openActivity
import com.example.stickers.WhatsAppBasedCode.Sticker
import com.example.stickers.ads.showToast
import com.example.stickers.app.AppClass
import com.example.stickers.databinding.FragmentGalleryItemListBinding

class GalleryFragment : Fragment(), ImageGalleryCallBack {

    private lateinit var binding: FragmentGalleryItemListBinding
    private var galleryImages = arrayListOf<RadioFile>()
    private var adapter: SingleGalleryAdapter? = null
    private var filePath: String? = ""
    private var adisready = "notshowed"
    var isActivityRunning = false
    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagesViewModel.loadImages()
        // Set the adapter
        adapter = SingleGalleryAdapter(this)
        with(binding) {

            back.setOnClickListener {
                findNavController().popBackStack()
            }
            done.setOnClickListener {
                if (!filePath.isNullOrEmpty()) {
                    activity?.openActivity<StickerActivity> {
                        putExtra("path", filePath)
                    }
                    findNavController().popBackStack()
                } else
                    activity?.showToast("Kindly Select File First!")
            }
            list.layoutManager = GridLayoutManager(context, 2)
            list.adapter = adapter
        }

        imagesViewModel.photos.observe(viewLifecycleOwner) {
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

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

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