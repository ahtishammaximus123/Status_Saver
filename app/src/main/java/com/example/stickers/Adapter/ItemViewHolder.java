package com.example.stickers.Adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.stickers.R;


public class ItemViewHolder extends RecyclerView.ViewHolder{

    public ImageView save, share;
    public ImageView imageView;
    public ImageView play;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.ivThumbnail);
        save = itemView.findViewById(R.id.save);
        share = itemView.findViewById(R.id.share);
        play  = itemView.findViewById(R.id.imageView15);

    }
}