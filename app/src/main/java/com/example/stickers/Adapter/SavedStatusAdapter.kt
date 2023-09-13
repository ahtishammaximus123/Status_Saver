package com.example.stickers.Adapter

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.DeletedCallback
import com.example.stickers.Models.Status
import com.example.stickers.R
import com.example.stickers.SavedMultiSelectCallback
import com.example.stickers.app.shareFile

class SavedStatusAdapter(
    private var imagesList: ArrayList<Status>?,
    private var callback: DeletedCallback?,
    private val callBack: SavedMultiSelectCallback,
    val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<ItemViewHolder>() {
    private var context: Context? = null

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_saved_files, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val status = imagesList?.get(position)
        val reversedPosition = imagesList?.size?.minus(1)?.minus(position) ?: -1

        holder.save.setImageDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ic_delete_ic
            )
        )
        holder.share.visibility = View.VISIBLE
        holder.save.visibility = View.VISIBLE
        if (status?.isVideo == true) {
            holder.play.visibility = View.VISIBLE
        } else holder.play.visibility = View.GONE
        status?.let {
            Glide.with(context!!).load(status.file.absolutePath).into(holder.imageView)
        }

        holder.imageView.setOnClickListener { v: View? ->

        }

        if (imagesList?.isNotEmpty() == true && reversedPosition >= 0 && reversedPosition < imagesList?.size!!) {
            if (imagesList!![reversedPosition].isSelected) {
                val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.selected_back) }
                holder.imageView.foreground = drawable
            } else {
                holder.imageView.foreground = null
            }
        }

        holder.imageView.setOnLongClickListener {
            if (!ImagesFragment.isSavedMultiSelect) {
                ImagesFragment.isSavedMultiSelect = true
                ImagesFragment.savedSelectedStatusList.add(status)
                imagesList!![reversedPosition].isSelected = true

                val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                holder.imageView.foreground = drawable
                callBack.onSavedMultiSelectModeActivated()
            }
            true
        }

        holder.imageView.setOnClickListener {
            if (ImagesFragment.isSavedMultiSelect) {
                if (ImagesFragment.savedSelectedStatusList.contains(status)) {
                    ImagesFragment.savedSelectedStatusList.remove(status)
                    imagesList!![reversedPosition].isSelected = false
                    holder.imageView.foreground = null
                    callBack.onSavedMultiSelectModeActivated()
                } else {
                    val drawable = ContextCompat.getDrawable(context!!, R.drawable.selected_back)
                    holder.imageView.foreground = drawable
                    ImagesFragment.savedSelectedStatusList.add(status)
                    imagesList!![reversedPosition].isSelected = true
                    callBack.onSavedMultiSelectModeActivated()
                }
            } else {
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

        holder.save.setOnClickListener { view: View? ->

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

        holder.share.setOnClickListener { v: View? ->
            status?.let {
                context?.shareFile(
                    status.file.toUri(),
                    supportFragmentManager
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return imagesList?.size ?: 0
    }
}
