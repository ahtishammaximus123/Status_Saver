package com.xinlan.imageeditlibrary.editimage.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.SingleStickerImageCollage;
import com.xinlan.imageeditlibrary.StickerImageCollage;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;


public class StickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public DisplayImageOptions imageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(R.drawable.yd_image_tx)
            .build();//

    private StickerFragment mStickerFragment;
    private final List<String> pathList = new ArrayList<String>();
    private Context context;//
    SingleStickerImageCollage stickerImageCollage;

    public StickerAdapter(StickerFragment fragment) {
        super();
        this.mStickerFragment = fragment;
    }

    public StickerAdapter(Activity context, SingleStickerImageCollage stickerImageCollage) {
        this.context = context;
        this.stickerImageCollage = stickerImageCollage;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ImageHolder(View itemView, SingleStickerImageCollage singleStickerImageCollage) {
            super(itemView);
            this.image = itemView.findViewById(R.id.img);
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String data = (String) view.getTag();
                    singleStickerImageCollage.SingleImageTagData(data);
                }
            });
        }
    }// end inner class

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_item, parent, false);
        ImageHolder holer = new ImageHolder(v, stickerImageCollage);
        return holer;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageHolder imageHolder = (ImageHolder) holder;
        String path = pathList.get(position);

        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        imageHolder.image.setImageBitmap(bitmap);
        imageHolder.image.setTag(path);

    }

    public void addStickerImages(String folderPath) {
        pathList.clear();
        try {
            String[] files = context.getAssets()
                    .list(folderPath);
            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }

}// end class
