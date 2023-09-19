package com.example.stickers.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList29
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Models.Status
import com.example.stickers.MultiVideoSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig

class VideoAdapter(
    val videoList: List<Status>,
    private val container: ConstraintLayout,
    private val recyclerView: RecyclerView,
    private var context: Activity,
    private val callBack: ImageAdapterCallBack,    private val callBack2: MultiVideoSelectCallback
) : RecyclerView.Adapter<ViewHolder>() {
    private val ITEM_TYPE_IMAGE = 0
    private val ITEM_TYPE_AD = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_TYPE_AD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_ad, parent, false)
            AdViewHolder(view)
        } else {
            // Inflate your regular item layout for regular items
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_video, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_TYPE_IMAGE) {
            val imageHolder = holder as ItemViewHolder
            val status = videoList[position]
            if (status != null && status.isSavedStatus()) {
                holder.save.setImageResource(R.drawable.ic_download_ic__1_)
                holder.save.tag = "saved"
            } else {
                holder.save.setImageResource(R.drawable.ic_download_ic)
                holder.save.tag = "notSaved"
            }
            if (holder.save.tag == "saved") {
                holder.save.isClickable = false
            }
            holder.save.setOnClickListener {
                holder.save.isClickable = true
                if (holder.save.tag !== "saved")
                    callBack.onDownloadClick(status, container)
                videoList[position].setSavedStatus(true)
                callBack.onImageViewClicked(
                    status,
                    holder.save.tag
                )
                notifyItemChanged(position)
            }
            Glide.with(context!!).asBitmap().load(status.file)
                .into(holder.imageView)
            holder.share.setOnClickListener { v: View? -> callBack.onShareClicked(status) }

            if (videoList.isNotEmpty() && position >= 0 && position < videoList.size) {
                if (videoList[position].isSelected) {
                    val drawable =
                        context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                    holder.imageView.foreground = drawable
                } else {
                    holder.imageView.foreground = null
                }
            } else {
                // Handle the case where reversedPosition is out of bounds or selectedStatusList is empty.
                // You can log an error or perform appropriate error handling here.
            }
            holder.imageView.setOnLongClickListener {
                if (!isMultiSelect) {
                    isMultiSelect = true
                    savedSelectedVideoStatusList29.add(status)
                    videoList[position].isSelected = true
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    holder.imageView.foreground = drawable
                    callBack2.onMultiVideoSelectModeActivated()
                }
                //     notifyItemChanged(position)
                true
            }

            holder.imageView.setOnClickListener {
                if (isMultiSelect) {
                    if (savedSelectedVideoStatusList29.contains(status)) {
                        savedSelectedVideoStatusList29.remove(status)
                        videoList[position].isSelected = false
                        holder.imageView.foreground = null
                        callBack2.onMultiVideoSelectModeActivated()
                    } else {
                        val drawable =
                            ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                        holder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    holder.imageView.foreground=foregroundDrawable
                        savedSelectedVideoStatusList29.add(status)
                        videoList[position].isSelected = true
                        callBack2.onMultiVideoSelectModeActivated()
                    }
                    // notifyItemChanged(position)
                } else {
                    callBack.onImageViewClicked(
                        status,
                        holder.save.tag
                    )
                }
            }
        }
        else if (getItemViewType(position) == ITEM_TYPE_AD) {
            val adHolder = holder as AdViewHolder

            loadNativeAd(context,adHolder.frame!!,
                RemoteDateConfig.remoteAdSettings.admob_native_dashboard_ad.value,context.layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})

        }

    }
    fun setLayoutManager() {
        val layoutManager = GridLayoutManager(context, Common.GRID_COUNT)
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
    override fun getItemCount(): Int {
        return videoList.size
    }
}