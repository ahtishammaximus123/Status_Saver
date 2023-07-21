package com.example.stickers.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.FullScreenVideoActivity
import com.example.stickers.DeletedCallback
import com.example.stickers.Models.StatusesHeaders
import com.example.stickers.R
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import java.util.*

class SavedStatusAdapter(private var imagesList: ArrayList<StatusesHeaders>?, private var callback: DeletedCallback?) : RecyclerView.Adapter<ItemViewHolder>() {


    private var context: Context? = null
    fun getDate(timestamp: Long) :String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
        return date
    }
    fun isToday(date : String): Boolean{
        val today = Calendar.getInstance(Locale.ENGLISH)
        val tdate = DateFormat.format("dd-MM-yyyy",today).toString()
        return date == tdate
    }
    fun isRecent(timestamp: Long): Boolean{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val cTime = calendar.timeInMillis
        val currentTime = Calendar.getInstance(Locale.ENGLISH).timeInMillis
        val differenceInMillis: Long = currentTime - cTime
        val differenceInHours =
            differenceInMillis / 1000L / 60L / 60L // Divide by millis/sec, secs/min, mins/hr
        return differenceInHours < 2
    }
    override fun getItemViewType(position: Int): Int {
        val status = imagesList?.get(position)
        return if(status?.status == null) 0
        else 1
//        val time = status.file.lastModified()
//        val date = getDate(status.file.lastModified())
//        return if(isToday(date) && isRecent(time)) 0
//        else if(isToday(date)) 1
//        else 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        if(viewType ==0) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.saved_header, parent, false)
            return StatusItemHeaderViewHolder(view)
        }
        else         {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.item_saved_files, parent, false)
            return ItemViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if(viewType == 0){
            val headerHolder: StatusItemHeaderViewHolder = holder as StatusItemHeaderViewHolder
            headerHolder.title.text = imagesList?.get(position)?.title ?: ""
        }else {
            val status = imagesList?.get(position)?.status
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
                        imagesList?.removeAt(position)
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
                        context!!.getUriPath(status.file.absolutePath)
                    )
                }
            }
            holder.imageView.setOnClickListener { v: View? ->
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
                    val i = Intent(
                        context,
                        FullScreenImageActivity::class.java
                    )
                    i.action = "delete"
                    i.putExtra("status", status)
                    context!!.startActivity(i)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imagesList?.size ?: 0
    }

}
class StatusItemHeaderViewHolder(itemView: View) : ItemViewHolder(itemView) {
    var title : TextView
    init {
        title = itemView.findViewById(R.id.tvTitle)
    }
}