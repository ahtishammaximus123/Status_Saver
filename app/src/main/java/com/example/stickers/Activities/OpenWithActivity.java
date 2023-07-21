package com.example.stickers.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stickers.R;

public class OpenWithActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_with);

        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}