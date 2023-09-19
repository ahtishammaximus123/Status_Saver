/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.example.stickers.WhatsAppBasedCode

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.sticker.GalleryActivity
import com.example.stickers.Activities.sticker.StickerActivity
import com.example.stickers.BuildConfig
import com.example.stickers.R
import com.example.stickers.Utils.WAoptions
import com.example.stickers.WhatsAppBasedCode.StickerPreviewAdapter.StickerClicked
import com.example.stickers.ads.InterAdsClass
import com.example.stickers.ads.loadNativeAd

import com.example.stickers.app.Constants
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.dialog.ProgressDialog

import com.example.stickers.stickers.GridSpacingItemDecoration
import com.example.stickers.stickers.StickerBook
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import java.io.File
import java.util.*

class StickerPackDetailsActivity : BaseActivity(), StickerClicked {
    private var recyclerView: RecyclerView? = null
    private var layoutManager: GridLayoutManager? = null
    private var stickerPreviewAdapter: StickerPreviewAdapter? = null
    private var numColumns = 0
    private var divider: View? = null

    //private var whiteListCheckAsyncTask: WhiteListCheckAsyncTask? = null
    private var deletedStickersList: MutableList<Sticker>? = null
    private var stickerPreviewView: ImageView? = null
    private var trayIcon: ImageView? = null
    private var cast: ImageView? = null
    private var adisready = "notshowed"
    var isActivityRunning = false
    var loadingDialog: ProgressDialog? = null



    var job = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_sticker_pack_details)

        loadingDialog = ProgressDialog(this, "Loading...")
        SplashActivity.fbAnalytics?.sendEvent("StkrPckActy_Open")

        val showUpButton = intent.getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false)
        deletedStickersList = ArrayList()
        trayIcon = findViewById(R.id.imgTrayIcon)
        stickerPack = StickerBook.getStickerPackById(intent.getStringExtra(EXTRA_STICKER_PACK_DATA))
        val title = findViewById<TextView>(R.id.titleAddStickerActivity)
        val author = findViewById<TextView>(R.id.authorAddStickerActivity)
        val back = findViewById<ImageView>(R.id.back_arrow)
        cast = findViewById(R.id.img_cast)
        cast?.setOnClickListener(View.OnClickListener {
            SplashActivity.fbAnalytics?.sendEvent("StickerPackActivity_Mrr Cast")
            try {
                startActivity(Intent("android.settings.CAST_SETTINGS"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Casting is not supported!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        val deleteSelectedStickers = findViewById<ImageView>(R.id.delete_selected_stickers)
//        if (deletedStickersList?.size!! <= 0) {
//            deleteSelectedStickers.visibility = View.GONE
//        }
        deleteSelectedStickers.setOnClickListener {
            SplashActivity.fbAnalytics?.sendEvent("StkrPckActy_Del")
            if (deletedStickersList!!.size <= 0) {
                Toast.makeText(
                    applicationContext,
                    "Select any sticker first...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                for (i in deletedStickersList!!.indices) {
                    val sc = deletedStickersList!!.get(i)
                    stickerPack?.deleteSticker(sc)
                }
                finish()
                overridePendingTransition(0, 0)
                this@StickerPackDetailsActivity.startActivity(this@StickerPackDetailsActivity.intent)
                Toast.makeText(applicationContext, "Stickers has been deleted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //TextView packPublisherTextView = findViewById(R.id.author);
        //ImageView packTrayIcon = findViewById(R.id.tray_image);
        back.setOnClickListener { onBackPressed() }
        val addToWhatsappBTNCV = findViewById<MaterialButton>(R.id.btnAddNewSticker)
        layoutManager = GridLayoutManager(this, 3)
        recyclerView = findViewById(R.id.sticker_list)
        recyclerView?.setLayoutManager(layoutManager)

//        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
//        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider)
        if (stickerPreviewAdapter == null && stickerPack != null) {

            //stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack);
            stickerPreviewAdapter =
                StickerPreviewAdapter(layoutInflater, R.drawable.sticker_error, stickerPack!!, this)
            val spanCount = 3 // 3 columns
            val spacing = 70 // 50px
            recyclerView?.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
            recyclerView?.setAdapter(stickerPreviewAdapter)
        }
        title.text = stickerPack?.name
        author.text = stickerPack?.publisher + ""
        val categoryPref = getSharedPreferences("category_saved", MODE_PRIVATE)
        val drawable =
            categoryPref.getInt(stickerPack?.name + stickerPack?.publisher, R.drawable.happy)
        trayIcon?.setImageResource(drawable)
        setTrayIcon()

        //packPublisherTextView.setText(stickerPack.publisher);
        //packTrayIcon.setImageURI(stickerPack.getTrayImageUri());
        findViewById<View>(R.id.openFileForSticker).setOnClickListener {
            // openFile();
        }
        addToWhatsappBTNCV.setOnClickListener {
            sharePackToWhatsApp()
            //sharePackToAsZIP();
        }
        if (StickerBook.allStickerPacks.isEmpty()) {
            StickerBook.init(this)
        }
        //
//        deletePackBTNCV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deletePack();
//            }
//        });
    }

    private fun setTrayIcon() {
        if (!stickerPack?.stickers.isNullOrEmpty()) {
            trayIcon?.setImageURI(stickerPack?.stickers?.get(0)?.getUri())
        } else {
            val categoryPref = getSharedPreferences("category_saved", MODE_PRIVATE)
            val drawable =
                categoryPref.getInt(stickerPack?.name + stickerPack?.publisher, R.drawable.happy)
            trayIcon?.setImageResource(drawable)
        }
    }

    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    private fun deletePack() {
        val alertDialog = AlertDialog.Builder(this@StickerPackDetailsActivity)
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
            .setPositiveButton("Confirm") { dialogInterface, i ->
                dialogInterface.dismiss()
                StickerBook.deleteStickerPackById(stickerPack!!.getIdentifier())
                onBackPressed()
                Toast.makeText(
                    this@StickerPackDetailsActivity,
                    "Sticker Pack deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }.create()
        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("Deleting this package will also remove it from your WhatsApp app.")
        alertDialog.show()
    }

    private fun sharePackToWhatsApp() {
        try {
            if (stickerPack!!.stickers.size >= 3) {
                if (stickerPack!!.getIdentifier() != null && !stickerPack!!.getIdentifier()
                        .isEmpty()
                ) {
                    val newId = UUID.randomUUID().toString()
                    var newTray = stickerPack!!.trayImageUri
                    val newName = stickerPack!!.name
                    val newCreator = stickerPack!!.publisher
                    val ls = stickerPack!!.stickers
                    for (sp in StickerBook.allStickerPacks) {
                        if (sp.getIdentifier() == stickerPack!!.getIdentifier()) {
                            File(sp.getTrayImageUri().path).parentFile.delete()
                            StickerBook.allStickerPacks.remove(sp)
                        }
                    }
                    val sp1 = StickerPack(
                        newId,
                        newName,
                        newCreator,
                        newTray,
                        "",
                        "",
                        "",
                        "",
                        this
                    )
                    for (s in ls) {
                        newTray = ls[0].getUri()
                        sp1.trayImageUri = newTray
                        sp1.addSticker(s.getUri(), this)
                    }
                    StickerBook.addStickerPackExisting(sp1)
                    stickerPack = sp1
                }
                addStickerPackToWhatsApp(stickerPack)
            } else {
                val alertDialog = AlertDialog.Builder(this@StickerPackDetailsActivity)
                    .setNegativeButton("Ok") { dialogInterface, i -> dialogInterface.dismiss() }
                    .create()
                alertDialog.setTitle("Invalid Action")
                alertDialog.setMessage("In order to be applied to WhatsApp, the sticker pack must have at least 3 stickers. Please add more stickers first.")
                alertDialog.show()
            }
        } catch (exp: Exception) {
            sharePackToWhatsApp()
        }
    }

    private fun openFile() {

        val intent = Intent(this, GalleryActivity::class.java)
        startActivityForResult(intent, 3000)

//        isApplovinClicked = true
//        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(i, 3000)

        //        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.setType("image/*");
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), 3000);
    }

    private fun addStickerPackToWhatsApp(sp: StickerPack?) {
        Log.e("tag**", "pkg - " + WAoptions.appPackage)
        Log.e("tag**", " " + sp!!.getIdentifier() + "" + sp.getName())
        val intent = Intent()
        intent.action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
        Log.w("IS IT A NEW IDENTIFIER?", sp.getIdentifier())
        intent.putExtra(EXTRA_STICKER_PACK_ID, sp.getIdentifier())
        intent.putExtra(
            EXTRA_STICKER_PACK_AUTHORITY,
            Constants.providerWhatsApp + ".WhatsAppLicensedCode.StickerContentProvider"
        )
        intent.setPackage(WAoptions.appPackage)
        intent.putExtra(EXTRA_STICKER_PACK_NAME, sp.getName())
        try {
            startActivityForResult(intent, ADD_PACK)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show()
        }
        isApplovinClicked = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PACK) {

                finish()

            if (resultCode == RESULT_CANCELED && data != null) {
                val validationError = data.getStringExtra("validation_error")
                if (validationError != null) {
                    if (BuildConfig.DEBUG) {
                        //validation error should be shown to developer only, not users.
                        MessageDialogFragment.newInstance(
                            R.string.title_validation_error,
                            validationError
                        ).show(
                            supportFragmentManager, "validation error"
                        )
                    }
                    Log.e(
                        TAG,
                        "Validation failed: on Activity result 200 ( ADD_PACK )$validationError"
                    )
                    Log.e("tag***", validationError)
                }
            } else if (resultCode == RESULT_OK) {

                SplashActivity.fbAnalytics?.sendEvent("StickerPackActivity_Added2WA")
//                if (RemoteDateConfig.getRemoteAdSettings().getInter_sticker_added().getValue().equalsIgnoreCase("on"))
//                    showApplovinInter(StickerPackDetailsActivity.this, () -> null);
            }
        } else if (requestCode == 3000 && resultCode == RESULT_OK) {
//            Log.e("intentcheckif", "opening   else if (requestCode == 3000")
//            // Toast.makeText(StickerPackDetailsActivity.this, "else if comming", Toast.LENGTH_SHORT).show();
            if (data != null) {
                val uri =
                    data.getStringExtra("path") ?: ""

                val intent = Intent(this, StickerActivity::class.java)
                intent.putExtra("path", uri)
                startActivityForResult(intent, CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                Log.d("intentcheckif", "opening if   ")
                Log.e("tagUri*", "URI : $uri")
//                CropImage.activity(uri.toUri()).start(this)
            }

        } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val uri =
                    data.getStringExtra("path") ?: ""
                Log.e("tag**", "resUri : $uri")
                stickerPack?.addSticker(uri.toUri(), this)
            }
//            Log.e("intentcheckif", "opening else if   ")
//            val result = CropImage.getActivityResult(data)
//            val resultUri = result.uri
//            Log.e("tag**", "resUri : $resultUri")
//            stickerPack!!.addSticker(resultUri, this)
        }
    }

    private val pageLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        setNumColumns(
            recyclerView!!.width / recyclerView!!.context.resources.getDimensionPixelSize(
                R.dimen.sticker_pack_details_image_size
            )
        )
    }

    private fun setNumColumns(numColumns: Int) {
        if (this.numColumns != numColumns) {
            layoutManager!!.spanCount = numColumns
            this.numColumns = numColumns
            if (stickerPreviewAdapter != null) {
                stickerPreviewAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private val dividerScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                updateDivider(recyclerView)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDivider(recyclerView)
            }

            private fun updateDivider(recyclerView: RecyclerView) {
                val showDivider = recyclerView.computeVerticalScrollOffset() > 0
                if (divider != null) {
                    divider!!.visibility =
                        if (showDivider) View.VISIBLE else View.INVISIBLE
                }
            }
        }

    override fun onResume() {
        super.onResume()

        isActivityRunning=true

        if(RemoteDateConfig.remoteAdSettings.admob_create_sticker_create_btn_inter_ad.value=="on"&& adStatus=="comingToThis")
        {

            showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_create_sticker_create_btn_inter_ad.value){
            }
        }
        else if(RemoteDateConfig.remoteAdSettings.admob_create_sticker_done_btn_inter_ad.value=="on"&&adStatus=="backToThis"){
            adisready="notshowed"
            showInterAd(this, RemoteDateConfig.remoteAdSettings.admob_create_sticker_create_btn_inter_ad.value){
            }
        }

        val frame = findViewById<FrameLayout>(R.id.sticker_maker_native)
        loadNativeAd(this,frame!!,
            RemoteDateConfig.remoteAdSettings.admob_native_sticker_maker_ad.value,layoutInflater,R.layout.gnt_medium_template_without_media_view,{ },{})

        setTrayIcon()
        stickerPreviewAdapter?.notifyDataSetChanged()
    }
    private fun showInterAd(activity: Activity, status:String, functionalityListener: () -> Unit) {

        if (status=="on"&& adisready=="notshowed"&& InterAdsClass.currentInterAd !=null ) {
            adStatus=""
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
    override fun onPause() {
        super.onPause()
        isActivityRunning=false
        Log.e("checkcccheckcccheck", "on pause")
//        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask!!.isCancelled) {
//            whiteListCheckAsyncTask!!.cancel(true)
//        }
    }

    fun updateAddUI(isWhitelisted: Boolean?) {
//        if (isWhitelisted) {
//            addButton.setVisibility(View.GONE);
//            alreadyAddedText.setVisibility(View.VISIBLE);
//        } else {
//            addButton.setVisibility(View.VISIBLE);
//            alreadyAddedText.setVisibility(View.GONE);
//        }
    }

    override fun onCheckBoxChecked(i: Int, v: View, isChecked: Boolean) {
        val img = v.findViewById<ImageView>(R.id.sticker_preview)
        val thisSticker = stickerPack!!.getSticker(i)
        if (isChecked) deletedStickersList!!.add(thisSticker) else deletedStickersList!!.remove(
            thisSticker
        )
    }

    override fun onAddSticker(position: Int, v: View) {
        stickerPreviewView = v.findViewById(R.id.sticker_preview)
        Log.e("checktag", stickerPreviewView?.getTag().toString() + "")
        if (stickerPreviewView?.getTag() == "no_plus") {
//            Toast.makeText(getApplicationContext(), "Already added.", Toast.LENGTH_SHORT).show();
        } else openFile()
    }

    companion object {
        const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
        const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
        const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
        const val ADD_PACK = 200
        const val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203
        const val EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website"
        const val EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email"
        const val EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy"
        const val EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon"
        const val EXTRA_SHOW_UP_BUTTON = "show_up_button"
        const val EXTRA_STICKER_PACK_DATA = "sticker_pack"
        private const val TAG = "StickerPackDetails"
         var adStatus = "comingToThis"
        @JvmField
        var stickerPack: StickerPack? = null
        fun saveSticker(yourUriSt: Uri?, context: Context?) {
            stickerPack!!.addSticker(yourUriSt, context)
        }
    }

}

//internal class WhiteListCheckAsyncTask(stickerPackListActivity: StickerPackDetailsActivity) :
//    AsyncTask<StickerPack?, Void?, Boolean>() {
//    private val stickerPackDetailsActivityWeakReference: WeakReference<StickerPackDetailsActivity>
//    protected override fun doInBackground(vararg stickerPacks: StickerPack): Boolean {
//        val stickerPack = stickerPacks[0]
//        val stickerPackDetailsActivity =
//            stickerPackDetailsActivityWeakReference.get() ?: return false
//        return WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier)
//    }
//
//    override fun onPostExecute(isWhitelisted: Boolean) {
//        val stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get()
//        stickerPackDetailsActivity?.updateAddUI(isWhitelisted)
//    }
//
//    init {
//        stickerPackDetailsActivityWeakReference = WeakReference(stickerPackListActivity)
//    }
//}