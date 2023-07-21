package com.xinlan.imageeditlibrary.editimage.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.StickerImageCollage;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;


public class StickerTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String[] stickerPath = {"stickers/type1", "stickers/type2", "stickers/type3", "stickers/type4", "stickers/type5", "stickers/type6"};
    public static final String[] stickerPathName = {"Love", "Heart", "Romantic", "Couple", "Quote", "B'Day"};
    int[] typeIcon = {R.drawable.love, R.drawable.heart, R.drawable.romentic, R.drawable.couple, R.drawable.quotes, R.drawable.b_day};
    private StickerFragment mStickerFragment;
    private Context context;
    StickerImageCollage stickerImageCollage;

    public StickerTypeAdapter(Context context, StickerImageCollage stickerImageCollage) {
        this.context = context;
        this.stickerImageCollage = stickerImageCollage;
    }

    public StickerTypeAdapter(StickerFragment fragment) {
        super();
        this.mStickerFragment = fragment;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public ImageView text;

        public ImageHolder(View itemView, StickerImageCollage stickerImageCollage1) {

            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.text = itemView.findViewById(R.id.text);

            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String data = (String) view.getTag();
                    stickerImageCollage1.ImageTagData(data);
                }
            });
        }
    }// end inner class

    @Override
    public int getItemCount() {
        return stickerPathName.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_type_item, parent, false);
        ImageHolder holer = new ImageHolder(v, stickerImageCollage);
        return holer;
    }

    /**
     *
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageHolder imageHolder = (ImageHolder) holder;
        imageHolder.text.setImageResource(typeIcon[position]);
        imageHolder.text.setTag(stickerPath[position]);
    }

}// end class
