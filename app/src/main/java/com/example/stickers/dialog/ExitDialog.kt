package com.example.stickers.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.RelativeLayout
import com.example.stickers.databinding.DialogExitBinding

class ExitDialog(
    activity: Activity,
    private val listener: () -> Unit,
) : Dialog(activity) {

    private lateinit var binding: DialogExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.apply {
            setLayout(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }

        with(binding) {
            btnYesexit.setOnClickListener {
                listener.invoke()
                dismiss()
            }
            btnNoexit.setOnClickListener {
                dismiss()
            }
        }
    }
}