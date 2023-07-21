package com.example.stickers.Activities.sticker.lib

import android.view.MotionEvent

class RotateIconEvent : StickerIconEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
        stickerView?.RotateCurrentSticker(event!!)
    }

    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        stickerView?.currentSticker?.let {
            stickerView.onStickerOperationListener
                ?.onStickerZoomFinished(it)
        }
    }
}