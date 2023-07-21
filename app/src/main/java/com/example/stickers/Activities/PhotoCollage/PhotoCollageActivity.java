package com.example.stickers.Activities.PhotoCollage;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stickers.Activities.CollageFilesActivity;
import com.example.stickers.Activities.PhotoCollage.layout.straight.StraightLayoutHelper;
import com.example.stickers.R;
import com.example.stickers.app.BillingBaseActivity;
import com.example.stickers.stickers.GridSpacingItemDecoration;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.poiphoto.GetAllPhotoTask;
import com.xiaopo.flying.poiphoto.PhotoManager;
import com.xiaopo.flying.poiphoto.datatype.Photo;
import com.xiaopo.flying.poiphoto.ui.adapter.PhotoAdapter;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoCollageActivity extends BillingBaseActivity{

    private static final String TAG = "MainActivity";

    private RecyclerView photoList;
    private RecyclerView puzzleList;
    //private RecyclerView puzzleListLayout;
    private TextView tvGallery, tvLayouts;

    private PuzzleAdapter puzzleAdapter;
    private PhotoAdapter photoAdapter;

    private List<Bitmap> bitmaps = new ArrayList<>();
    private ArrayMap<String, Bitmap> arrayBitmaps = new ArrayMap<>();
    private final ArrayList<String> selectedPath = new ArrayList<>();

    private ImageView crossGallery;
    private ImageView tick;
    private PuzzleHandler puzzleHandler;
    private final List<Target> targets = new ArrayList<>();
    private int deviceWidth;
    private int count = 0;
    private ImageView back, screen_mirroring;
    private TextView choosePhotos, chooseLayout;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());
        setContentView(R.layout.activity_photo_collage);

        SharedPreferences pref = getSharedPreferences("firstTime", 0);
        SharedPreferences.Editor editor = pref.edit();
        boolean firstRun = pref.getBoolean("firstRunLiveStatus", true);


        puzzleHandler = new PuzzleHandler(this);

        deviceWidth = getResources().getDisplayMetrics().widthPixels;

        initView();

        initLayoutView();

        prefetchResPhoto();

        if (firstRun) {
            editor.putBoolean("firstRunLiveStatus", false);
            editor.apply();
            final Dialog dialog = new Dialog(PhotoCollageActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_grant_permission);

            Button okButton = dialog.findViewById(R.id.grant_permission);
            ImageView cancelDialog = dialog.findViewById(R.id.close_permission_dialog);

            okButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PhotoCollageActivity.this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 110);
                    } else {
                        loadPhoto();
                    }
                }
            });
            cancelDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }else{
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhotoCollageActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                }, 110);
            } else {
                loadPhoto();
            }
        }
    }

    private void prefetchResPhoto() {


    }

    private void loadPhoto() {

        new GetAllPhotoTask() {
            @Override
            protected void onPostExecute(List<Photo> photos) {
                super.onPostExecute(photos);
                photoAdapter.refreshData(photos);
            }
        }.execute(new PhotoManager(this));
    }

    private void initView() {
        screen_mirroring = findViewById(R.id.photo_collage_screen_mirroring);

        back = findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //puzzleListLayout = (RecyclerView) findViewById(R.id.puzzle_list_layouts);
        photoList = findViewById(R.id.photo_list_gallery);
        puzzleList = findViewById(R.id.puzzle_list);

        tvGallery = findViewById(R.id.gallery_open);
        tvLayouts = findViewById(R.id.open_layout);
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Select more than one image for collage...", Toast.LENGTH_SHORT).show();

            }
        });
        tvLayouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Select more than one image for collage...", Toast.LENGTH_SHORT).show();

            }
        });
        TextView tvLayouts2 = findViewById(R.id.open_layout2);
        tvLayouts2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhotoCollageActivity.this, CollageFilesActivity.class));
            }
        });

        choosePhotos = findViewById(R.id.choose_photo);
        chooseLayout = findViewById(R.id.choose_layout);

        choosePhotos.setVisibility(View.VISIBLE);
        chooseLayout.setVisibility(View.GONE);

        tvGallery.setTextColor(getResources().getColor(R.color.light_green_text));
        tvLayouts.setTextColor(getResources().getColor(R.color.accentColor));

        tick = findViewById(R.id.tick);

        //puzzleListLayout.setVisibility(View.GONE);
        puzzleList.setVisibility(View.GONE);
        photoList.setVisibility(View.VISIBLE);

        photoAdapter = new PhotoAdapter();
        photoAdapter.setMaxCount(8);
        photoAdapter.setSelectedResId(R.drawable.photo_selected_shadow);

        photoList.setAdapter(photoAdapter);
        photoList.setLayoutManager(new GridLayoutManager(this, 4));

        //puzzleAdapter = new PuzzleAdapter(1);
//        puzzleList.setAdapter(puzzleAdapter);
//        puzzleList.setLayoutManager(
//                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        puzzleList.setHasFixedSize(true);

        puzzleAdapter = new PuzzleAdapter(1);
        puzzleList.setAdapter(puzzleAdapter);
        puzzleList.setLayoutManager(
                new GridLayoutManager(this, 2));
//        puzzleList.setHasFixedSize(true);
        int spanCount = 2; // 3 columns
        int spacing = 50; // 50px
        puzzleList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        puzzleAdapter.setOnItemClickListener(new PuzzleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {

                Intent intent = new Intent(PhotoCollageActivity.this, CollageProcessActivity.class);
                intent.putStringArrayListExtra("photo_path", selectedPath);
                if (puzzleLayout instanceof SlantPuzzleLayout) {
                    intent.putExtra("type", 0);
                } else {
                    intent.putExtra("type", 1);
                }
                intent.putExtra("piece_size", selectedPath.size());
                intent.putExtra("theme_id", themeId);

                startActivity(intent);
            }
        });

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1) {
                    Toast.makeText(getApplicationContext(), "Select more than one image for collage...", Toast.LENGTH_SHORT).show();
                    return;
                } else if (count >= 9) {
                    Toast.makeText(getApplicationContext(), "Limit reached, Select 8 images only...", Toast.LENGTH_SHORT).show();
                    return;
                } else if (count == 0) {
                    Toast.makeText(getApplicationContext(), "Select Images First...", Toast.LENGTH_SHORT).show();
                } else if (count > 0) {

                    choosePhotos.setVisibility(View.GONE);
                    tick.setVisibility(View.GONE);

                    chooseLayout.setVisibility(View.VISIBLE);

                    tvGallery.setTextColor(getResources().getColor(R.color.accentColor));
                    tvLayouts.setTextColor(getResources().getColor(R.color.light_green_text));

                    photoList.setVisibility(View.GONE);
                    puzzleList.setVisibility(View.VISIBLE);

//                    PuzzleAdapter puzzleAdapterLayout = new PuzzleAdapter(2);
//                    puzzleListLayout.setAdapter(puzzleAdapterLayout);

//                    Log.e("countcheck", count + "     " + selectedPath.size() + "     " + bitmaps.size());
//                    puzzleAdapterLayout.refreshData(PuzzleUtils.getPuzzleLayoutsCustomOnDemand(count), null);
//
//                    puzzleAdapterLayout.setOnItemClickListener(new PuzzleAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {
//                            Intent intent = new Intent(PhotoCollageActivity.this, CollageProcessActivity.class);
//                            if (puzzleLayout instanceof SlantPuzzleLayout) {
//                                intent.putExtra("type", 0);
//                            } else {
//                                intent.putExtra("type", 1);
//                            }
//                            intent.putExtra("piece_size", puzzleLayout.getAreaCount());
//                            intent.putExtra("theme_id", themeId);
//
//                            startActivity(intent);
//                        }
//                    });

                }
            }
        });

        screen_mirroring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent("android.settings.CAST_SETTINGS"));
                }
                catch (ActivityNotFoundException ex){
                    Toast.makeText(getApplicationContext(), "Casting is not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        puzzleAdapter.setOnItemClickListener(new PuzzleAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {
//                Log.e("checkeddd", "class click");
//                Intent intent = new Intent(PhotoCollageActivity.this, CollageProcessActivity.class);
//                intent.putStringArrayListExtra("photo_path", selectedPath);
//                if (puzzleLayout instanceof SlantPuzzleLayout) {
//                    intent.putExtra("type", 0);
//                } else {
//                    intent.putExtra("type", 1);
//                }
//                intent.putExtra("piece_size", selectedPath.size());
//                intent.putExtra("theme_id", themeId);
//
//                startActivity(intent);
//            }
//        });

        photoAdapter.setOnPhotoSelectedListener(new PhotoAdapter.OnPhotoSelectedListener() {
            @Override
            public void onPhotoSelected(final Photo photo, int position) {
                count++;
                if (count >= 8) {
                    Toast.makeText(getApplicationContext(), "Limit exceed. Select 8 images only...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Message message = Message.obtain();
                message.what = 120;
                message.obj = photo.getPath();
                puzzleHandler.sendMessage(message);


                changeBtnState();
                //prefetch the photo
                if(photo.getPath()!=null)
                {
                    try {
                        Picasso.with(PhotoCollageActivity.this)
                                .load("file:///" + photo.getPath())
                                .resize(deviceWidth, deviceWidth)
                                .centerInside()
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .fetch();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        photoAdapter.setOnPhotoUnSelectedListener(new PhotoAdapter.OnPhotoUnSelectedListener() {
            @Override
            public void onPhotoUnSelected(Photo photo, int position) {
                Bitmap bitmap = arrayBitmaps.remove(photo.getPath());
                bitmaps.remove(bitmap);
                selectedPath.remove(photo.getPath());
                count--;
                changeBtnState();
                puzzleAdapter.refreshData(StraightLayoutHelper.getAllThemeLayout(bitmaps.size()), bitmaps);
            }
        });

        photoAdapter.setOnSelectedMaxListener(new PhotoAdapter.OnSelectedMaxListener() {
            @Override
            public void onSelectedMax() {
                //Toast.makeText(PhotoCollageActivity.this, "Cant open more...", Toast.LENGTH_SHORT).show();
            }
        });

//        tvGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                photoList.setVisibility(View.VISIBLE);
//                puzzleListLayout.setVisibility(View.GONE);
//
//                tvGallery.setTextColor(getResources().getColor(R.color.light_green_text));
//                tvLayouts.setTextColor(getResources().getColor(R.color.accentColor));
//            }
//        });
//
//        tvLayouts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                photoList.setVisibility(View.GONE);
//                puzzleListLayout.setVisibility(View.VISIBLE);
//
//                tvGallery.setTextColor(getResources().getColor(R.color.accentColor));
//                tvLayouts.setTextColor(getResources().getColor(R.color.light_green_text));
//            }
//        });

    }


    private void changeBtnState() {
        if(count == 0) {
            tick.setImageResource(R.drawable.tick_bg_);
        } else {
            tick.setImageResource(R.drawable.tick_bg);
        }
    }
    private void initLayoutView() {
//        puzzleAdapter = new PuzzleAdapter(1);
//        Log.e("checkeddd", "adapter click");
//        puzzleList.setLayoutManager(new GridLayoutManager(this, 2));
//
//        int spanCount = 2; // 3 columns
//        int spacing = 50; // 50px
//        puzzleList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
//        puzzleList.setAdapter(puzzleAdapter);

//        puzzleListLayout.setLayoutManager(new GridLayoutManager(this, 3));
//
//        int spanCount = 3; // 3 columns
//        int spacing = 50; // 50px
//        puzzleListLayout.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));

    }

    private void showMoreDialog(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.BOTTOM);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.action_playground:
//                        Intent intent = new Intent(MainActivity.this, PlaygroundActivity.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.action_about:
//                        showAboutInfo();
//                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showAboutInfo() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        bottomSheetDialog.setContentView(R.layout.about_info);
//        bottomSheetDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            loadPhoto();
        }else if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        arrayBitmaps.clear();
        arrayBitmaps = null;

        bitmaps.clear();
        bitmaps = null;
    }

//    private void refreshLayout() {
//        puzzleList.post(new Runnable() {
//            @Override
//            public void run() {
//                puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
//            }
//        });
//    }

    public void fetchBitmap(final String path) {
        Log.d(TAG, "fetchBitmap: ");
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(TAG, "onBitmapLoaded: ");

                arrayBitmaps.put(path, bitmap);
                bitmaps.add(bitmap);
                selectedPath.add(path);

                puzzleHandler.sendEmptyMessage(119);
                targets.remove(this);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(getApplicationContext())
                .load("file:///" + path)
                .resize(300, 300)
                .centerInside()
                .config(Bitmap.Config.RGB_565)
                .into(target);

        targets.add(target);
    }

    private void refreshLayout() {
        puzzleList.post(new Runnable() {
            @Override
            public void run() {
                puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
            }
        });
    }

    private static class PuzzleHandler extends Handler {
        private final WeakReference<PhotoCollageActivity> mReference;

        PuzzleHandler(PhotoCollageActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 119) {
                mReference.get().refreshLayout();
            } else if (msg.what == 120) {
                mReference.get().fetchBitmap((String) msg.obj);
            }
        }
    }


    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
            for (String permissions : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        } else {
            for (String permissions : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }
    public  void adjustFontScale( Configuration configuration) {

        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }
    @Override
    protected void onResume() {
        super.onResume();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
//            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
//            return;
//        }
//
//        Common.APP_DIR = Environment.getExternalStorageDirectory().getPath() +
//                File.separator + "StatusDownloader";

    }
}
