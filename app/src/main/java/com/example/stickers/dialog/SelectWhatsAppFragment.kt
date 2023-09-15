package com.example.stickers.dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.example.stickers.R
import com.example.stickers.Utils.AppCommons
import com.example.stickers.Utils.WAoptions
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.ads.loadSplashNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.SharedPreferenceData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectWhatsAppFragment(val openWhat: ImageView, val proceedListenerr: () -> Unit) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.BottomSheetDialog)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.select_whats_app_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable=false
        // Find views and set click listeners
        val simplewhats = view.findViewById<Button>(R.id.go_for_simple)
        val business = view.findViewById<Button>(R.id.go_for_business)

        simplewhats.setOnClickListener {

            SharedPreferenceData(requireActivity()).putString("apppackage", WAoptions.appPackage)
            SharedPreferenceData(requireActivity()).putBoolean("ComingFirstTime", false)
            if (AppCommons.isAppInstalled(requireActivity(), "com.whatsapp")) {

                SharedPreferenceData(requireActivity()).putString("apppackage", "com.whatsapp")

                openWhat.setBackgroundResource(R.drawable.whats_app_icon)
                proceedListenerr.invoke()

            } else Toast.makeText(
                requireActivity(),
                "WhatsApp is not installed",
                Toast.LENGTH_LONG
            ).show()
dismiss()
        }
        business.setOnClickListener {
            SharedPreferenceData(requireActivity()).putString("apppackage", "com.whatsapp.w4b")
            SharedPreferenceData(requireActivity()).putBoolean("ComingFirstTime", false)
            if (AppCommons.isAppInstalled(requireActivity(), "com.whatsapp.w4b")) {
                WAoptions.appPackage = "com.whatsapp.w4b"
                SharedPreferenceData(requireActivity()).putString(
                    "apppackage",
                    WAoptions.appPackage
                )
                openWhat.setBackgroundResource(R.drawable.w_business_icon)
                proceedListenerr.invoke()
            } else Toast.makeText(
                requireActivity(),
                "WhatsApp Business is not installed",
                Toast.LENGTH_LONG
            ).show()
            dismiss()

        }
    }

    override fun onResume() {
        super.onResume()
        val frame = view?.findViewById<FrameLayout>(R.id.select_whatsapp_native)
        loadNativeAd(requireActivity(),frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_select_whats_app_ad.value,layoutInflater,R.layout.gnt_medium_template_view,{ },{})
    }
}



