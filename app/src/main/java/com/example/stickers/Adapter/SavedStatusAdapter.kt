package com.example.stickers.Adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.openSaved
import com.example.stickers.DeletedCallback
import com.example.stickers.Models.Status
import com.example.stickers.R
import com.example.stickers.SavedMultiSelectCallback
import com.example.stickers.Utils.Common
import com.example.stickers.WhatsAppBasedCode.StickerPackListActivity
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.shareFile

class SavedStatusAdapter(
    private var imagesList: ArrayList<Status>?,
    private var callback: DeletedCallback?,
    private val callBack: SavedMultiSelectCallback,
    private val context: Activity,
    private val recyclerView: RecyclerView,
    val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_AD = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            // Inflate your regular item layout for regular items
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_saved_files, parent, false)
            ItemViewHolder(view)
        } else {
            // Inflate your ad item layout for ad items
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_ad, parent, false)
            AdViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            val itemHolder = holder as ItemViewHolder
            val status = imagesList?.get(position)

            val reversedPosition = imagesList?.size?.minus(1)?.minus(position) ?: -1

            itemHolder.save.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_delete_ic
                )
            )
            itemHolder.share.visibility = View.VISIBLE
            itemHolder.save.visibility = View.VISIBLE
            if (status?.isVideo == true) {
                itemHolder.play.visibility = View.VISIBLE
            } else itemHolder.play.visibility = View.GONE
            status?.let {
                Glide.with(context!!).load(status.file.absolutePath).into(itemHolder.imageView)
            }

            itemHolder.imageView.setOnClickListener { v: View? ->

            }

            if (imagesList?.isNotEmpty() == true && reversedPosition >= 0 && reversedPosition < imagesList?.size!!) {
                if (imagesList!![reversedPosition].isSelected) {
                    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                    itemHolder.imageView.foreground = drawable
                } else {
                    itemHolder.imageView.foreground = null
                }
            }

            itemHolder.imageView.setOnLongClickListener {
                if (!ImagesFragment.isSavedMultiSelect) {
                    ImagesFragment.isSavedMultiSelect = true
                    ImagesFragment.savedSelectedStatusList.add(status)
                    imagesList!![reversedPosition].isSelected = true

                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    itemHolder.imageView.foreground = drawable
                    callBack.onSavedMultiSelectModeActivated()
                }
                true
            }

            itemHolder.imageView.setOnClickListener {
                if (ImagesFragment.isSavedMultiSelect) {
                    if (ImagesFragment.savedSelectedStatusList.contains(status)) {
                        ImagesFragment.savedSelectedStatusList.remove(status)
                        imagesList!![reversedPosition].isSelected = false
                        itemHolder.imageView.foreground = null
                        callBack.onSavedMultiSelectModeActivated()
                    } else {
                        val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                        itemHolder.imageView.foreground = drawable
                        ImagesFragment.savedSelectedStatusList.add(status)
                        imagesList!![reversedPosition].isSelected = true
                        callBack.onSavedMultiSelectModeActivated()
                    }
                } else {
                    openSaved=true
                    if (status?.isVideo == true) {
                        val i = Intent(
                            context,
                            FullScreenVideoActivity::class.java
                        )
                        i.action = "delete"
                        i.putExtra("status", status)
                        context?.startActivity(i)

                        Log.e("tagS**", "tagS**- $status")
                    } else {
                        val i = Intent(context, FullScreenImageActivity::class.java)
                        i.action = "delete"
                        i.putExtra("status", status)
                        ImagesFragment.clickedPosition = position
                        ImagesFragment.openSaved=true
                        context!!.startActivity(i)
                    }
                }
            }

            itemHolder.save.setOnClickListener { view: View? ->

                val dialog = Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.delete_dialog)

                val okButton = dialog.findViewById<TextView>(R.id.grant_permission)
                val cancelDialog = dialog.findViewById<TextView>(R.id.button)
                okButton.setOnClickListener {
                    if (status?.file?.delete() == true) {
                        imagesList?.removeAt(reversedPosition)
                        notifyDataSetChanged()
                        callback?.onDeleted(true)
                        Toast.makeText(
                            context,
                            "File Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else Toast.makeText(
                        context,
                        "Unable to Delete File",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
                cancelDialog.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            itemHolder.share.setOnClickListener { v: View? ->
                status?.let {
                    context?.shareFile(
                        status.file.toUri(),
                        supportFragmentManager
                    )
                }
            }

        } else if (getItemViewType(position) == VIEW_TYPE_AD) {
            val adHolder = holder as AdViewHolder
            loadNativeAd(context!!,adHolder.frame!!,
                RemoteDateConfig.remoteAdSettings.admob_native_dashboard_ad.value,context!!.layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})
        }



    }


    fun setLayoutManager() {
        val layoutManager = GridLayoutManager(StickerPackListActivity.context, Common.GRID_COUNT)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == VIEW_TYPE_AD) {
                    Common.GRID_COUNT // For ad items, span the entire row
                } else {
                    1 // For regular items, occupy one span
                }
            }
        }
        recyclerView.layoutManager = layoutManager
    }
    override fun getItemCount(): Int {
        return imagesList?.size?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 2) {
            VIEW_TYPE_AD
        } else {
            VIEW_TYPE_ITEM
        }
    }
}
