package com.example.stickers.Activities.sticker

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stickers.Models.StickerModel
import com.example.stickers.R
import com.example.stickers.databinding.ItemStickerBinding


class StickerAdapter(
    private val contexts: Activity,
    private val stickerList: List<StickerModel> = arrayListOf(),
    listener: StickerInterface
) : RecyclerView.Adapter<StickerAdapter.MyViewHolder>() {
    private var listener: StickerInterface
    private var options: RequestOptions? = null

    init {
        this.listener = listener
        options = RequestOptions().placeholder(R.drawable.ic_emoji).error(R.drawable.ic_emoji)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStickerBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bindData(stickerList[position])
    }

    override fun getItemCount(): Int {
        return stickerList.size
    }

    inner class MyViewHolder(val view: ItemStickerBinding) : RecyclerView.ViewHolder(view.root) {
        fun bindData(model: StickerModel) {
            with(view) {

                Glide.with(contexts)
                    .load(model.path)
                    .apply(options!!).into(image)
                image.setOnClickListener {
                    listener.onStickerClick(model)
                }
            }
        }
    }
    interface StickerInterface {
        fun onStickerClick(model: StickerModel)
    }
}
