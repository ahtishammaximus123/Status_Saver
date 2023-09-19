package com.example.stickers.Adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.imagesList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.selectedStatusList
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.MultiSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.saveStatus
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity.context
import com.example.stickers.ads.loadNativeAd

import com.example.stickers.ads.showToast
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.app.shareFile

class ImageAdapter30plus(
     val context: Activity,
    private val downloadListener: () -> Unit,
    private val callBack: MultiSelectCallback,
     private val recyclerView: RecyclerView,
    val supportFragmentManager: FragmentManager
) :
    ListAdapter<StatusDocFile, RecyclerView.ViewHolder>(ADAPTER_COMPARATOR) {
    private val ITEM_TYPE_IMAGE = 0
    private val ITEM_TYPE_AD = 1
    private val ITEMS_PER_AD = 2 // Show an ad after every 2 items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_status, parent, false)
                ItemImageViewHolder(view)
            }
            ITEM_TYPE_AD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_ad, parent, false)
                AdViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_TYPE_IMAGE) {
            // Handle regular item
            val imageHolder = holder as ItemImageViewHolder
            val reversedPosition = itemCount - 1 - position
            val reversedStatus = getItem(reversedPosition)
            ItemsViewModel = reversedStatus

            if (imagesList.isNotEmpty() && reversedPosition >= 0 && reversedPosition < imagesList.size) {
                if (imagesList[reversedPosition].isSelected) {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                    holder.imageView.foreground = drawable
                } else {
                    holder.imageView.foreground = null
                }
            } else {
            }


            Log.d("tree", "onBindViewHolder " + reversedStatus.file.uri)

            if (ItemsViewModel?.isSavedStatus() == true) {
                holder.save.setImageResource(R.drawable.ic_download_ic__1_)
                holder.save.tag = "saved"
            } else {
                holder.save.setImageResource(R.drawable.ic_download_ic)
                holder.save.tag = "notSaved"

            }

            Glide.with(context)
                .load(ItemsViewModel!!.file.uri)
                .into(holder.imageView)

            holder.share.setOnClickListener { v ->
                ItemsViewModel = reversedStatus
                ItemsViewModel?.file?.let {
                    context.shareFile(it.uri, supportFragmentManager)
                }
            }

            holder.imageView.setOnLongClickListener {
                if (!isMultiSelect) {
                    isMultiSelect = true
                    selectedStatusList.add(reversedStatus)
                    imagesList[reversedPosition].isSelected = true
                    val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                    holder.imageView.foreground = drawable
                    callBack.onMultiSelectModeActivated()
                    context.showToast("Now select items by clicking")
                }

                true
            }

            holder.imageView.setOnClickListener {
                if (isMultiSelect) {
                    if (selectedStatusList.contains(reversedStatus)) {
                        selectedStatusList.remove(reversedStatus)
                        imagesList[reversedPosition].isSelected = false
                        holder.imageView.foreground = null
                        callBack.onMultiSelectModeActivated()
                    } else {
                        val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                        holder.imageView.foreground = drawable
                        imagesList[reversedPosition].isSelected = true
//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    holder.imageView.foreground=foregroundDrawable
                        selectedStatusList.add(reversedStatus)
                        callBack.onMultiSelectModeActivated()
                    }
                    //     notifyItemChanged(position)
                } else {
                    ImagesFragment.clickedPosition = position
                    ImagesFragment.openSaved = false
                    val i = Intent(
                        context,
                        FullScreenImageActivity::class.java
                    )
                    i.action = "asa"
                    ItemsViewModel = reversedStatus
                    i.putExtra("is30Plus", true)
                    i.putExtra("img_tag", holder.save.getTag().toString())
                    Log.e("img_tag", holder.save.getTag().toString())
                    context.startActivity(i)
                }
            }
            holder.save.setOnClickListener { v ->
                if (holder.save.tag != "saved") {
                    ItemsViewModel = reversedStatus
                    try {
                        context.saveStatus(v, reversedStatus)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Corrupted Image, Can't be saved", Toast.LENGTH_LONG)
                            .show()
                    }
                    ItemsViewModel?.setSavedStatus(true)
                    notifyItemChanged(reversedPosition)
                    downloadListener.invoke()
                    ImagesFragment.clickedPosition = position
                    ImagesFragment.openSaved = false
                    val i = Intent(
                        context,
                        FullScreenImageActivity::class.java
                    )
                    i.action = "asa"
                    ItemsViewModel = reversedStatus
                    i.putExtra("is30Plus", true)
                    i.putExtra("img_tag", holder.save.getTag().toString())
                    Log.e("img_tag", holder.save.getTag().toString())
                    context.startActivity(i)
                    holder.save.setImageResource(R.drawable.ic_download_ic__1_)
                } else {
                    context.showToast("Image Already Downloaded")
                }
            }

        }
        else if (getItemViewType(position) == ITEM_TYPE_AD) {
            val adHolder = holder as AdViewHolder
            loadNativeAd(context,adHolder.frame!!,
                remoteAdSettings.admob_native_dashboard_ad.value,context.layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        }

    }
    class ItemImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var save: ImageView
        var share: ImageView
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.ivThumbnail)
            save = itemView.findViewById(R.id.save)
            share = itemView.findViewById(R.id.share)
        }
    }


    companion object {
        private val ADAPTER_COMPARATOR = object : DiffUtil.ItemCallback<StatusDocFile>() {
            override fun areItemsTheSame(oldItem: StatusDocFile, newItem: StatusDocFile): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: StatusDocFile,
                newItem: StatusDocFile
            ): Boolean {
                return oldItem.path == newItem.path
            }
        }
    }

    override fun getItemCount(): Int {
        val itemCountWithoutAds = super.getItemCount()
        val adCount = itemCountWithoutAds / ITEMS_PER_AD
        return itemCountWithoutAds
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 2) {
            ITEM_TYPE_AD
        } else {
            ITEM_TYPE_IMAGE
        }
    }


    fun setLayoutManager() {
        val layoutManager = GridLayoutManager(StickerPackListActivity.context, Common.GRID_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == ITEM_TYPE_AD) {
                    Common.GRID_COUNT // For ad items, span the entire row
                } else {
                    1 // For regular items, occupy one span
                }
            }
        }
        recyclerView.layoutManager = layoutManager
    }
}


class AdViewHolder(adview: View?) : RecyclerView.ViewHolder(adview!!) {
    var frame: FrameLayout = itemView.findViewById(R.id.main_dash_native)

}
