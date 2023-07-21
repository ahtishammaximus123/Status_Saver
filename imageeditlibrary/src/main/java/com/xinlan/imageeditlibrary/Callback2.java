package com.xinlan.imageeditlibrary;

import android.graphics.Bitmap;

public interface Callback2 {
    void onSuccess(Bitmap bitmap);

    void onFailed();
}