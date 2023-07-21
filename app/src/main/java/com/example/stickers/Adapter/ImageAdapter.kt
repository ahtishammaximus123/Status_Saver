package com.example.stickers.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.ImageAdapterCallBack
import com.example.stickers.Adapter.ItemViewHolder
import com.bumptech.glide.Glide
import com.example.stickers.Models.Status
import com.example.stickers.R
import java.io.File

class ImageAdapter(private val container: ConstraintLayout?,
    private val callBack: ImageAdapterCallBack
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
        if (status.isSavedStatus()) {
            holder.save.setImageResource(R.drawable.ic_download_ic__1_)
            holder.save.tag = "saved"
        } else {
            holder.save.setImageResource(R.drawable.ic_download_ic)
            holder.save.tag = "notSaved"
        }


        //Picasso.with(context).load(status.getFile()).into(holder.imageView);
        Glide.with(context!!)
            .load(status.file)
            .into(holder.imageView)
        if (holder.save.tag == "saved") {
            holder.save.isClickable = false
        } else {
            holder.save.setOnClickListener {
                holder.save.isClickable = true
                if (holder.save.tag != "saved")
                callBack.onDownloadClick(status, container)
                getItem(position).setSavedStatus(true)
                notifyItemChanged(position)
            }
        }
        holder.share.setOnClickListener { v: View? -> callBack.onShareClicked(status) }
        holder.imageView.setOnClickListener { v: View? ->
            callBack.onImageViewClicked(
                status,
                holder.save.tag
            )
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