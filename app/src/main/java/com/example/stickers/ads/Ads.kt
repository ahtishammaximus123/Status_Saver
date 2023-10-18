package com.example.stickers.ads


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.example.stickers.ads.NativeAdmobClass.Companion.alreadyRequested
import com.example.stickers.ads.NativeAdmobClass.Companion.secondPiroritySecondAd
import com.example.stickers.ads.NativeAdmobClass.Companion.firstPirorityNativeAd
import com.example.stickers.ads.NativeAdmobClass.Companion.splashNativeAd
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
                firstPirorityNativeAd==null -> {
                    if(!alreadyRequested)
                    {
                        alreadyRequested=true
                    NativeAdmobClass.getInstance().loadDashboardNative(activity,
                        {
                            showNativeAd(frameLayout,it,status,layoutInflater,layoutRes,{loadListener.invoke()},{  frameLayout.visibility=View.GONE
                                failedListener.invoke()})

                          //  alreadyRequested=false
                        },{
                            NativeAdmobClass.getInstance().loadSecondPirorityNativeAd(activity,
                                {
                                    showNativeAd(frameLayout,it,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                                        frameLayout.visibility=View.GONE
                                        failedListener.invoke()})

                                },{
                                    frameLayout.visibility=View.GONE
                                    failedListener.invoke()

                                })
                        })
                    }
                    else if( firstPirorityNativeAd!=null)
                    {  showNativeAd(frameLayout,
                        firstPirorityNativeAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                            frameLayout.visibility=View.GONE
                            failedListener.invoke()})

                    }
                    else if( secondPiroritySecondAd!=null && firstPirorityNativeAd==null)
                    {  showNativeAd(frameLayout,
                        secondPiroritySecondAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                            frameLayout.visibility=View.GONE
                            failedListener.invoke()})

                    }
                    else{
                        failedListener.invoke()
                        frameLayout.visibility=View.GONE
                    }
                }
                firstPirorityNativeAd!=null -> {
                    showNativeAd(frameLayout,
                        firstPirorityNativeAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                            frameLayout.visibility=View.GONE
                            failedListener.invoke()})
                }

                (secondPiroritySecondAd!=null && firstPirorityNativeAd==null) -> {
                    showNativeAd(frameLayout,
                        secondPiroritySecondAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                            frameLayout.visibility=View.GONE
                            failedListener.invoke()})
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
                        showNativeAd(frameLayout,it,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                            frameLayout.visibility=View.GONE
                            failedListener.invoke()})


                    },{
                        frameLayout.visibility=View.GONE
                        failedListener.invoke()

                    })
            }
            splashNativeAd!=null -> {
                showNativeAd(frameLayout,
                    splashNativeAd!!,status,layoutInflater,layoutRes,{loadListener.invoke()},{
                        frameLayout.visibility=View.GONE
                        failedListener.invoke()})
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

