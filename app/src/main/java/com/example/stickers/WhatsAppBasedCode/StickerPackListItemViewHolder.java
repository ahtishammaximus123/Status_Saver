/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.stickers.WhatsAppBasedCode;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.R;

public class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {

    View container;
    ImageView imgTrayIcon;
    TextView titleView;
    TextView publisherView;
    ImageView menu;
    ImageView stickerPackDel;
    //    TextView fileSizeView;
//    ImageView addButton;
//    ImageView shareButton;
    LinearLayout imageRowView;

    StickerPackListItemViewHolder(final View itemView) {
        super(itemView);
        container = itemView;
        imgTrayIcon = itemView.findViewById(R.id.imgTrayIcon);
        titleView = itemView.findViewById(R.id.sticker_pack_title);
        publisherView = itemView.findViewById(R.id.sticker_pack_publisher);
        menu = itemView.findViewById(R.id.menu_stickerPack);
//        fileSizeView = itemView.findViewById(R.id.sticker_pack_filesize);
//        addButton = itemView.findViewById(R.id.add_button_on_list);
//        shareButton = itemView.findViewById(R.id.export_button_on_list)
        imageRowView = itemView.findViewById(R.id.sticker_packs_list_item_image_list);
        stickerPackDel = itemView.findViewById(R.id.stickerPackDel);
    }
}