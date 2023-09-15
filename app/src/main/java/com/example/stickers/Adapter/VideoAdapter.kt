package com.example.stickers.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList29
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Models.Status
import com.example.stickers.MultiVideoSelectCallback
import com.example.stickers.R

class VideoAdapter(
    val videoList: List<Status>,
    private val container: ConstraintLayout,
    private val callBack: ImageAdapterCallBack,    private val callBack2: MultiVideoSelectCallback
) : RecyclerView.Adapter<ItemViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.row_item_video, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
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
                val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
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
                videoList[position].isSelected=true
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
                    videoList[position].isSelected=false
                    holder.imageView.foreground=null
                    callBack2.onMultiVideoSelectModeActivated()
                } else {
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    holder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    holder.imageView.foreground=foregroundDrawable
                    savedSelectedVideoStatusList29.add(status)
                    videoList[position].isSelected=true
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

    override fun getItemCount(): Int {
        return videoList.size
    }
}