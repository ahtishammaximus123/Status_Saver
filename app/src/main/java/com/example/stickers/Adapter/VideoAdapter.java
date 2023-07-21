package com.example.stickers.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.stickers.Activities.FullScreenVideoActivity;
import com.example.stickers.ImageAdapterCallBack;
import com.example.stickers.Models.Status;
import com.example.stickers.R;
import com.example.stickers.Utils.Common;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    public final List<Status> videoList;
    private Context context;
    private final ConstraintLayout container;
    private final ImageAdapterCallBack callBack;

    public VideoAdapter(List<Status> videoList, ConstraintLayout container, ImageAdapterCallBack callBack) {
        this.videoList = videoList;
        this.container = container;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_video, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final Status status = videoList.get(position);

        if(status != null && status.isSavedStatus()){
            holder.save.setImageResource(R.drawable.ic_download_ic__1_);
            holder.save.setTag("saved");
        }else{
            holder.save.setImageResource(R.drawable.ic_download_ic);
            holder.save.setTag("notSaved");
        }
        if(holder.save.getTag().equals("saved")){
            holder.save.setClickable(false);
        }holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.save.setClickable(true);
                if (holder.save.getTag() != "saved")
                callBack.onDownloadClick(status, container);

                videoList.get(position).setSavedStatus(true);
                notifyItemChanged(position);
            }
        });

        Glide.with(context).asBitmap().load(status.getFile())
                .into(holder.imageView);

        holder.share.setOnClickListener(v -> {
            callBack.onShareClicked(status);
        });

        holder.imageView.setOnClickListener(v -> {
            callBack.onImageViewClicked(status, holder.save.getTag());
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
}
