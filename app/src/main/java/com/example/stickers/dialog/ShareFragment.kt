package com.example.stickers.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons
import com.example.stickers.app.getUriPath
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShareFragment(val path: Uri) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val link = "http://play.googlee.com/store/apps/details?id=${requireActivity().packageName}"
        val shareMessage =
            "You can save all WhatsApp Status for free and fast. \n Download it here: $link".trimIndent()
        // Find views and set click listeners
        val whatsapp = view.findViewById<Button>(R.id.whats_app_share)
        val other = view.findViewById<Button>(R.id.other_share)

        whatsapp.setOnClickListener {
            if (AppCommons.isAppInstalled(requireActivity(), "com.whatsapp")) {
                try {
                    try {

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        if (path.path?.contains("jpg") == true) {
                            shareIntent.type = "image/*"
                        } else {
                            shareIntent.type = "video/*"
                        }
                        shareIntent.setPackage("com.whatsapp")
                        shareIntent.putExtra(
                            Intent.EXTRA_STREAM,
                            requireActivity().getUriPath(path.path.toString())
                        )
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(shareIntent, "Share"))
                    } catch (e: Exception) {
                        Log.e("error__", e.toString())
                    }
                } catch (e: Exception) {
                    Log.e("WhatsAppicon", "4: Clicked")
                    Toast.makeText(
                        requireActivity(),
                        "Whatsapp not installed!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Whatsapp not installed!",
                    Toast.LENGTH_LONG
                ).show()
            }
            dismiss()
        }
        other.setOnClickListener {

            try {

                val shareIntent = Intent(Intent.ACTION_SEND)
                if (path.path?.contains("jpg") == true) {
                    shareIntent.type = "image/*"
                } else {
                    shareIntent.type = "video/*"
                }

                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    requireActivity().getUriPath(path.path.toString())
                )
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "Share"))
            } catch (e: Exception) {
                Log.e("error__", e.toString())
            }
            dismiss()
        }
    }
}



