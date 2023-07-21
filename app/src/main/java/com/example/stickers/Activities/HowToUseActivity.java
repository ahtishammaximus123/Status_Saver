package com.example.stickers.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.stickers.R;
import com.example.stickers.app.BillingBaseActivity;

public class HowToUseActivity extends BillingBaseActivity {

    private ImageView imgBackArrow;
    private ConstraintLayout connect;
    private TextView txtconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_how_to_use);

        if (SplashActivity.getFbAnalytics() != null)
            SplashActivity.getFbAnalytics().sendEvent("how2UseActivity_Open");
        imgBackArrow = findViewById(R.id.back_arrow);
        connect = findViewById(R.id.connect_screen_mirroring);
        txtconnect = findViewById(R.id.txtconnect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent("android.settings.CAST_SETTINGS"));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Casting is not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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

    private void checkDisplay() {
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager.getDisplays().length >= 2) {
            txtconnect.setText(getString(R.string.connected));
        } else {
            txtconnect.setText(getString(R.string.connect));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDisplay();
    }
}