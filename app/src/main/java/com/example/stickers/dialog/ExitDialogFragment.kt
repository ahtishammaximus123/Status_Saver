package com.example.stickers.dialog
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.stickers.R
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.system.exitProcess

class ExitDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.BottomSheetDialog)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return inflater.inflate(R.layout.fragment_exit_dialog, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views and set click listeners
        val exitButton = view.findViewById<Button>(R.id.exit_btn)

        exitButton.setOnClickListener {
            requireActivity().finishAffinity()
            exitProcess(0)
        }
    }

    override fun onResume() {
        super.onResume()
        val frame = view?.findViewById<FrameLayout>(R.id.exit_native)
        loadNativeAd(requireActivity(),frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_exit_dialog_ad.value,layoutInflater,R.layout.gnt_medium_template_view,{ },{})
    }
}



