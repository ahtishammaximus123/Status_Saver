/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.stickers.WhatsAppBasedCode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stickers.R;
import com.example.stickers.stickers.DataArchiver;
import com.example.stickers.stickers.StickerBook;

import java.util.List;

public class StickerPackListAdapter extends RecyclerView.Adapter<StickerPackListItemViewHolder> {

    @NonNull
    private final List<StickerPack> stickerPacks;
    private final Context context;
    @NonNull
    private final OnAddButtonClickedListener onAddButtonClickedListener;
    private int maxNumberOfStickersInARow = 3;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    StickerPackListAdapter(@NonNull List<StickerPack> stickerPacks, @NonNull Context context,
                           @NonNull OnAddButtonClickedListener onAddButtonClickedListener) {
        this.stickerPacks = stickerPacks;
        this.context = context;
        this.onAddButtonClickedListener = onAddButtonClickedListener;

    }

    @NonNull
    @Override
    public StickerPackListItemViewHolder onCreateViewHolder(@NonNull final ViewGroup p0, final int p1) {
        final Context context = p0.getContext();

        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View stickerPackRow = layoutInflater.inflate(R.layout.sticker_packs_list_item, p0, false);
        return new StickerPackListItemViewHolder(stickerPackRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPackListItemViewHolder viewHolder, final int index) {
        //Collections.reverse(stickerPacks);
        StickerPack pack = stickerPacks.get(index);
//        viewHolder.imgTrayIcon.setImageURI(pack.getTrayImageUri());

        Glide.with(context)
                .load(pack.getTrayImageUri())
                .into(viewHolder.imgTrayIcon);
        final Context context = viewHolder.publisherView.getContext();
        viewHolder.publisherView.setText(pack.publisher);
        //viewHolder.filesizeView.setText(Formatter.formatShortFileSize(context, pack.getTotalSize()));

        viewHolder.titleView.setText(pack.name);
        viewHolder.container.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), StickerPackDetailsActivity.class);
            //store index on sharedPref
            sharedPreferences = context.getSharedPreferences("usmanshah", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putInt("POSITION", index);
            editor.apply();

            Log.e("sdssd", "clicked");

            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, pack.identifier);
            view.getContext().startActivity(intent);

        });

        viewHolder.stickerPackDel.setOnClickListener(view -> {
            StickerPack stickerPack = StickerBook.getStickerPackById(pack.identifier);
            StickerBook.deleteStickerPackById(stickerPack.getIdentifier());
            notifyDataSetChanged();
            onAddButtonClickedListener.onDeleted(true);
            Toast.makeText(context, "Sticker deleted successfully...", Toast.LENGTH_SHORT).show();
        });
        viewHolder.menu.setOnClickListener(view -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);
            final PopupMenu popupMenu = new PopupMenu(wrapper, view);
            popupMenu.inflate(R.menu.sticker_pack_menu);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.share:
                            //right your action here
                            DataArchiver.createZipFileFromStickerPack(pack, context);
                            return true;

                        case R.id.delete:
                            //your code here
                            StickerPack stickerPack = StickerBook.getStickerPackById(pack.identifier);
                            StickerBook.deleteStickerPackById(stickerPack.getIdentifier());
                            notifyDataSetChanged();
                            onAddButtonClickedListener.onDeleted(true);
                            Toast.makeText(context, "Pack deleted successfully...", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                    return false;
                }
            });
        });

        viewHolder.imageRowView.removeAllViews();
        //if this sticker pack contains less stickers than the max, then take the smaller size.
        int actualNumberOfStickersToShow = Math.min(maxNumberOfStickersInARow, pack.getStickers().size());
        Log.d("stickerstoshow", "count " + actualNumberOfStickersToShow);
        Log.d("stickerstoshow", "count size " + pack.getStickers().size());
//        for (int i = 0; i < actualNumberOfStickersToShow; i++) {
//            final SimpleDraweeView rowImage = (SimpleDraweeView) LayoutInflater.from(context).inflate(R.layout.sticker_pack_list_item_image, viewHolder.imageRowView, false);
//            rowImage.setImageURI(pack.getSticker(i).getUri());
//
//            final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rowImage.getLayoutParams();
//            final int marginBetweenImages = (viewHolder.imageRowView.getMeasuredWidth() - maxNumberOfStickersInARow * viewHolder.imageRowView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size)) / (maxNumberOfStickersInARow - 1) - lp.leftMargin - lp.rightMargin;
//            if (i != actualNumberOfStickersToShow - 1 && marginBetweenImages > 0) { //do not set the margin for the last image
//                lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin + marginBetweenImages, lp.bottomMargin);
//                rowImage.setLayoutParams(lp);
//            }
//            viewHolder.imageRowView.addView(rowImage);
//        }
        //setAddButtonAppearance(viewHolder.addButton, pack, index);

    }

    private void setAddButtonAppearance(ImageView addButton, StickerPack pack, int index) {
        if (pack.getIsWhitelisted()) {
            //todo addButton.setImageResource(R.drawable.sticker_3rdparty_added);
//            addButton.setImageResource(R.drawable.sticker_3rdparty_added);
            addButton.setClickable(false);
            addButton.setOnClickListener(null);
            addButton.setBackgroundDrawable(null);
        } else {
            addButton.setImageResource(android.R.drawable.stat_sys_download);
            addButton.setOnClickListener(v -> onAddButtonClickedListener.onAddButtonClicked(pack, index));
            TypedValue outValue = new TypedValue();
            addButton.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            addButton.setBackgroundResource(outValue.resourceId);
        }
    }

    @Override
    public int getItemCount() {
        return stickerPacks.size();

    }

    void setMaxNumberOfStickersInARow(int maxNumberOfStickersInARow) {
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow;
            notifyDataSetChanged();
        }
    }

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(StickerPack stickerPack, int pos);

        void onDeleted(boolean flag);

    }
}
