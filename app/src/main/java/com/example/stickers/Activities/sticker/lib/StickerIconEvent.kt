package com.example.stickers.Activities.sticker.lib

import android.view.MotionEvent

interface StickerIconEvent {
    fun onActionDown(stickerView: StickerView?, event: MotionEvent?)
    fun onActionMove(stickerView: StickerView?, event: MotionEvent?)
    fun onActionUp(stickerView: StickerView?, event: MotionEvent?)
}
