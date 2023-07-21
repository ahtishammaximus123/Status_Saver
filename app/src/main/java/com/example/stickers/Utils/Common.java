package com.example.stickers.Utils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.stickers.Utils.ExtKt.showSnackBar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.example.stickers.Activities.FullScreenImageActivity;
import com.example.stickers.Activities.FullScreenVideoActivity;
import com.example.stickers.Models.Status;
import com.example.stickers.Models.StatusDocFile;
import com.example.stickers.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

public class Common {

    static final int MINI_KIND = 1;
    static final int MICRO_KIND = 3;

    public static final int GRID_COUNT = 2;

    private static final String CHANNEL_NAME = "USMAN";

    public static File STATUS_DIRECTORY = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp/Media/.Statuses");
    public static final File STATUS_DIRECTORY_1 = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp/Media/.Statuses");
    public static final File STATUS_DIRECTORY_2 = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp Business/Media/.Statuses");

    public static final File STATUS_DIRECTORY_NEW = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp/Media/.Statuses");
    public static final File STATUS_DIRECTORY_NEW_2 = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp Business/Media/.Statuses");

    public static String APP_DIR;

    public static void copyFile(Status status, Context context, ConstraintLayout container) {

        if (getSavedFile(status.getTitle())) {
            showSnackBar(context, container, "Already downloaded.");
            return;
        }

        File file = new File(Common.APP_DIR);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                showSnackBar(context, container, "Something went wrong");
            }
        }

        String fileName;

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String currentDateTime = sdf.format(new Date());

        if (status.isVideo()) {
            fileName = status.getTitle();
        } else {
            fileName = status.getTitle();
        }

        File destFile = new File(file + File.separator + fileName);

        try {
            org.apache.commons.io.FileUtils.copyFile(status.getFile(), destFile);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
//            } else
//                copyFile2(status.getFile().getAbsolutePath(), status.getFile().getAbsolutePath(), destFile.getAbsolutePath());
            destFile.setLastModified(System.currentTimeMillis());
            new SingleMediaScanner(context, file);
            showNotification(context, container, destFile, status);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void copyFile2(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void copyFileDoc(StatusDocFile status, Context context, ConstraintLayout container) {

        if (getSavedFile(status.getTitle())) {

            showSnackBar(context, container, "Already downloaded.");
            return;
        }

        File file = new File(Common.APP_DIR);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                showSnackBar(context, container, "Something went wrong");
            }
        }

        String fileName;

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String currentDateTime = sdf.format(new Date());

        if (status.isVideo()) {
            fileName = status.getTitle();
        } else {
            fileName = status.getTitle();
        }

        File destFile = new File(file + File.separator + fileName);

        try {
//            FileUtils.copyFile(status.getPath()-, destFile);
            destFile.setLastModified(System.currentTimeMillis());
            new SingleMediaScanner(context, file);
            showNotification(context, container, destFile, status);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void showNotification(Context context, ConstraintLayout container, File destFile, Status status) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(context);
        }

//        Uri data = FileProvider.getUriForFile(context, "com.example.stickers.WhatsAppLicensedCode.StickerContentProvider" + ".provider", new File(destFile.getAbsolutePath()));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Uri data = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID , new File(destFile.getAbsolutePath()));
        //Uri data = FileProvider.getUriForFile(context, "com.example.stickers.provider", new File(destFile.getAbsolutePath()));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//
//        if (status.isVideo()) {
//            intent.setDataAndType(data, "video/*");
//        } else {
//            intent.setDataAndType(data, "image/*");
//        }
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent;
        if (status.isVideo()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenVideoActivity.class).putExtra("status", status).putExtra("img_tag", "saved").setAction("delete"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            } else {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenVideoActivity.class).putExtra("status", status).putExtra("img_tag", "saved").setAction("delete"), PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenImageActivity.class).putExtra("status", status).putExtra("img_tag", "saved"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            } else {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenImageActivity.class).putExtra("status", status).putExtra("img_tag", "saved"), PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }

//        Intent i = new Intent(getContext(), FullScreenImageActivity.class);
//        i.setAction("asa");
//        i.putExtra("status", status);
//        i.putExtra("img_tag", "saved");
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_NAME);

        notification.setSmallIcon(R.drawable.ic_download_ic)
                .setContentTitle(destFile.getName())
                .setContentText("File Saved to" + APP_DIR)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(new Random().nextInt(), notification.build());

        showSnackBar(context, container, "Saved successfully.");

    }

    private static void showNotification(Context context, ConstraintLayout container, File destFile, StatusDocFile status) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(context);
        }
        PendingIntent pendingIntent;
        if (status.isVideo()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenVideoActivity.class).putExtra("status", status).putExtra("img_tag", "saved").setAction("delete"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            } else {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenVideoActivity.class).putExtra("status", status).putExtra("img_tag", "saved").setAction("delete"), PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenImageActivity.class).putExtra("status", status).putExtra("img_tag", "saved"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

            } else {
                pendingIntent = PendingIntent.getActivity(context,
                        0, new Intent(context, FullScreenImageActivity.class).putExtra("status", status).putExtra("img_tag", "saved"), PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
//
////        Uri data = FileProvider.getUriForFile(context, "com.example.stickers.WhatsAppLicensedCode.StickerContentProvider" + ".provider", new File(destFile.getAbsolutePath()));
////        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", new File(destFile.getAbsolutePath()));
//        //Uri data = FileProvider.getUriForFile(context, "com.example.videodownloader", new File(destFile.getAbsolutePath()));
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//
//        if (status.isVideo()) {
//            intent.setDataAndType(data, "video/*");
//        } else {
//            intent.setDataAndType(data, "image/*");
//        }
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_NAME);

        notification.setSmallIcon(R.drawable.ic_download_ic)
                .setContentTitle(destFile.getName())
                .setContentText("File Saved to" + APP_DIR)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(new Random().nextInt(), notification.build());

        showSnackBar(context, container, "Saved successfully.");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void makeNotificationChannel(Context context) {

        NotificationChannel channel = new NotificationChannel(Common.CHANNEL_NAME, "Saved", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    public static boolean getSavedFile(String name) {

        final File app_dir = new File(Common.APP_DIR);

//        Log.e("checkinggsaved", "Saved Time    " + app_dir.getAbsolutePath() + "");
        if (app_dir.exists()) {
            File[] savedFiles;
            savedFiles = app_dir.listFiles();

            if (savedFiles != null && savedFiles.length > 0) {
                Arrays.sort(savedFiles);
                for (File file : savedFiles) {
                    Status status = new Status(file, file.getName(), file.getAbsolutePath());
                    if (name.equals(status.getTitle())) {
                        return true;
                    }
                }

            }

        }

        return false;
    }


}
