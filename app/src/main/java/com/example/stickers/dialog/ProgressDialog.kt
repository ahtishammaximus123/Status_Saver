package com.example.stickers.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.RelativeLayout
import com.example.stickers.databinding.SProgressDialogBinding

class ProgressDialog(
    activity: Context,
    private val text: String = ""
) : Dialog(activity) {

    private lateinit var binding: SProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SProgressDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCancelable(false)
        window?.apply {
            setLayout(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }
        if (text.isNotEmpty())
            binding.txtTitle.text = text
    }
}