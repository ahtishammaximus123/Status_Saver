package com.example.stickers.Activities;

import static com.example.stickers.Utils.ExtKt.saveStatus;
import static com.example.stickers.ads.AdsExtKt.beGone;
import static com.example.stickers.ads.AdsExtKt.beVisible;
import static com.example.stickers.ads.AdsExtKt.showInterAd;
import static com.example.stickers.ads.AdsExtKt.showInterDemandAdmob;
import static com.example.stickers.ads.NativeAdmobKt.loadNativeAdmob;
import static com.example.stickers.app.ExtensionFuncKt.getUriPath;
import static com.example.stickers.app.ExtensionFuncKt.shareFile;
import static com.example.stickers.app.RemoteDateConfig.getRemoteAdSettings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment;
import com.example.stickers.Models.Status;
import com.example.stickers.R;
import com.example.stickers.Utils.AppCommons;
import com.example.stickers.Utils.Common;
import com.example.stickers.Utils.SaveHelperFull;
import com.example.stickers.ads.Ads;
import com.example.stickers.app.AppClass;
import com.example.stickers.app.BillingBaseActivity;
import com.example.stickers.app.RemoteAdDetails;
import com.example.stickers.app.RemoteDateConfig;
import com.example.stickers.databinding.ActivityFullScreenVideoBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.io.File;

public class FullScreenVideoActivity extends BillingBaseActivity {

    private ActivityFullScreenVideoBinding binding;
    private Boolean is30Plus = false;
    Status status;
    ExoPlayer player;
    boolean fullscreen = false;
    boolean lock = false;

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Ads().showBannerAd(getApplicationContext(), binding.lytBanner, binding.progressBar7, binding.adView);
        boolean hasVid = false;
        if (is30Plus) {
            hasVid = true;
        } else {
            if (status != null) hasVid = true;
        }
        if (is30Plus) {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(VideosFragment.ItemsViewModel.getFile().getUri());
            // Attach player to the view.

            if (ImagesFragment.Companion.getItemsViewModel() != null)
                mediaItem = MediaItem.fromUri(ImagesFragment.Companion.getItemsViewModel().getFile().getUri());
        } else {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
            mediaItem = MediaItem.fromUri(Uri.fromFile(status.getFile()));
        }

        if (hasVid && player == null) {
            player = new ExoPlayer.Builder(this).build();
            binding.videoFull.setPlayer(player);
            if (mediaItem != null)
                player.setMediaItem(mediaItem);
            binding.controls.setPlayer(player);
            player.prepare();
            player.play();
        }

    }

    //    int position = 0;
    MediaItem mediaItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityFullScreenVideoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //setAdLyt1(binding.lytBanner);
        //setAdContent1(binding.adView);
        //setAdProgressBar1(binding.progressBar7);

        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("FullVideoActivity_Open");
        loadNativeAdmob(
                this,
                binding.nativeLayout.getRoot(), binding.nativeLayout.adFrameLarge,
                R.layout.admob_native_small,
                R.layout.max_native_small, 2,
                getRemoteAdSettings().getNative_inner(), null, null,
                getRemoteAdSettings().getAdmob_native_id_2().getValue()
        );

        binding.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri;
                    if (is30Plus) {
                        uri = ImagesFragment.Companion.getItemsViewModel().getFile().getUri();
                    } else {
                        uri = Uri.parse("file://" + status.getFile().getAbsolutePath());
                    }
                    AppCommons.Companion.ShowWAppDialog(FullScreenVideoActivity.this, binding.imgPost, uri, true);
                } catch (Exception ignored) {
                }
            }
        });

        binding.imageView22.setOnClickListener(view1 -> {
            binding.imgMirror.performClick();
        });

        binding.imgMirror.setOnClickListener(view1 -> {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "Casting is not supported!", Toast.LENGTH_SHORT).show();
            }
        });

        Intent getIntent = getIntent();

        if (getIntent() != null)
            is30Plus = getIntent().getBooleanExtra("is30Plus", false);
        if (is30Plus) {

        } else {
            status = (Status) getIntent.getSerializableExtra("status");
        }


        String tag = getIntent().getExtras().getString("img_tag");
        Log.e("img_tag", tag + "     jkjkkj");
        try {
            if (tag.equals("saved")) {
                Log.e("img_tag", tag + "");
                binding.imgDownload.setVisibility(View.VISIBLE);
                binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_);
            } else {
                binding.imgDownload.setImageResource(R.drawable.ic_download_ic);
                binding.imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (is30Plus) {


                            saveStatus(getApplicationContext(), binding.container,
                                    ImagesFragment.Companion.getItemsViewModel());

                            binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_);
                        } else {
                            Common.copyFile(status, getApplicationContext(), binding.container);
                            binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_);
                        }

//                        showInterDemandAdmob(FullScreenVideoActivity.this,
//                                getRemoteAdSettings().getAdmob_inter_download_btn_ad(),
//                                getRemoteAdSettings().getAdmob_inter_download_btn_id().getValue(),
//                                // Code to execute when the listener is invoked
//
//                        );


                    }
                });
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is30Plus) {
                    /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "You can save all WhatsApp Status for free and fast. \n Download it here: " + link + "");
                    shareIntent.setType("video/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, ImagesFragment.Companion.getItemsViewModel().getFile().getUri());
                    startActivity(shareIntent);*/
                    shareFile(FullScreenVideoActivity.this,
                            ImagesFragment.Companion.getItemsViewModel().getFile().getUri());
                } else {
                    /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "You can save all WhatsApp Status for free and fast. \n Download it here: " + link + "");
                    shareIntent.setType("video/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
                    startActivity(shareIntent);*/

                    shareFile(FullScreenVideoActivity.this, getUriPath(FullScreenVideoActivity.this, status.getFile().getAbsolutePath()));
                }

            }
        });

        if (getIntent != null && getIntent.getAction() != null) {
            if (getIntent.getAction().equals("download")) {
                binding.imgDownload.setImageResource(R.drawable.ic_download_ic);
                binding.imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("hshs", "onClick: hereeee");
                        if (is30Plus) {
                            String root = Environment.getExternalStorageDirectory().toString();
                            File justDirOut = new File(Common.APP_DIR);
                            if (!justDirOut.exists()) {
                                justDirOut.mkdir();
                            }
                            Log.d("hshs", "onClick: hereeee11");
                            File outDirCopy = new File(justDirOut, ImagesFragment.Companion.getItemsViewModel().getFile().getName());
                            Log.d("hshs", "onClick: hereeee22");
                            SaveHelperFull saveHelper = new SaveHelperFull();
                            Log.d("hshs", "onClick: hereeee33");
                            saveHelper.saveintopathFull(ImagesFragment.Companion.getItemsViewModel().getFile().getUri(), outDirCopy, FullScreenVideoActivity.this);
                            Log.d("hshs", "onClick: hereeee44");
                            binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_);
                        } else {
                            Common.copyFile(status, getApplicationContext(), binding.container);
                            binding.imgDownload.setImageResource(R.drawable.ic_download_ic__1_);
                        }

                    }
                });

            } else if (getIntent.getAction().equals("delete")) {
                binding.imgDownload.setImageResource(R.drawable.ic_delete_ic);
                binding.imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status.getFile().delete()) {
                            Toast.makeText(FullScreenVideoActivity.this, "File Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(FullScreenVideoActivity.this, "Unable to Delete File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        player = new ExoPlayer.Builder(this).build();
        binding.videoFull.setPlayer(player);

        if (is30Plus) {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(VideosFragment.ItemsViewModel.getFile().getUri());
            // Attach player to the view.

            if (ImagesFragment.Companion.getItemsViewModel() != null)
                mediaItem = MediaItem.fromUri(ImagesFragment.Companion.getItemsViewModel().getFile().getUri());
        } else {
            //binding.videoFull.setMediaController(mediaController);
            //binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
            mediaItem = MediaItem.fromUri(Uri.fromFile(status.getFile()));
        }

        if (mediaItem != null)
            player.setMediaItem(mediaItem);
        binding.controls.setPlayer(player);
        player.prepare();
        player.play();
        binding.videoFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullscreen) {
                    if (binding.controls.isVisible())
                        binding.controls.hide();
                    else {

                        binding.controls.show();

                    }
                }
            }
        });

        ImageView fullscreenButton = binding.controls.findViewById(R.id.exo_fullscreen_icon);
        ImageView lockButton = binding.controls.findViewById(R.id.exo_lock_icon);
        if (fullscreenButton != null) {
            fullscreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!lock) {
                        if (fullscreen) {
                            beVisible(binding.nativeLayout.getRoot());
                            binding.imageView22.setVisibility(View.GONE);
                            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_full));
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().show();
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.constraintLayout3.getLayoutParams();
                            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                            params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                            params.topMargin = (int) (13 * getApplicationContext().getResources().getDisplayMetrics().density);
                            params.bottomMargin = 0;
                            params.leftMargin = (int) (13 * getApplicationContext().getResources().getDisplayMetrics().density);
                            params.rightMargin = (int) (13 * getApplicationContext().getResources().getDisplayMetrics().density);
                            binding.constraintLayout3.setLayoutParams(params);
                            fullscreen = false;
                            binding.videoFull.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                            //player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
                            binding.controls.show();
                        } else {
                            beGone(binding.nativeLayout.getRoot());
                            binding.imageView22.setVisibility(View.VISIBLE);
                            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(FullScreenVideoActivity.this, R.drawable.ic_potrait));
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().hide();
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.constraintLayout3.getLayoutParams();
                            params.topMargin = 0;
                            params.bottomMargin = 0;
                            params.leftMargin = 0;
                            params.rightMargin = 0;
                            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                            binding.constraintLayout3.setLayoutParams(params);
                            binding.videoFull.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                            // player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(binding.constraintLayout3);
                            constraintSet.connect(R.id.controls, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                            //constraintSet.connect(R.id.video_full,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0);
                            constraintSet.applyTo(binding.constraintLayout3);
                            binding.controls.hide();
                            fullscreen = true;
                        }
                    }
                }
            });
        }
        if (lockButton != null) {
            lockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lock = !lock;
                    if (lock) {
                        lockButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_bg_end_color, getTheme())));
                        int currentOrientation = getResources().getConfiguration().orientation;
                        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        }
                    } else {
                        lockButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, getTheme())));

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    }
                }
            });
        }
//        ArrayList<String> list = new ArrayList<>();
//        if( AppClass.Companion.getFileList() != null && !AppClass.Companion.getFileList().isEmpty()){
//            for(Status a: AppClass.Companion.getFileList()){
//                list.add(Uri.fromFile(a.getFile()).toString());
//            }
//        }
//        else if( AppClass.Companion.getFile30List() != null && !AppClass.Companion.getFile30List().isEmpty()){
//            for(StatusDocFile a: AppClass.Companion.getFile30List()){
//
////                String[] listt = a.getFile().getUri().getPath().split("/document/primary:");
////
////                String toUse = listt[1];
////                Log.e("tree", "read30SDKWithUri: toUse : " + toUse);
////
////                toUse = "/storage/emulated/0/" + toUse;
////                File f = new File(toUse);
//
//                list.add(a.getFile().getUri().toString());
//            }
//        }

//        binding.imageView23.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(list.size() > position+1) {
//                            position = position + 1;
//
//                            binding.videoFull.setMediaController(mediaController);
//                            binding.videoFull.setVideoURI(Uri.parse(list.get(position)));
//                        }
//                    }
//                }
//        );
//        binding.imageView24.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(position-1 >= 0){
//                            position = position - 1;
//
//                            binding.videoFull.setMediaController(mediaController);
//                            binding.videoFull.setVideoURI(Uri.parse(list.get(position)));
//                        }
//                    }
//                }
//        );
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video1_jetay3.mp4");
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video2_sn3sek.mp4");
//        list.add("https://res.cloudinary.com/kartiksaraf/video/upload/v1564516308/github_MediaSliderView/demo_videos/video3_jcrsb3.mp4");
//
        //  loadMediaSliderView(list,"video",false,true,false,"Video-Slider","#000000",null,0);

//        final MediaController mediaController = new MediaController(FullScreenVideoActivity.this, true);
//
//        binding.videoFull.setOnPreparedListener(mp -> {
//            mp.start();
//            mediaController.show(0);
//            //mp.setLooping(true);
//        });
//
//        binding.videoFull.setMediaController(mediaController);
//        mediaController.setMediaPlayer(binding.videoFull);
//        binding.videoFull.setVideoURI(Uri.fromFile(status.getFile()));
//        binding.videoFull.requestFocus();
//
//        ((ViewGroup) mediaController.getParent()).removeView(mediaController);
//
//        if (FullScreenVideoActivity.this.binding.videoViewWrapper.getParent() != null) {
//            FullScreenVideoActivity.this.binding.videoViewWrapper.removeView(mediaController);
//        }
//
//        FullScreenVideoActivity.this.binding.videoViewWrapper.addView(mediaController);
    }

    //    public static Uri getImageContentUri(Context context, File imageFile) {
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Video.Media._ID },
//                MediaStore.Video.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int a = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
//            if(a >= 0) {
//                int id = cursor.getInt(a);
//                cursor.close();
//                return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
//            } else return null;
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Video.Media.DATA, filePath);
//                return context.getContentResolver().insert(
//                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
//    }
    public void adjustFontScale(Configuration configuration) {

        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

    }

    @Override
    public void onBackPressed() {
        AppClass.Companion.setFile30List(null);
        AppClass.Companion.setFileList(null);

        showInterAd(this, RemoteDateConfig.getRemoteAdSettings()
                .getInter_view_video(), () -> {
            finish();
            return null;
        });
    }
}