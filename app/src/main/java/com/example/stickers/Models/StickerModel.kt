package com.example.stickers.Models

import android.graphics.drawable.Drawable

data class StickerModel(
    val id: Int,
    val path: Drawable?,
    val resId: Int,
    val isEmoji: Boolean = false
)
