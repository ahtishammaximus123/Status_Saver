package com.example.stickers.Activities.sticker.lib

import android.view.MotionEvent
import com.example.stickers.Activities.sticker.lib.StickerIconEvent

class DeleteIconEvent : StickerIconEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        stickerView?.removeCurrentSticker()
    }
}
