package com.example.stickers.Utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import co.mobiwise.materialintro.animation.MaterialIntroListener
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView


fun Activity.showIntro(
    targetView: View?,
    infoText: String?,
    usageId: String?,
    enableDotAnim: Boolean,
    listener: MaterialIntroListener
) {
    MaterialIntroView.Builder(this as AppCompatActivity)
        .enableDotAnimation(enableDotAnim)
        .enableIcon(false)
        .setFocusGravity(FocusGravity.CENTER)
        .setFocusType(Focus.MINIMUM)
        .setDelayMillis(300)
        .enableFadeAnimation(true)
        .performClick(true)
        .setInfoText(infoText)
        .setShape(ShapeType.RECTANGLE)
        .setTarget(targetView)
        .performClick(true)
        .setListener(listener)
        .setUsageId(usageId)
        .show()
}
