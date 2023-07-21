package com.example.stickers.Activities;

import static com.example.stickers.ads.AdsFunctionsKt.afterDelay;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.stickers.R;
import com.example.stickers.app.BillingBaseActivity;

import ai.lib.billing.IapConnector;

public class PremiumActivity extends BillingBaseActivity {

    private ImageView close, buyNow;
    private boolean isExit = true;

    private IapConnector iapConnector;
    private boolean isBillingClientConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_premium);


        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("PremiumActivity_Open");
        close = findViewById(R.id.closePremium);
        buyNow = findViewById(R.id.imageView14);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        close.setVisibility(View.VISIBLE);

        buyNow.setOnClickListener(v -> {
            // subscribe(Constants.subsList.get(0));
            buyNow.setEnabled(false);
            afterDelay(1500, () -> {
                buyNow.setEnabled(true);
                return null;
            });
            if (SplashActivity.getFbAnalytics() != null)
                SplashActivity.getFbAnalytics().sendEvent("PremiumActivity_buy");

        });

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
        if (isExit)
            finish();
    }
}