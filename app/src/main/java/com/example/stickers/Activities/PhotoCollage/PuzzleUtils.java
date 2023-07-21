package com.example.stickers.Activities.PhotoCollage;

import com.example.stickers.Activities.PhotoCollage.layout.slant.OneSlantLayout;
import com.example.stickers.Activities.PhotoCollage.layout.slant.SlantLayoutHelper;
import com.example.stickers.Activities.PhotoCollage.layout.slant.ThreeSlantLayout;
import com.example.stickers.Activities.PhotoCollage.layout.slant.TwoSlantLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.EightStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.FiveStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.FourStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.NineStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.OneStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.SevenStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.SixStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.StraightLayoutHelper;
import com.example.stickers.Activities.PhotoCollage.layout.straight.ThreeStraightLayout;
import com.example.stickers.Activities.PhotoCollage.layout.straight.TwoStraightLayout;
import com.xiaopo.flying.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class PuzzleUtils {
    private static final String TAG = "PuzzleUtils";

    private PuzzleUtils() {
        //no instance
    }

    public static PuzzleLayout getPuzzleLayout(int type, int borderSize, int themeId) {
        if (type == 0) {
            switch (borderSize) {
                case 1:
                    return new OneSlantLayout(themeId);
                case 2:
                    return new TwoSlantLayout(themeId);
                case 3:
                    return new ThreeSlantLayout(themeId);
                default:
                    return new OneSlantLayout(themeId);
            }
        } else {
            switch (borderSize) {
                case 1:
                    return new OneStraightLayout(themeId);
                case 2:
                    return new TwoStraightLayout(themeId);
                case 3:
                    return new ThreeStraightLayout(themeId);
                case 4:
                    return new FourStraightLayout(themeId);
                case 5:
                    return new FiveStraightLayout(themeId);
                case 6:
                    return new SixStraightLayout(themeId);
                case 7:
                    return new SevenStraightLayout(themeId);
                case 8:
                    return new EightStraightLayout(themeId);
                case 9:
                    return new NineStraightLayout(themeId);
                default:
                    return new OneStraightLayout(themeId);
            }
        }
    }

  public static List<PuzzleLayout> getAllPuzzleLayouts() {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    //slant layout
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(3));

    // straight layout
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(3));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(4));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(5));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(6));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(7));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(8));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(9));
    return puzzleLayouts;
  }

    public static List<PuzzleLayout> getPuzzleLayoutsCustomOnDemand(int numberOfImagesSelected) {
        List<PuzzleLayout> puzzleLayouts = new ArrayList<>();

        if (numberOfImagesSelected == 1 || numberOfImagesSelected == 2) {
            //because 2 or 3 images also support straight layouts and slant layouts
            //impppppp
            //this needs to be checked if one image selected duplicate the image and show layout for 2 images
            //slant layout
            puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(numberOfImagesSelected));
            // straight layout
            puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(numberOfImagesSelected));
        } else if (numberOfImagesSelected == 3) {
            //slant layout
            puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(numberOfImagesSelected));
            // straight layout
            puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(numberOfImagesSelected));
        } else {
            //because more than 3 only supports straight layouts
            // straight layout
            puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(numberOfImagesSelected));
        }
        return puzzleLayouts;
    }

    public static List<PuzzleLayout> getPuzzleLayouts(int pieceCount) {
        List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
        puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(pieceCount));
        puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(pieceCount));
        return puzzleLayouts;
    }
}
