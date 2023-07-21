package com.example.stickers;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.stickers.Models.Status;

import java.io.File;

public interface ImageAdapterCallBack {
    void onShareClicked(Status status);
    void onImageViewClicked(Status status, Object tag);
    void onDownloadClick(Status status, ConstraintLayout container);
}
