package com.example.stickers.Activities.sticker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.gallery.RadioFile
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.ads.showToast
import com.example.stickers.databinding.FragmentGalleryItemBinding


class MultipleGalleryAdapter(private val callBack: ImageGalleryCallBack) :
    ListAdapter<RadioFile, MultipleGalleryAdapter.ViewHolder>(ADAPTER_COMPARATOR) {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            FragmentGalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    private fun selectedList(): Int {
        var index = 0
        currentList.forEach {
            if (it.selected)
                index++
        }
        return index
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        context?.let {
            Glide.with(it)
                .load(item.file)
                .into(holder.image)
        }
        holder.radio.setOnCheckedChangeListener(null)
        holder.radio.isChecked = item.selected

//        holder.image.setOnClickListener {
//            callBack.onImageViewClicked(item.file.absolutePath, position)
//            Log.e("tag**", "check: ${holder.radio.isChecked}")
//        }
        holder.image.setOnClickListener {
            if (selectedList() < 8 || holder.radio.isChecked)
                holder.radio.isChecked = !item.selected
            else
                context?.showToast("Can't Select More Than 8 Photos!")
            //callBack.onImageViewClicked(item.file.absolutePath, position)
        }
        holder.radio.setOnCheckedChangeListener { compoundButton, b ->
            callBack.onImageViewClicked(item.file.absolutePath, position)
        }

    }

    inner class ViewHolder(binding: FragmentGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image: ImageView = binding.image
        val radio: CheckBox = binding.radio


    }

    companion object {
        private val ADAPTER_COMPARATOR = object : DiffUtil.ItemCallback<RadioFile>() {
            override fun areItemsTheSame(oldItem: RadioFile, newItem: RadioFile): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: RadioFile, newItem: RadioFile): Boolean {
                return oldItem.file.path == newItem.file.path
            }
        }
    }
}