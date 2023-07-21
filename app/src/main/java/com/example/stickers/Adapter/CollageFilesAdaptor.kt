package com.example.stickers.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stickers.Models.FileModel
import com.example.stickers.R
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import com.example.stickers.databinding.ItemCollageFileBinding

class CollageFilesAdaptor(
    private val context: Context,
    fileList: MutableList<FileModel>,
    listener: FileListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var fileList: MutableList<FileModel> = mutableListOf()

    private var listener: FileListener
    private val options by lazy {
        RequestOptions().placeholder(R.drawable.funny).error(R.drawable.funny)
    }

    init {
        this.fileList = fileList
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCollageFileBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder)
            holder.bindData(fileList[position], position)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class MyViewHolder(private val binding: ItemCollageFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: FileModel, position: Int) {
            with(binding) {

                Glide.with(context).load(item.filePath).apply(options).into(ivThumbnail)
                root.setOnClickListener {
                    listener.onFileClick(item)
                }
                delete.setOnClickListener {
                    listener.onFileDelete(item, position)
                }
                share.setOnClickListener {
                    context.shareFile(context.getUriPath(item.filePath))
                }
            }
        }
    }

    interface FileListener {
        fun onFileClick(fileModel: FileModel)
        fun onFileDelete(fileModel: FileModel , position: Int)
    }
}