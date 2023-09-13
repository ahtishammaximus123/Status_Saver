package com.example.stickers.Adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.MultiVideoSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.saveStatus
import com.example.stickers.ads.showInterAd
import com.example.stickers.ads.showToast
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.shareFile
import java.io.*
import java.util.*


class VideoAdapter30plus(
    val mList: MutableList<StatusDocFile>,
    val context1: FragmentManager,
    private val context: Activity,
    private val downloadListener: () -> Unit,
    private val callBack: MultiVideoSelectCallback
) : RecyclerView.Adapter<ItemViewHolder>() {

    val FILE_PROVIDER_AUTHORITY = "com.example.videodownloader"

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        Log.d("tree", "onCreateViewHolder ")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_video, parent, false)
        return ItemViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        try {
            val reversedPosition = mList.size - 1 - position
            val itemsViewModel = mList[reversedPosition]
            Log.d("tree", "onBindViewHolder " + itemsViewModel.file.uri)

            if (itemsViewModel.isSavedStatus()) {
                holder.save.setImageResource(R.drawable.ic_download_ic__1_)
                holder.save.tag = "saved"
            } else {
                holder.save.setImageResource(R.drawable.ic_download_ic)
                holder.save.tag = "notSaved"
            }

            Glide.with(context)
                .load(itemsViewModel.file.uri)
                .into(holder.imageView)

            holder.share.setOnClickListener { v ->
                itemsViewModel.file.let {
                    context.shareFile(it.uri, context1)
                }
            }

            if (mList.isNotEmpty() && reversedPosition >= 0 && reversedPosition < mList.size) {
                if (mList[reversedPosition].isSelected) {
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
                    savedSelectedVideoStatusList.add(itemsViewModel)
                    mList[reversedPosition].isSelected=true
                    val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                    holder.imageView.foreground = drawable
                    callBack.onMultiVideoSelectModeActivated()
                }
                //     notifyItemChanged(position)
                true
            }

            holder.imageView.setOnClickListener {
                if (isMultiSelect) {
                    if (savedSelectedVideoStatusList.contains(itemsViewModel)) {
                        savedSelectedVideoStatusList.remove(itemsViewModel)
                        mList[reversedPosition].isSelected=false
                        holder.imageView.foreground=null
                        callBack.onMultiVideoSelectModeActivated()
                    } else {
                        val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                        holder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    holder.imageView.foreground=foregroundDrawable
                        savedSelectedVideoStatusList.add(itemsViewModel)
                        mList[reversedPosition].isSelected=true
                        callBack.onMultiVideoSelectModeActivated()
                    }
                    // notifyItemChanged(position)
                } else {
                    val i = Intent(context, FullScreenVideoActivity::class.java)
                    ItemsViewModel = mList[reversedPosition]
                    i.action = "sa"
                    i.putExtra("is30Plus", true)
                    i.putExtra("img_tag", holder.save.tag.toString())
                    Log.e("img_tag", holder.save.tag.toString())
                    context.startActivity(i)
                }
            }


            holder.save.setOnClickListener { v ->
                if (holder.save.tag != "saved") {
                    context.showInterAd(RemoteDateConfig.remoteAdSettings.inter_download_status) {
                        if (reversedPosition < mList.size) {
                            try {
                                val newItem = mList[reversedPosition]
                                try {
                                    context.saveStatus(v, newItem)
                                    newItem.setSavedStatus(true)

                                    notifyDataSetChanged()

                                    downloadListener.invoke()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Corrupted Video, Can't be Saved", Toast.LENGTH_LONG).show()
                                }
                            } catch (exp: ArrayIndexOutOfBoundsException) {
                            }
                        }
                    }
                } else {
                    context.showToast("Video Already Downloaded")
                }
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
}

