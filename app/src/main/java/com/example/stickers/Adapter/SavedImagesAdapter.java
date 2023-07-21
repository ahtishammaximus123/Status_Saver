package com.example.stickers.Adapter;

import static com.example.stickers.app.ExtensionFuncKt.getUriPath;
import static com.example.stickers.app.ExtensionFuncKt.shareFile;

import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.Activities.FullScreenImageActivity;
import com.example.stickers.BuildConfig;
import com.example.stickers.DeletedCallback;
import com.example.stickers.Models.Status;
import com.example.stickers.R;
import com.example.stickers.app.AppClass;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class SavedImagesAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private final List<Status> imagesList;
    private Context context;
    private DeletedCallback callback;

    public SavedImagesAdapter(List<Status> imagesList, DeletedCallback callback) {
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
        if (status.isVideo()){return;}
        holder.save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete_ic));
        holder.share.setVisibility(View.VISIBLE);
        holder.save.setVisibility(View.VISIBLE);

        if (status.isVideo()){}
            //Glide.with(context).asBitmap().load(status.getFile()).into(holder.imageView);
//            holder.imageView.setImageBitmap(status.getThumbnail());
        else
            try {
                Picasso.with(context).load(status.getFile()).into(holder.imageView);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        holder.save.setOnClickListener(view -> {



            if (status.getFile().delete()) {
                imagesList.remove(position);
                notifyDataSetChanged();
                callback.onDeleted(true);
                Toast.makeText(context, "File Deleted", Toast.LENGTH_SHORT).show();
            } else
            {
               try{

                   Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID , new File(status.getFile().getAbsolutePath()));
                   // android 28 and below
                   FirebaseCrashlytics.getInstance().log("Saved Images Adapter data= "+data.toString());
                    context.getContentResolver().delete(data, null, null);
                }catch ( SecurityException e) {
                   FirebaseCrashlytics.getInstance().log("Saved Images Adapter SecurityException= "+e.getMessage().toString());
                }
                imagesList.remove(position);
                notifyDataSetChanged();
                callback.onDeleted(true);
            }
            try {
                FirebaseCrashlytics.getInstance().log("Saved Images Adapter");
                FirebaseCrashlytics.getInstance().recordException(new RuntimeException("holder.save"));
            } catch (RuntimeException ex){}
        });

        holder.share.setOnClickListener(v -> {

            shareFile(context, getUriPath(context, status.getFile().getAbsolutePath()));

        });

        holder.imageView.setOnClickListener(v -> {

            if (status.isVideo()) {}
            else {

                Intent i = new Intent(context, FullScreenImageActivity.class);
                i.setAction("delete");
                i.putExtra("status", status);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

}