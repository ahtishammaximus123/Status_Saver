package com.example.stickers.Adapter;

import static com.example.stickers.app.ExtensionFuncKt.getUriPath;
import static com.example.stickers.app.ExtensionFuncKt.shareFile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stickers.Activities.FullScreenVideoActivity;
import com.example.stickers.DeletedCallback;
import com.example.stickers.Models.Status;
import com.example.stickers.R;

import java.util.List;

public class SavedVideosAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private final List<Status> imagesList;
    private DeletedCallback callback;
    private Context context;

    public SavedVideosAdapter(List<Status> imagesList, DeletedCallback callback) {
        this.callback = callback;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_files, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final Status status = imagesList.get(position);
        if (!status.isVideo())
            return;
        holder.save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete_ic));
        holder.share.setVisibility(View.VISIBLE);
        holder.save.setVisibility(View.VISIBLE);


        if (status.isVideo())
            Glide.with(context).asBitmap().load(status.getFile()).into(holder.imageView);


        holder.save.setOnClickListener(view -> {
            if (status.getFile().delete()) {
                imagesList.remove(position);
                notifyDataSetChanged();
                callback.onDeleted(true);
                Toast.makeText(context, "File Deleted", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "Unable to Delete File", Toast.LENGTH_SHORT).show();
        });

        holder.share.setOnClickListener(v -> {
            shareFile(context, getUriPath(context, status.getFile().getAbsolutePath()));
        });


        holder.imageView.setOnClickListener(v -> {
            Intent i = new Intent(context, FullScreenVideoActivity.class);
            i.setAction("delete");
            i.putExtra("status", status);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

}