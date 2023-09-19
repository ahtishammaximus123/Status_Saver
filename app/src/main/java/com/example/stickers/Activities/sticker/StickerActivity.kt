package com.example.stickers.Activities.sticker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.stickers.Activities.PhotoCollage.FileUtils
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.sticker.lib.DrawableSticker
import com.example.stickers.Models.StickerModel
import com.example.stickers.R
import com.example.stickers.Utils.CenterLayoutManager
import com.example.stickers.Utils.getMyColor
import com.example.stickers.Utils.setTextViewDrawableColor
import com.example.stickers.WhatsAppBasedCode.StickerPackDetailsActivity.Companion.adStatus
import com.example.stickers.ads.AdmobCollapsibleBanner
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadAdaptiveBanner

import com.example.stickers.ads.showToast
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.RemoteDateConfig.Companion.remoteAdSettings

import com.example.stickers.app.getUriPath
import com.example.stickers.databinding.ActivityStickerBinding
import com.example.stickers.dialog.ProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class StickerActivity : AppCompatActivity(), StickerAdapter.StickerInterface {

    private val binding by lazy { ActivityStickerBinding.inflate(layoutInflater) }
    private lateinit var options: RequestOptions

    private lateinit var adapter: StickerAdapter
    private var isBgSet = false
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SplashActivity.fbAnalytics?.sendEvent("StickerActivity_Open")
        loadingDialog = ProgressDialog(this, "Loading...")


        val filePath = intent?.extras?.getString("path")
        options = RequestOptions().placeholder(R.drawable.ic_emoji).error(R.drawable.ic_emoji)

        if (!filePath.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            var bitmap = BitmapFactory.decodeFile(filePath, bmOptions)
            bitmap =
                Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

            val drawable: Drawable = BitmapDrawable(resources, bitmap)
            binding.stickerView.addSticker(
                DrawableSticker(
                    drawable
                )
            )
            binding.stickerView.invalidate()

        }
        initRecycler(arrayListOf())
        initViews()
    }

    private fun initViews() {
        with(binding) {
            imgBack.setOnClickListener { onBackPressed() }
            contentLayout.setOnClickListener {
                if (!stickerView.isLocked) {
                    stickerView.isLocked = true
                }
            }
            imgEmoji.setOnClickListener {
                stickerLayout.visibility = View.VISIBLE
                txtTitle.text = getString(R.string.emoji)
//                txtDone.text = getString(R.string.done)
                resetIcons()
                initRecycler(LoadSticker.emojisList)
                imgEmoji.setTextColor(getMyColor(R.color.colorPrimaryDark))
                imgEmoji.setTextViewDrawableColor(R.color.colorPrimaryDark)
                SplashActivity.fbAnalytics?.sendEvent("Sticker_Emoji_Click")
            }
            imgBase.setOnClickListener {
                stickerLayout.visibility = View.VISIBLE
                txtTitle.text = getString(R.string.base)
//                txtDone.text = getString(R.string.done)
                resetIcons()
                initRecycler(LoadSticker.baseList)
                imgBase.setTextColor(getMyColor(R.color.colorPrimaryDark))
                imgBase.setTextViewDrawableColor(R.color.colorPrimaryDark)
                SplashActivity.fbAnalytics?.sendEvent("Sticker_Base_Click")
            }
            txtDone.setOnClickListener {


                    SplashActivity.fbAnalytics?.sendEvent("Sticker_Save_Click")
                    if (!stickerView.isLocked) {
                        stickerView.isLocked = true
                    }
//                if (txtDone.text == getString(R.string.save)) {
                    if (!isBgSet && stickerView.isNoneSticker) {
                        showToast("Kindly Add Sticker to Create Image.")
                        return@setOnClickListener
                    }
                    val fileName = StringBuilder()
                    fileName.append(System.currentTimeMillis())
                    fileName.append(".png")
                    saveLogo(fileName.toString())
                     adStatus="backToThis"
                    return@setOnClickListener
//                }
//                txtTitle.text = getString(R.string.sticker_maker)
//                txtDone.text = getString(R.string.save)
//                resetIcons()
//                stickerLayout.visibility = View.INVISIBLE
                }

            imgEmoji.performClick()
        }
    }


    private fun saveLogo(fileName: String) {

        var path = ""
        CoroutineScope(Dispatchers.IO).launch {
            path = executeSaveLogoTask(binding.stickerView, fileName)

        }.invokeOnCompletion {
            runOnUiThread {
//                showToast(getString(R.string.image_saved))
//                openActivity<SavedStickersActivity>()
                val intent = Intent()
                intent.putExtra("path", "${getUriPath(path)}")
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun executeSaveLogoTask(imageView: View, fileName: String): String {

        val saveImage: Bitmap = imageView.drawToBitmap(Bitmap.Config.ARGB_8888)

        var file = File(FileUtils.getFolderName("StickerMaker"))
        if (!file.exists()) {
            file.mkdir()
        }

        file = File(file, fileName)
        return try {
            val fileOutputStream = FileOutputStream(file)
            saveImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            "" + file.absoluteFile
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun resetIcons() {
        with(binding) {
            imgEmoji.setTextColor(getMyColor(R.color.grey))
            imgBase.setTextColor(getMyColor(R.color.grey))
            imgEmoji.setTextViewDrawableColor(R.color.grey)
            imgBase.setTextViewDrawableColor(R.color.grey)
        }
    }

    private fun initRecycler(stickerList: List<StickerModel>) {
        adapter = StickerAdapter(this, stickerList, this)

        val layoutManager =
            CenterLayoutManager(this@StickerActivity, RecyclerView.HORIZONTAL, false)

        with(binding) {
            recyclerSticker.layoutManager = layoutManager
            recyclerSticker.adapter = adapter
            imgLeft.setOnClickListener {
                recyclerSticker.layoutManager?.apply {
                    val pos = layoutManager.findFirstVisibleItemPosition() + 1
                    recyclerSticker.smoothScrollToPosition(pos)
                }
            }

            imgRight.setOnClickListener {
                recyclerSticker.layoutManager?.apply {
                    val pos = layoutManager.findLastVisibleItemPosition() + 1
                    recyclerSticker.smoothScrollToPosition(pos)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityRunning=false
    }
    override fun onResume() {
        super.onResume()
        isActivityRunning=true
        showInterAd(this@StickerActivity, remoteAdSettings.admob_create_sticker_done_btn_inter_ad.value){}
        val frameBanner = findViewById<FrameLayout>(R.id.banner_adview)
        loadAdaptiveBanner(this,frameBanner, RemoteDateConfig.remoteAdSettings.admob_adaptive_create_sticker_banner_ad.value)
    }
    private fun showInterAd(activity: Activity, status:String, functionalityListener: () -> Unit) {


        if (status=="on"&& adisready=="notshowed"&& InterAdsClass.currentInterAd !=null ) {

            loadingDialog?.dialogShow()
            Handler(Looper.getMainLooper()).postDelayed({
                if(isActivityRunning)
                {
                    InterAdsClass.getInstance().showInterAd123(activity,
                        { functionalityListener.invoke()
                        }, {}, {

                            adisready="showed"
                            loadingDialog?.dismiss()
                        })
                }


            }, 900)
        }
        else{
            functionalityListener.invoke()
        }
    }
    override fun onStickerClick(model: StickerModel) {
        with(binding) {
            if (model.isEmoji) {
                val bitmap = BitmapFactory.decodeResource(resources, model.resId)
                val drawable: Drawable = BitmapDrawable(resources, bitmap)
                stickerView.addSticker(DrawableSticker(drawable))
                stickerView.invalidate()
            } else {
                isBgSet = true
                imgFrame.setImageResource(model.resId)
            }
        }
    }
}