package com.example.stickers.ads


import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.example.stickers.R
import com.example.stickers.ads.InterAdsClass.Companion.currentInterAd
import com.example.stickers.ads.NativeAdmobClass.Companion.dashboardNativeAd
import com.example.stickers.ads.NativeAdmobClass.Companion.splashNativeAd
import com.example.stickers.dialog.ProgressDialog
import com.google.android.gms.ads.nativead.NativeAd







    fun loadNativeAd(
        activity:Activity,
        frameLayout: FrameLayout,
        status:String,
        layoutInflater: LayoutInflater,
        @LayoutRes layoutRes: Int,
        loadListener: () -> Unit,
        failedListener: () -> Unit
    )
    {

        if(status=="on"&& activity.isNetworkAvailable()&&activity.verifyInstallerId()&& !activity.isAlreadyPurchased())
        {
            frameLayout.visibility=View.VISIBLE
            when {
                dashboardNativeAd==null -> {
                    NativeAdmobClass.getInstance().loadDashboardNative(activity,
                        {
                            showNativeAd(frameLayout,it,status,layoutInflater,layoutRes,{loadListener.invoke()},{failedListener.invoke()})

                        },{
                            failedListener.invoke()

                        })
                }
                dashboardNativeAd!=null -> {
                    showNativeAd(frameLayout,
                        dashboardNativeAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{failedListener.invoke()})
                }
                else -> {
                    frameLayout.visibility=View.GONE
                }
            }
        }
        else{
            failedListener.invoke()
           frameLayout.visibility=View.GONE
    }


    }

fun loadSplashNativeAd(
    activity:Activity,
    frameLayout: FrameLayout,
    status:String,
    layoutInflater: LayoutInflater,
    @LayoutRes layoutRes: Int,
    loadListener: () -> Unit,
    failedListener: () -> Unit
)
{

    if(status=="on"&& activity.isNetworkAvailable() && activity.verifyInstallerId() && !activity.isAlreadyPurchased())
    {
        frameLayout.visibility=View.VISIBLE
        when {
            splashNativeAd==null -> {
                NativeAdmobClass.getInstance().loadSplashNative(activity,
                    {
                        showNativeAd(frameLayout,it,status,layoutInflater,layoutRes,{loadListener.invoke()},{failedListener.invoke()})

                    },{
                        failedListener.invoke()

                    })
            }
            splashNativeAd!=null -> {
                showNativeAd(frameLayout,
                    splashNativeAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{failedListener.invoke()})
            }
            else -> {
                frameLayout.visibility=View.GONE
            }
        }
    }
    else{
        failedListener.invoke()
        frameLayout.visibility=View.GONE
    }


}

    fun showNativeAd(
        frameLayout: FrameLayout,
        nativeAd:NativeAd,
        status:String,
        layoutInflater: LayoutInflater,
        @LayoutRes layoutRes: Int,
        loadListener: () -> Unit,
        failedListener: () -> Unit
    )
    {
        if(status=="on"&& nativeAd!=null)
        {

            NativeAdmobClass.getInstance().showNative(nativeAd,frameLayout,layoutInflater,layoutRes,{})
            loadListener.invoke()
        }
        else{
            failedListener.invoke()
            frameLayout.visibility=View.GONE
        }

    }
 fun loadAdaptiveBanner(activity: Activity,frameLayout: FrameLayout,status:String) {
    if(status=="on"&& activity.isNetworkAvailable() && activity.verifyInstallerId() && !activity.isAlreadyPurchased()) {
        if (AdmobCollapsibleBanner.adaptiveBannerAd == null) {
            AdmobCollapsibleBanner.getInstance().loadAdmobAdaptiveBanner(activity, frameLayout, {})
        } else if (AdmobCollapsibleBanner.adaptiveBannerAd != null) {
            AdmobCollapsibleBanner.getInstance().showAdaptiveBanner(frameLayout)
        } else {
            AdmobCollapsibleBanner.getInstance().loadAdmobSmallBanner(activity, frameLayout)
        }
    }
     else{
         frameLayout.visibility=View.GONE
    }
}

