package com.example.stickers.Activities.newDashboard.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.gallery.placeholder.PlaceholderContent.PlaceholderItem
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.Models.Status
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity.context
import com.example.stickers.databinding.FragmentGalleryItemBinding
import java.io.File

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class GalleryItemRecyclerViewAdapter(private val callBack: ImageGalleryCallBack) : ListAdapter<RadioFile, GalleryItemRecyclerViewAdapter.ViewHolder>(ADAPTER_COMPARATOR)
{
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            FragmentGalleryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
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
        holder.radio.setOnCheckedChangeListener { compoundButton, b ->
            callBack.onImageViewClicked(item.file.absolutePath, position)
        }
        holder.image.setOnClickListener {
            holder.radio.isChecked = !item.selected
            //callBack.onImageViewClicked(item.file.absolutePath, position)
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