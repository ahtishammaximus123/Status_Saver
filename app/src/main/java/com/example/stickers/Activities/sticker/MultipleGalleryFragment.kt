package com.example.stickers.Activities.sticker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.stickers.Activities.PhotoCollage.CollageProcessActivity
import com.example.stickers.Activities.newDashboard.ui.gallery.RadioFile
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.R

import com.example.stickers.ads.showToast
import com.example.stickers.ads.singleClick
import com.example.stickers.app.AppClass
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.databinding.FragmentGalleryItemListNewBinding
import com.example.stickers.databinding.ItemGalleryBinding

class MultipleGalleryFragment : Fragment(), ImageGalleryCallBack {

    private lateinit var binding: FragmentGalleryItemListNewBinding
    private var galleryImages = arrayListOf<RadioFile>()
    private var adapter: MultipleGalleryAdapter? = null
    private var currentPage = 1
    private lateinit var adapterPager: PagerAdapter

    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryItemListNewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openWhat = requireActivity().findViewById<ImageView>(R.id.open_whatsApp_icon)
        openWhat.visibility=View.GONE
        imagesViewModel.loadImages()
        // Set the adapter
        adapter = MultipleGalleryAdapter(this)
        with(binding) {

            back.setOnClickListener {
                findNavController().popBackStack()
            }
            done.setOnClickListener {
//                singleClick(it)
                if (selectedfiles.size > 1) {
                    if (selectedfiles.size <= 8) {
                            val intent = Intent(
                                activity,
                                CollageProcessActivity::class.java
                            )
                            intent.putStringArrayListExtra("photo_path", selectedfiles)
                            intent.putExtra("type", 1)
                            intent.putExtra("piece_size", selectedfiles.size)
                            startActivity(intent)
                            findNavController().popBackStack()

                    } else {
                        activity?.showToast("Can't Select More than 8 Photos!")
                    }
                } else
                    activity?.showToast("Kindly Select Minimum 2 Photos to Proceed!")
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

        setPagerAdapter()
    }

    private fun setPagerAdapter() {
        adapterPager = PagerAdapter()
        with(binding) {
            viewPager.adapter = adapterPager
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPage = position + 1
                    val text = " $currentPage / ${selectedfiles.size}"
                    txtPage.text = text
                }
            })

            imgLeft.setOnClickListener {
                if (viewPager.currentItem > 0)
                    viewPager.currentItem = viewPager.currentItem - 1
            }
            imgRight.setOnClickListener {
                if (viewPager.currentItem < selectedfiles.size)
                    viewPager.currentItem = viewPager.currentItem + 1
            }
        }
    }

    private var selectedfiles: ArrayList<String> = ArrayList()

    override fun onImageViewClicked(file: String?, tag: Int) {

        if (file != null) {
            if (selectedfiles.contains(file)) {
                selectedfiles.remove(file)
                galleryImages.firstOrNull { t -> t.file.absolutePath == file }?.selected = false
            } else {
                selectedfiles.add(file)
                galleryImages.firstOrNull { t -> t.file.absolutePath == file }?.selected = true
            }
        }

        adapter?.submitList(galleryImages)
        adapterPager.notifyDataSetChanged()
        val text = " $currentPage / ${selectedfiles.size}"
        binding.txtPage.text = text

        if (selectedfiles.isEmpty()) {
            binding.emptyImgLayout.visibility = View.VISIBLE
            binding.selectedImagesLayout.visibility = View.GONE
        } else {
            binding.emptyImgLayout.visibility = View.GONE
            binding.selectedImagesLayout.visibility = View.VISIBLE
        }

//        galleryImages.forEach {
//            it.selected = (it.file.absolutePath == file)
//        }
//        galleryImages[tag].selected = true
//        filePath = file
//
//        adapter?.submitList(galleryImages)
//        adapter?.notifyDataSetChanged()
    }

    private inner class PagerAdapter : RecyclerView.Adapter<PagerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemGalleryBinding.inflate(inflater, parent, false)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setOnBoardingData(selectedfiles[position])
        }

        override fun getItemCount(): Int {
            return selectedfiles.size
        }

        inner class ViewHolder(private val binding: ItemGalleryBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun setOnBoardingData(item: String) {
                Glide.with(requireContext())
                    .load(item)
                    .into(binding.image)
            }
        }
    }
}