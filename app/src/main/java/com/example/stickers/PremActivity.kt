package com.example.stickers

import ai.lib.billing.BillingClientConnectionListener
import ai.lib.billing.DataWrappers
import ai.lib.billing.IapConnector
import ai.lib.billing.PurchaseServiceListener
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.example.stickers.Activities.SplashActivity.Companion.fbAnalytics
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Utils.openActivity
import com.example.stickers.ads.afterDelay
import com.example.stickers.ads.isNetworkAvailable
import com.example.stickers.ads.showToast
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.Constants
import com.example.stickers.app.SharedPreferenceData

class PremActivity : BillingBaseActivity() {

    private lateinit var iapConnector: IapConnector
    private var isBillingClientConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_premium)

        fbAnalytics?.sendEvent("PremiumActivity_Open")

        val close = findViewById<ImageView>(R.id.closePremium)
        val buyNow = findViewById<ImageView>(R.id.imageView14)
        close.setOnClickListener { onBackPressed() }
        close.visibility = View.VISIBLE

        buyNow.setOnClickListener { v: View? ->
            // subscribe(Constants.subsList.get(0));
            buyNow.isEnabled = false
            afterDelay(1500) {
                buyNow.isEnabled = true
            }
            purchase(Constants.subsList[0])
            fbAnalytics?.sendEvent("PremiumActivity_buy")
        }

        initBillingListener()
    }

    private fun initBillingListener() {
        iapConnector = IapConnector(
            context = this,
            consumableKeys = Constants.subsList,
            enableLogging = true
        )

        iapConnector.addBillingClientConnectionListener(object : BillingClientConnectionListener {
            override fun onConnected(status: Boolean, billingResponseCode: Int) {
                Log.d(
                    "KSA",
                    "This is the status: $status and response code is: $billingResponseCode"
                )
                isBillingClientConnected = status
            }
        })


        iapConnector.addPurchaseListener(object : PurchaseServiceListener {

            override fun onEmptyPurchasedList() {
                Log.e("tagV4*", "onEmptyPurchasedList")
                SharedPreferenceData(this@PremActivity).putBoolean(
                    Constants.KEY_IS_PURCHASED,
                    false
                )
            }

            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.SkuDetails>) {
                // list of available products will be received here, so you can update UI with prices if needed
                Log.e("tagV4*", "iapKeyPrices $iapKeyPrices")
                val prices = HashMap<String, String>()
                Constants.subsList.forEach {
                    val currency = iapKeyPrices[it]?.priceCurrencyCode ?: ""
                    val price = iapKeyPrices[it]?.price ?: ""
                    prices[it] = "$currency $price"
                }
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered whenever subscription succeeded
                when (purchaseInfo.sku) {
                    Constants.subsList[0] -> {
                        Log.e("tagV4*", "onSubscriptionRestored $purchaseInfo")
                        SharedPreferenceData(this@PremActivity).putBoolean(
                            Constants.KEY_IS_PURCHASED,
                            true
                        )
                        showToast("Successfully Subscribed!")
                        finishAffinity()
                        openActivity<MainDashActivity>()
                    }
                }
            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered upon fetching owned subscription upon initialization
                Log.e("tagV4*", "onSubscriptionRestored $purchaseInfo")
                when (purchaseInfo.sku) {
                    Constants.subsList[0] -> {
                        Log.e("tagV4*", "onSubscriptionRestored $purchaseInfo")
                        SharedPreferenceData(this@PremActivity).putBoolean(
                            Constants.KEY_IS_PURCHASED,
                            true)
                    }
                }
            }
        })
    }

    private fun purchase(skuId: String) {
        if (isNetworkAvailable()) {
            if (isBillingClientConnected)
                iapConnector.purchase(this, skuId)
            else
                Toast.makeText(this, "Billing Not connected!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

}