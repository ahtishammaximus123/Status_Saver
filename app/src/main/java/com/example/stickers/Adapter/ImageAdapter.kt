package com.example.stickers.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Adapter.ItemViewHolder
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Models.Status
import com.example.stickers.MultiSelectCallback
import com.example.stickers.R
import com.example.stickers.ads.showToast
import java.io.File

class ImageAdapter(private val container: ConstraintLayout?,
                   private val callBack: ImageAdapterCallBack,
                   private val callBack2: MultiSelectCallback
)
    : ListAdapter<Status, ItemViewHolder>(ADAPTER_COMPARATOR)
{
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.row_item_status, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val status = getItem(position)

        // Reverse the position calculation to display newest items at the top
        val reversedPosition = itemCount - 1 - position
        val reversedStatus = getItem(reversedPosition)
        if (ImagesFragment.imagesList29.isNotEmpty() && reversedPosition >= 0 && reversedPosition < ImagesFragment.imagesList29.size) {
            if (ImagesFragment.imagesList29[reversedPosition].isSelected) {
                val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                holder.imageView.foreground = drawable
            } else {
                holder.imageView.foreground = null
            }
        } else {
            // Handle the case where reversedPosition is out of bounds or selectedStatusList is empty.
            // You can log an error or perform appropriate error handling here.
        }

        if (reversedStatus.isSavedStatus()) {
            holder.save.setImageResource(R.drawable.ic_download_ic__1_)
            holder.save.tag = "saved"
        } else {

            holder.save.setImageResource(R.drawable.ic_download_ic)
            holder.save.tag = "notSaved"
        }
//        if (!ImagesFragment.isMultiSelect) {
//            holder.imageView.foreground=null
//            holder.save.visibility=View.VISIBLE
//            holder.share.visibility=View.VISIBLE
//        }
//        else{
//            holder.save.visibility=View.INVISIBLE
//            holder.share.visibility=View.INVISIBLE
//        }
        // Load the image for the reversed status
        Glide.with(context!!)
            .load(reversedStatus.file)
            .into(holder.imageView)

        if (holder.save.tag == "saved") {
            holder.save.isClickable = false
        } else {
            holder.save.setOnClickListener {
                holder.save.isClickable = true
                if (holder.save.tag != "saved")
                {
                    holder.save.setImageResource(R.drawable.ic_download_ic)
                    callBack.onDownloadClick(reversedStatus, container)
                    holder.save.visibility=View.INVISIBLE
                    holder.share.visibility=View.INVISIBLE
                    ImagesFragment.openSaved =false
                    ImagesFragment.clickedPosition = position
                    callBack.onImageViewClicked(reversedStatus, holder.save.tag)
                    reversedStatus.setSavedStatus(true)
                    notifyItemChanged(reversedPosition)

                }
                else{
                    context?.showToast("Image Already Downloaded")
                }

          //      notifyItemChanged(reversedPosition)
            }
        }
        holder.share.setOnClickListener { v: View? -> callBack.onShareClicked(reversedStatus) }

        holder.imageView.setOnLongClickListener {
            if (!ImagesFragment.isMultiSelect) {
                ImagesFragment.isMultiSelect = true
                ImagesFragment.selectedStatusList29.add(reversedStatus)
                ImagesFragment.imagesList29[reversedPosition].isSelected=true
                val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                holder.imageView.foreground = drawable
               callBack2.onMultiSelectModeActivated()
            }
            else{
//                holder.save.visibility=View.VISIBLE
//                holder.share.visibility=View.VISIBLE
            }
              //notifyDataSetChanged()
            true
        }

        holder.imageView.setOnClickListener { v: View? ->

            if (ImagesFragment.isMultiSelect) {
                if (ImagesFragment.selectedStatusList29.contains(reversedStatus)) {
                    ImagesFragment.selectedStatusList29.remove(reversedStatus)
                    ImagesFragment.imagesList29[reversedPosition].isSelected=false
                    holder.imageView.foreground=null
                    callBack2.onMultiSelectModeActivated()
                } else {
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    holder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    holder.imageView.foreground=foregroundDrawable
                    ImagesFragment.selectedStatusList29.add(reversedStatus)
                    ImagesFragment.imagesList29[reversedPosition].isSelected=true
                    callBack2.onMultiSelectModeActivated()
                }
                // notifyItemChanged(position)
            } else {
                holder.save.visibility=View.INVISIBLE
                holder.share.visibility=View.INVISIBLE
                ImagesFragment.openSaved =false
                ImagesFragment.clickedPosition = position
                callBack.onImageViewClicked(reversedStatus, holder.save.tag)
            }


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
