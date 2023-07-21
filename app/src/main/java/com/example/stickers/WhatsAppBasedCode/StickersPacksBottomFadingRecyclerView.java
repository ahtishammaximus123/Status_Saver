/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.stickers.WhatsAppBasedCode;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class StickersPacksBottomFadingRecyclerView extends RecyclerView {
    public StickersPacksBottomFadingRecyclerView(Context context) {
        super(context);
    }

    public StickersPacksBottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickersPacksBottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 0.0f;
    }
}
