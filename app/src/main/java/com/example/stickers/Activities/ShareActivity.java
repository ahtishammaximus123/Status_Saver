package com.example.stickers.Activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.example.stickers.app.BillingBaseActivity;
import com.example.stickers.databinding.ActivityShareBinding;

public class ShareActivity extends BillingBaseActivity {

    private ActivityShareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityShareBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("SharedActivity: Open");

        String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
        binding.shareback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.imgGmailShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareURL("com.google.android.gm");
            }
        });
        binding.imgSkypeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareURL("com.skype.raider");
            }
        });
        binding.imgShareShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_TEXT, "You can save all WhatsApp Status for free and fast. \n Download it here: " + link + "");

                startActivity(Intent.createChooser(intent, "Share APP"));
            }
        });
    }

    void shareURL(String packageName) {
        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("Share_" + packageName);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage(packageName);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
        intent.putExtra(Intent.EXTRA_TEXT, "You can save all WhatsApp Status for free and fast. \n Download it here: " + link + "");
        startActivity(Intent.createChooser(intent, "Share APP"));
    }

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
        finish();
//        InterstitialAdAppLovin.Companion.showCallback(this,  new AdCallback() {
//
//            @Override
//            public void onAdFailed() {
//                finish();
//            }
//
//            @Override
//            public void onAdClosed() {
//                finish();
//            }
//        },RemoteDateConfig.getRemoteAdSettings().getInter_share_back().getValue());

    }
}