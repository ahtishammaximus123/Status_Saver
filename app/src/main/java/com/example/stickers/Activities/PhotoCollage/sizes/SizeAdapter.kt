package com.example.stickers.Activities.PhotoCollage.sizes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.R
import com.example.stickers.databinding.ItemSizeBinding

class SizeAdapter(
    private val fileList: MutableList<String>,
    listener: FileListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: FileListener
    private var selectedTheme = 0
    private var lastSelectedTheme = -1
    private lateinit var context: Context

    init {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSizeBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = fileList[position]

        if (holder is MyViewHolder)
            holder.bindData(item, position)

    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class MyViewHolder(private val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: String, position: Int) {
            Log.e("tagSize", item)
            with(binding) {
                text.text = item
                if (position == selectedTheme) {
                    text.setBackgroundColor(
                        ResourcesCompat.getColor(
                            context.resources,
                            R.color.greyColorDark, context.theme
                        )
                    )
                } else {
                    text.setBackgroundColor(
                        ResourcesCompat.getColor(
                            context.resources,
                            R.color.greyColor, context.theme
                        )
                    )
                }
                text.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    dimensionRatio = item
//                    dimensionRatio = "H,$item"
                }
                text.setOnClickListener {
                    listener.onClickImage(item)

                    lastSelectedTheme = selectedTheme
                    selectedTheme = position
                    if (selectedTheme != -1) notifyItemChanged(selectedTheme)
                    if (lastSelectedTheme != -1) notifyItemChanged(lastSelectedTheme)
                }
            }
        }
    }

    interface FileListener {
        fun onClickImage(item: String)
    }
}