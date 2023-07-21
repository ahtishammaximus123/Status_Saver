/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.stickers.WhatsAppBasedCode;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class StickerPreviewViewHolder extends RecyclerView.ViewHolder {

    //    public SimpleDraweeView stickerPreviewView;
    public ImageView stickerPreviewView;
    public CheckBox cbDelete;

    StickerPreviewViewHolder(final View itemView, StickerPreviewAdapter.StickerClicked listener) {
        super(itemView);
        //todo
        stickerPreviewView = itemView.findViewById(R.id.sticker_preview);
        cbDelete = itemView.findViewById(R.id.cb_delete);

        cbDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                listener.onCheckBoxChecked(getAdapterPosition(), itemView, isChecked);
            }
        });

        stickerPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAddSticker(getAdapterPosition(), itemView);
            }
        });
    }
}