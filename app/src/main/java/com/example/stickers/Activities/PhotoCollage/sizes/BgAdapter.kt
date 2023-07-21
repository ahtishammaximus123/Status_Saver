package com.example.stickers.Activities.PhotoCollage.sizes

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.Models.BgModel
import com.example.stickers.databinding.ItemBgBinding
import com.example.stickers.databinding.ItemSizeBinding

class BgAdapter(
    private val fileList: MutableList<BgModel>,
    listener: FileListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: FileListener

    init {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBgBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = fileList[position]
        if (holder is MyViewHolder)
            holder.bindData(item)

    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class MyViewHolder(private val binding: ItemBgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: BgModel) {
            with(binding) {
                try {
                    if (item.isColor)
                        image.setBackgroundColor(item.background)
                    else
                        image.setImageResource(item.background)
                } catch (exp: Resources.NotFoundException){
                }
                image.setOnClickListener {
                    listener.onClickImage(item)
                }
            }
        }
    }

    interface FileListener {
        fun onClickImage(item: BgModel)
    }
}