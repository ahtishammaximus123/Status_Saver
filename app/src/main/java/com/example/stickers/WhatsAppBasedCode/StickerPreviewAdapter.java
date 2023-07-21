/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.stickers.WhatsAppBasedCode;

import static com.example.stickers.app.ExtensionFuncKt.length;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.R;

import java.io.File;


public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {

    @NonNull
    private final StickerPack stickerPack;
    StickerClicked mListener;

    //    private final int cellSize;
//    private int cellLimit;
//    private int cellPadding;
    private final int errorResource;

    private final LayoutInflater layoutInflater;

    public StickerPreviewAdapter(@NonNull final LayoutInflater layoutInflater, final int errorResource, @NonNull final StickerPack stickerPack, StickerClicked mListener) {
//        this.cellSize = cellSize;
//        this.cellPadding = cellPadding;
//        this.cellLimit = 0;
        this.layoutInflater = layoutInflater;
        this.errorResource = errorResource;
        this.stickerPack = stickerPack;
        this.mListener = mListener;
    }

    Context context;

    @NonNull
    @Override
    public StickerPreviewViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        //todo
        context = viewGroup.getContext();
        View itemView = layoutInflater.inflate(R.layout.sticker_image, viewGroup, false);
        StickerPreviewViewHolder vh = new StickerPreviewViewHolder(itemView, mListener);

        ViewGroup.LayoutParams layoutParams = vh.stickerPreviewView.getLayoutParams();
//        layoutParams.height = cellSize;
//        layoutParams.width = cellSize;
//        vh.stickerPreviewView.setLayoutParams(layoutParams);
//        vh.stickerPreviewView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPreviewViewHolder stickerPreviewViewHolder, final int i) {

        //todo old on bind had only this
//        Sticker thisSticker = stickerPack.getSticker(i);
//        Context thisContext = stickerPreviewViewHolder.stickerPreviewView.getContext();
//        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
//        stickerPreviewViewHolder.stickerPreviewView.setImageURI(thisSticker.getUri());

        //if position is greater than stickers count then display placeholder/ add sticker image
        //else show sticker

        if (i < stickerPack.getStickers().size()) {
            Sticker thisSticker = stickerPack.getSticker(i);
            stickerPreviewViewHolder.cbDelete.setVisibility(View.VISIBLE);
            Context thisContext = stickerPreviewViewHolder.stickerPreviewView.getContext();
            stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
            stickerPreviewViewHolder.stickerPreviewView.setImageURI(thisSticker.getUri());
            stickerPreviewViewHolder.stickerPreviewView.setTag("no_plus");

            File file =new File(thisSticker.getUri().getPath());
//            Uri newUri = Uri.fromFile(file);
//            getApplicationContext().getContentResolver().openFileDescriptor(newUri,"r" );
            long size  = length(thisContext.getContentResolver(), thisSticker.getUri());
            Log.e("tag**", file.getAbsolutePath()+"\n Size "+size);
        } else {
            stickerPreviewViewHolder.cbDelete.setVisibility(View.GONE);
            stickerPreviewViewHolder.stickerPreviewView.setImageResource(R.drawable.ic_add_new_sticker);
            stickerPreviewViewHolder.stickerPreviewView.setTag("plus");
        }


        //implementing new onClickListener which will work on only those where sticker can be added


//        stickerPreviewViewHolder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("sticasasker", "onClick: getAdapterPosition : " + stickerPreviewViewHolder.getAdapterPosition());
//                // these both IFs work fine one is small in code other is large in code
//
//                if (stickerPack.getStickers().size() == stickerPreviewViewHolder.getAdapterPosition()) {
//                    Log.e("sticker", "onClick: Click Accepted add sticker fun call here : ");
//                }
//
//                // these both work
//
//                if (stickerPack.getStickers().size() <= stickerPreviewViewHolder.getAdapterPosition()) {
//                    if (stickerPreviewViewHolder.getAdapterPosition() == stickerPack.getStickers().size()) {
//                        Log.e("sticker", "onClick: Click Accepted add sticker fun call here : ");
//                    }
//                }
//            }
//        });
//

        //        stickerPreviewViewHolder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImageView image = new ImageView(thisContext);
//                image.setImageURI(thisSticker.getUri());
//                AlertDialog alertDialog = new AlertDialog.Builder(thisContext)
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if (stickerPack.getStickers().size() > 3 || !WhitelistCheck.isWhitelisted(thisContext, stickerPack.getIdentifier())) {
//                                    dialogInterface.dismiss();
//                                    stickerPack.deleteSticker(thisSticker);
//                                    Activity thisActivity = ((Activity) thisContext);
//                                    thisActivity.finish();
//                                    thisActivity.startActivity(thisActivity.getIntent());
//                                    Toast.makeText(thisContext, "Sticker Pack deleted", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    AlertDialog alertDialog = new AlertDialog.Builder(thisContext)
//                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    dialogInterface.dismiss();
//                                                }
//                                            }).create();
//                                    alertDialog.setTitle("Invalid Action");
//                                    alertDialog.setMessage("A sticker pack that is already applied to WhatsApp cannot have less than 3 stickers. " +
//                                            "In order to remove additional stickers, please add more to the pack first or remove the pack from the WhatsApp app.");
//                                    alertDialog.show();
//                                }
//                            }
//                        })
//                        .setView(image)
//                        .create();
//                alertDialog.setTitle("Do you want to delete this sticker?");
//                alertDialog.setMessage("Deleting this sticker will also remove it from your WhatsApp app.");
//                alertDialog.show();
//            }
//        });
    }

    public interface StickerClicked {
        void onCheckBoxChecked(int position, View v, boolean isChecked);

        void onAddSticker(int position, View v);
    }

    @Override
    public int getItemCount() {
        int numberOfPreviewImagesInPack, numberOfTOTALImagesInPack;
        numberOfPreviewImagesInPack = stickerPack.getStickers().size();
        numberOfTOTALImagesInPack = 30;
//        return numberOfPreviewImagesInPack;
        return numberOfTOTALImagesInPack;
    }
}
