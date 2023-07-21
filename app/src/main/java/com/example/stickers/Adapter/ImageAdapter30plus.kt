package com.example.stickers.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stickers.Activities.FullScreenImageActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment.Companion.ItemsViewModel
import com.example.stickers.Models.StatusDocFile
import com.example.stickers.R
import com.example.stickers.Utils.saveStatus
import com.example.stickers.ads.showInterAd
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings
import com.example.stickers.app.shareFile
import java.io.*
import java.util.*


class ImageAdapter30plus(
    private val context: Activity,
    private val downloadListener: () -> Unit
) : ListAdapter<StatusDocFile, ImageAdapter30plus.ItemImageViewHolder>(ADAPTER_COMPARATOR) {

    val FILE_PROVIDER_AUTHORITY = "com.example.videodownloader"

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImageViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        Log.d("tree", "onCreateViewHolder ")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_status, parent, false)
        return ItemImageViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ItemImageViewHolder, position: Int) {
        val status = getItem(position)
        ItemsViewModel = status
        Log.d("tree", "onBindViewHolder " + status.file.uri)

        if (ItemsViewModel?.isSavedStatus() == true) {
            holder.save.setImageResource(R.drawable.ic_download_ic__1_)
            holder.save.tag = "saved"
        } else {
            holder.save.setImageResource(R.drawable.ic_download_ic)
            holder.save.tag = "notSaved"

        }
        Glide.with(context)
            .load(ItemsViewModel!!.file.uri)
            .into(holder.imageView)

        holder.share.setOnClickListener { v ->
            ItemsViewModel = status
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "image/*"
//            val link = "http://play.google.com/store/apps/details?id=" + context.packageName
//            intent.putExtra(
//                Intent.EXTRA_TEXT,
//                "You can save all WhatsApp Status for free and fast. \n Download it here: $link"
//            )
//            intent.putExtra(Intent.EXTRA_STREAM, ItemsViewModel?.file?.uri)
//            val shareSub = "Shared via Video Downloader and Editor"
//            intent.putExtra(Intent.EXTRA_TEXT, shareSub)
//            context.startActivity(intent)
            ItemsViewModel?.file?.uri?.let {

                context.shareFile(it)
            }
        }
        holder.imageView.setOnClickListener { v ->
            val i = Intent(
                context,
                FullScreenImageActivity::class.java
            )
            i.action = "asa"
            ItemsViewModel = status
//            i.putExtra("status", ItemsViewModel)
            i.putExtra("is30Plus", true)
            i.putExtra("img_tag", holder.save.getTag().toString())
            Log.e("img_tag", holder.save.getTag().toString())
            context.startActivity(i)
        }
        holder.save.setOnClickListener { v ->
            if (holder.save.tag != "saved")
                context.showInterAd(remoteAdSettings.inter_download_status) {
                    ItemsViewModel = status
                    context.saveStatus(v, status)

                    ItemsViewModel?.setSavedStatus(true)
                    notifyItemChanged(position)

                    downloadListener.invoke()
                }

//            copyFile(context.getExternalFilesDir("photo_editor")!!.getAbsolutePath(), ItemsViewModel.title, outDirCopy.absolutePath)
//            copyFile(ItemsViewModel.file.uri.path, ItemsViewModel.title, outDirCopy.absolutePath)
//            Common.copyFile(ItemsViewModel, context, container)
//            imagesList!![position].isSavedStatus = true
        }
    }

    private fun saveintopath(imgUri: Uri, file: File) {

        val chunkSize = 1024 // We'll read in one kB at a time

        val imageData = ByteArray(chunkSize)

        var `in`: InputStream? = null
        var out: OutputStream? = null


        try {
            `in` = context.getContentResolver().openInputStream(imgUri)
            val out =
                FileOutputStream(file) // I'm assuming you already have the File object for where you're writing to
            var bytesRead: Int
            while (`in`?.read(imageData).also { bytesRead = it!! }!! > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)))
            }
        } catch (ex: Exception) {
            Log.e("Something went wrong.", ex.toString())
        } finally {
            `in`?.close()
            out?.close()
        }
    }

    private fun copyFile(inputPath: String?, inputFile: String, outputPath: String) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        var outFile: File? = null
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            outFile = File(outputPath + File.separator + inputFile)
            if (!dir.exists()) {
                dir.mkdirs()
            }
//            `in` = FileInputStream(inputPath + File.separator + uriStringFileName)
            `in` = FileInputStream(inputPath)
            out = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null

            // write the output file (You have now copied the file)
            out.flush()
            out.close()
            out = null
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(outFile)
//            sendBroadcast(intent)
            Log.e("tree", "done outFile path  is : " + outFile.absolutePath)
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tree", "FileNotFoundException : " + fnfe1.message)
        } catch (e: Exception) {
            Log.e("tree", "Exception : " + e.message)
        }
    }


    // Holds the views for adding it to image and text
    class ItemImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var save: ImageView
        var share: ImageView
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.ivThumbnail)
            save = itemView.findViewById(R.id.save)
            share = itemView.findViewById(R.id.share)
        }
    }

    companion object {
        private val ADAPTER_COMPARATOR = object : DiffUtil.ItemCallback<StatusDocFile>() {
            override fun areItemsTheSame(oldItem: StatusDocFile, newItem: StatusDocFile): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: StatusDocFile,
                newItem: StatusDocFile
            ): Boolean {
                return oldItem.path == newItem.path
            }
        }
    }
}