package com.example.stickers.Activities.PhotoCollage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.Activities.PhotoCollage.layout.slant.NumberSlantLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.NumberStraightLayout;
import com.example.stickers.R;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.SquarePuzzleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {

    private List<PuzzleLayout> layoutData = new ArrayList<>();
    private List<Bitmap> bitmapData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private final int layout;
    private int selectedTheme = 0;
    private int lastSelectedTheme = -1;
    private Context context;

    public PuzzleAdapter(int layout) {
        this.layout = layout;
    }

    @Override
    public PuzzleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_puzzle, parent, false);
        if (layout == 1) {
            //collage
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_puzzle, parent, false);
        } else if (layout == 2) {
            //layouts list
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_puzzle_layout, parent, false);
        }
        return new PuzzleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PuzzleViewHolder holder, int position) {
        final PuzzleLayout puzzleLayout = layoutData.get(position);
        holder.puzzleView.setNeedDrawLine(true);
        holder.puzzleView.setNeedDrawOuterLine(false);
        holder.puzzleView.setTouchEnable(false);
//    holder.puzzleView.setLineColor(R.color.shimmerColor);
//    holder.puzzleView.setLineSize(4);
        holder.puzzleView.setPuzzleLayout(puzzleLayout);
        if (position == selectedTheme) {
            holder.puzzleView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),
                    R.color.greyColorDark, context.getTheme()));
        } else {
            holder.puzzleView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),
                    R.color.greyColor, context.getTheme()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("checkeddd", "adapter click");
                if (onItemClickListener != null) {
                    int theme = 0;
                    if (puzzleLayout instanceof NumberSlantLayout) {
                        theme = ((NumberSlantLayout) puzzleLayout).getTheme();
                    } else if (puzzleLayout instanceof NumberStraightLayout) {
                        theme = ((NumberStraightLayout) puzzleLayout).getTheme();
                    }
                    lastSelectedTheme = selectedTheme;
                    selectedTheme = position;
                    onItemClickListener.onItemClick(puzzleLayout, theme);
                    if (selectedTheme != -1)
                        notifyItemChanged(selectedTheme);
                    if (lastSelectedTheme != -1)
                        notifyItemChanged(lastSelectedTheme);
                }
            }
        });

        if (bitmapData == null) return;

        final int bitmapSize = bitmapData.size();

        if (puzzleLayout.getAreaCount() > bitmapSize) {
            for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
                if (bitmapSize != 0)
                    holder.puzzleView.addPiece(bitmapData.get(i % bitmapSize));
            }
        } else {
            holder.puzzleView.addPieces(bitmapData);
        }
    }

    @Override
    public int getItemCount() {
        return layoutData == null ? 0 : layoutData.size();
    }

    public void refreshData(List<PuzzleLayout> layoutData, List<Bitmap> bitmapData) {
        this.layoutData = layoutData;
        this.bitmapData = bitmapData;

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class PuzzleViewHolder extends RecyclerView.ViewHolder {

        SquarePuzzleView puzzleView;

        public PuzzleViewHolder(View itemView) {
            super(itemView);
            puzzleView = itemView.findViewById(R.id.puzzle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PuzzleLayout puzzleLayout, int themeId);
    }
}
