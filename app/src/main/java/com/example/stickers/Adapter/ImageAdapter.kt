package com.example.stickers.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.stickers.ImageAdapterCallBack
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Models.Status
import com.example.stickers.MultiSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.ads.showToast
import com.example.stickers.app.RemoteDateConfig

class ImageAdapter(private val container: ConstraintLayout?,
                   private val callBack: ImageAdapterCallBack,
                   private val callBack2: MultiSelectCallback,
                   private val recyclerView: RecyclerView,
                   private var context: Activity
)
    : ListAdapter<Status, RecyclerView.ViewHolder>(ADAPTER_COMPARATOR)
{

    private val ITEM_TYPE_AD = 1
    private val ITEM_TYPE_IMAGE = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_status, parent, false)
                ItemViewHolder(view)
            }
            ITEM_TYPE_AD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_ad, parent, false)
                AdViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_TYPE_IMAGE) {
            val itemHolder = holder as ItemViewHolder
            val status = getItem(position)
            // Reverse the position calculation to display newest items at the top
            val reversedPosition = itemCount - 1 - position
            val reversedStatus = getItem(reversedPosition)
            if (ImagesFragment.imagesList29.isNotEmpty() && reversedPosition >= 0 && reversedPosition < ImagesFragment.imagesList29.size) {
                if (ImagesFragment.imagesList29[reversedPosition].isSelected) {
                    val drawable =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                    itemHolder.imageView.foreground = drawable
                } else {
                    itemHolder.imageView.foreground = null
                }
            } else {
                // Handle the case where reversedPosition is out of bounds or selectedStatusList is empty.
                // You can log an error or perform appropriate error handling here.
            }

            if (reversedStatus.isSavedStatus()) {
                itemHolder.save.setImageResource(R.drawable.ic_download_ic__1_)
                itemHolder.save.tag = "saved"
            } else {

                itemHolder.save.setImageResource(R.drawable.ic_download_ic)
                itemHolder.save.tag = "notSaved"
            }
//        if (!ImagesFragment.isMultiSelect) {
//            itemHolder.imageView.foreground=null
//            itemHolder.save.visibility=View.VISIBLE
//            itemHolder.share.visibility=View.VISIBLE
//        }
//        else{
//            itemHolder.save.visibility=View.INVISIBLE
//            itemHolder.share.visibility=View.INVISIBLE
//        }
            // Load the image for the reversed status
            Glide.with(context!!)
                .load(reversedStatus.file)
                .into(itemHolder.imageView)

            if (itemHolder.save.tag == "saved") {
                itemHolder.save.isClickable = false
            } else {
                itemHolder.save.setOnClickListener {
                    itemHolder.save.isClickable = true
                    if (itemHolder.save.tag != "saved") {
                        itemHolder.save.setImageResource(R.drawable.ic_download_ic)
                        callBack.onDownloadClick(reversedStatus, container)
                        itemHolder.save.visibility = View.INVISIBLE
                        itemHolder.share.visibility = View.INVISIBLE
                        ImagesFragment.openSaved = false
                        ImagesFragment.clickedPosition = position
                        callBack.onImageViewClicked(reversedStatus, itemHolder.save.tag)
                        reversedStatus.setSavedStatus(true)
                        notifyItemChanged(reversedPosition)

                    } else {
                        context?.showToast("Image Already Downloaded")
                    }

                    //      notifyItemChanged(reversedPosition)
                }
            }
            itemHolder.share.setOnClickListener { v: View? -> callBack.onShareClicked(reversedStatus) }

            itemHolder.imageView.setOnLongClickListener {
                if (!ImagesFragment.isMultiSelect) {
                    ImagesFragment.isMultiSelect = true
                    ImagesFragment.selectedStatusList29.add(reversedStatus)
                    ImagesFragment.imagesList29[reversedPosition].isSelected = true
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    itemHolder.imageView.foreground = drawable
                    callBack2.onMultiSelectModeActivated()
                } else {
//                itemHolder.save.visibility=View.VISIBLE
//                itemHolder.share.visibility=View.VISIBLE
                }
                //notifyDataSetChanged()
                true
            }

            itemHolder.imageView.setOnClickListener { v: View? ->

                if (ImagesFragment.isMultiSelect) {
                    if (ImagesFragment.selectedStatusList29.contains(reversedStatus)) {
                        ImagesFragment.selectedStatusList29.remove(reversedStatus)
                        ImagesFragment.imagesList29[reversedPosition].isSelected = false
                        itemHolder.imageView.foreground = null
                        callBack2.onMultiSelectModeActivated()
                    } else {
                        val drawable =
                            ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                        itemHolder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    itemHolder.imageView.foreground=foregroundDrawable
                        ImagesFragment.selectedStatusList29.add(reversedStatus)
                        ImagesFragment.imagesList29[reversedPosition].isSelected = true
                        callBack2.onMultiSelectModeActivated()
                    }
                    // notifyItemChanged(position)
                } else {
                    itemHolder.save.visibility = View.INVISIBLE
                    itemHolder.share.visibility = View.INVISIBLE
                    ImagesFragment.openSaved = false
                    ImagesFragment.clickedPosition = position
                    callBack.onImageViewClicked(reversedStatus, itemHolder.save.tag)
                }
            }
        }
        else if (getItemViewType(position) == ITEM_TYPE_AD) {
            val adHolder = holder as AdViewHolder
            loadNativeAd(context,adHolder.frame!!,
                RemoteDateConfig.remoteAdSettings.admob_native_dashboard_ad.value,context.layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        }
    }
    override fun getItemCount(): Int {
        return super.getItemCount()
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
    override fun getItemViewType(position: Int): Int {
        return if (position == 2) {
            ITEM_TYPE_AD
        } else {
            ITEM_TYPE_IMAGE
        }
    }
    companion object {
        private val ADAPTER_COMPARATOR = object : DiffUtil.ItemCallback<Status>() {
            override fun areItemsTheSame(oldItem: Status, newItem: Status): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Status, newItem: Status): Boolean {
                return oldItem.path == newItem.path
            }
        }
    }
}
