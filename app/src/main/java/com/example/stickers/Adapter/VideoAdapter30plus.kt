package com.example.stickers.Adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.isMultiSelect
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.savedSelectedVideoStatusList
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.MultiVideoSelectCallback
import com.example.stickers.R
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.saveStatus
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity
import com.example.stickers.ads.loadNativeAd

import com.example.stickers.ads.showToast
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.shareFile
import java.io.*
import java.util.*


class VideoAdapter30plus(
    val mList: MutableList<StatusDocFile>,
    private val recyclerView: RecyclerView,
    val context1: FragmentManager,
    private val context: Activity,
    private val downloadListener: () -> Unit,
    private val callBack: MultiVideoSelectCallback
) : RecyclerView.Adapter<ViewHolder>() {

    private val ITEM_TYPE_IMAGE = 0
    private val ITEM_TYPE_AD = 1

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_TYPE_AD) {
            // Inflate your ad layout for ad items
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

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM_TYPE_IMAGE) {
            val imageHolder = holder as ItemViewHolder
            try {
                val reversedPosition = mList.size - 1 - position
                val itemsViewModel = mList[reversedPosition]
                Log.d("tree", "onBindViewHolder " + itemsViewModel.file.uri)

                if (itemsViewModel.isSavedStatus()) {
                    imageHolder.save.setImageResource(R.drawable.ic_download_ic__1_)
                    imageHolder.save.tag = "saved"
                } else {
                    imageHolder.save.setImageResource(R.drawable.ic_download_ic)
                    imageHolder.save.tag = "notSaved"
                }

                Glide.with(context)
                    .load(itemsViewModel.file.uri)
                    .into(imageHolder.imageView)

                imageHolder.share.setOnClickListener { v ->
                    itemsViewModel.file.let {
                        context.shareFile(it.uri, context1)
                    }
                }

                if (mList.isNotEmpty() && reversedPosition >= 0 && reversedPosition < mList.size) {
                    if (mList[reversedPosition].isSelected) {
                        val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                        imageHolder.imageView.foreground = drawable
                    } else {
                        imageHolder.imageView.foreground = null
                    }
                } else {
                    // Handle the case where reversedPosition is out of bounds or selectedStatusList is empty.
                    // You can log an error or perform appropriate error handling here.
                }
                imageHolder.imageView.setOnLongClickListener {
                    if (!isMultiSelect) {
                        isMultiSelect = true
                        savedSelectedVideoStatusList.add(itemsViewModel)
                        mList[reversedPosition].isSelected=true
                        val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                        imageHolder.imageView.foreground = drawable
                        callBack.onMultiVideoSelectModeActivated()
                    }
                    //     notifyItemChanged(position)
                    true
                }

                imageHolder.imageView.setOnClickListener {
                    if (isMultiSelect) {
                        if (savedSelectedVideoStatusList.contains(itemsViewModel)) {
                            savedSelectedVideoStatusList.remove(itemsViewModel)
                            mList[reversedPosition].isSelected=false
                            imageHolder.imageView.foreground=null
                            callBack.onMultiVideoSelectModeActivated()
                        } else {
                            val drawable = ContextCompat.getDrawable(context, R.drawable.selected_back)
                            imageHolder.imageView.foreground = drawable

//                    val foregroundDrawable = ColorDrawable(Color.parseColor("#C9ACACAC"))
//                    imageHolder.imageView.foreground=foregroundDrawable
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
                        i.putExtra("img_tag", imageHolder.save.tag.toString())
                        Log.e("img_tag", imageHolder.save.tag.toString())
                        context.startActivity(i)
                    }
                }


                imageHolder.save.setOnClickListener { v ->
                    if (imageHolder.save.tag != "saved") {

                        if (reversedPosition < mList.size) {
                            try {
                                val newItem = mList[reversedPosition]
                                try {
                                    context.saveStatus(v, newItem)
                                    newItem.setSavedStatus(true)

                                    notifyDataSetChanged()

                                    downloadListener.invoke()
                                    val i = Intent(context, FullScreenVideoActivity::class.java)
                                    ItemsViewModel = mList[reversedPosition]
                                    i.action = "sa"
                                    i.putExtra("is30Plus", true)
                                    i.putExtra("img_tag", imageHolder.save.tag.toString())
                                    Log.e("img_tag", imageHolder.save.tag.toString())
                                    context.startActivity(i)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Corrupted Video, Can't be Saved", Toast.LENGTH_LONG).show()
                                }
                            } catch (exp: ArrayIndexOutOfBoundsException) {
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
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
}

