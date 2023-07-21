package com.example.stickers.Activities.sticker

import android.app.Activity
import com.example.stickers.Models.BgModel
import com.example.stickers.Models.StickerModel
import com.example.stickers.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LoadSticker {

    val emojisList = arrayListOf<StickerModel>()
    val baseList = arrayListOf<StickerModel>()
    val colorList = arrayListOf<BgModel>()
    val gradientList = arrayListOf<BgModel>()
    val effectList = arrayListOf<BgModel>()

    fun loadStickers(activity: Activity) {
        if (emojisList.isEmpty() || baseList.isEmpty()
        ) {
            loadAllLocalDrawable(activity) {}
        }
    }

    private fun loadAllLocalDrawable(activity: Activity, listener: () -> Unit) {
        emojisList.clear()
        baseList.clear()

        val task = CoroutineScope(Dispatchers.Default).launch {
            try {
                activity.executeToLoad()
            } catch (ignored: OutOfMemoryError) {
                listener.invoke()
            }
        }
        task.invokeOnCompletion {
            listener.invoke()
        }
    }

    private fun Activity.executeToLoad() {
        //Emojis
        val arrayEmojis = resources.obtainTypedArray(R.array.emojis)
        for (index in 0 until arrayEmojis.length()) {
            val resId = arrayEmojis.getResourceId(index, 0)
            emojisList.add(StickerModel(index, arrayEmojis.getDrawable(index), resId, true))
        }
        arrayEmojis.recycle()

        //Logos
        val array = resources.obtainTypedArray(R.array.base)
        for (index in 0 until array.length()) {
            val resId = array.getResourceId(index, 0)
            baseList.add(StickerModel(index, array.getDrawable(index), resId))
        }
        array.recycle()


        //Background
        val arrayBg = resources.obtainTypedArray(R.array.gradients)
        for (index in 0 until arrayBg.length()) {
            gradientList.add(BgModel(arrayBg.getResourceId(index, -1)))
        }
        arrayBg.recycle()

        //Effect Tab in Graphics
        val arrayEffects = resources.obtainTypedArray(R.array.effects)
        for (index in 0 until arrayEffects.length()) {
            effectList.add(BgModel(arrayEffects.getResourceId(index, -1)))
        }
        arrayEffects.recycle()

        val intArray: IntArray = resources.getIntArray(R.array.colors)
        for (element in intArray) {
            colorList.add(BgModel(element, true))
        }
    }
}