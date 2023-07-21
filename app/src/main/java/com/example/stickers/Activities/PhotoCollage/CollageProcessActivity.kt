package com.example.stickers.Activities.PhotoCollage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stickers.Activities.CollageFilesActivity
import com.example.stickers.Activities.PhotoCollage.sizes.BgAdapter
import com.example.stickers.Activities.PhotoCollage.sizes.SizeAdapter
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.ui.photocollage.PhotoCollageViewModel
import com.example.stickers.Activities.newDashboard.ui.photocollage.PhotoCollageViewModelFactory
import com.example.stickers.Activities.sticker.LoadSticker
import com.example.stickers.Models.BgModel
import com.example.stickers.R
import com.example.stickers.Utils.*
import com.example.stickers.ads.afterDelay
import com.example.stickers.ads.loadNativeAdmob
import com.example.stickers.ads.showInterAd
import com.example.stickers.ads.showToast
import com.example.stickers.app.AppClass
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.xiaopo.flying.poiphoto.Define
import com.xiaopo.flying.puzzle.PuzzleLayout
import com.xiaopo.flying.puzzle.PuzzleView
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout
import com.xinlan.imageeditlibrary.SingleStickerImageCollage
import com.xinlan.imageeditlibrary.StickerImageCollage


class CollageProcessActivity : BillingBaseActivity(), StickerImageCollage,
    SingleStickerImageCollage {

    private var puzzleLayout: PuzzleLayout? = null
    private var bitmapPaint: List<String>? = null
    private var puzzleView: PuzzleView? = null

    private val targets: MutableList<Target> = ArrayList()
    private var deviceWidth = 0

    private var type = 0
    private var pieceSize = 0
    private var themeId = 0


    private val sizeList = arrayListOf<String>()
    private var sizeAdapter: SizeAdapter? = null
    private var sizeRecycler: RecyclerView? = null
    private lateinit var puzzleList: RecyclerView
    private lateinit var bgRecycler: RecyclerView
    private lateinit var bgLayout: ConstraintLayout
    private lateinit var layoutSize: ConstraintLayout
    private lateinit var layoutPuzzle: ConstraintLayout
    private lateinit var layoutSaveImage: ConstraintLayout
    private var bgAdapter: BgAdapter? = null

    private lateinit var seekbarBorder: SeekBar
    private lateinit var seekbarRound: SeekBar

    private lateinit var imgLeftP: ImageView
    private lateinit var imgRightP: ImageView
    private lateinit var imgLeftB: ImageView
    private lateinit var imgRightB: ImageView
    private lateinit var imgLeftS: ImageView
    private lateinit var imgRightS: ImageView

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_done -> {
                val file = FileUtils.getNewFile(this@CollageProcessActivity, "Puzzle")
                try {
                    showInterAd(RemoteDateConfig.remoteAdSettings.inter_collage_save_photo) {
                        Log.e("tagPath*", file.absolutePath)
                        layoutSaveImage.isDrawingCacheEnabled = true
                        val parent = layoutSaveImage.drawingCache

                        FileUtils.savePuzzleFromConstraints(
                            puzzleView,
                            parent,
                            file,
                            100,
                            object : Callback {
                                override fun onSuccess() {
                                    gotoNextTask()
                                }

                                override fun onFailed() {
                                    showToast("Save Failed")
                                }
                            })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val model: PhotoCollageViewModel by viewModels {
        PhotoCollageViewModelFactory((application as AppClass).photosRep)
    }

    private fun initSizeAdapter() {
        sizeList.add("1:1")
        sizeList.add("4:5")
        sizeList.add("16:9")
        sizeList.add("9:16")
        sizeList.add("3:4")
        sizeList.add("4:3")
        sizeList.add("1:4")
        sizeList.add("2:3")
        sizeList.add("3:2")
        sizeList.add("2:1")
        sizeList.add("1:2")
        sizeAdapter = SizeAdapter(sizeList, object : SizeAdapter.FileListener {
            override fun onClickImage(item: String) {
                puzzleView?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    dimensionRatio = item
                }
            }
        })
        sizeRecycler = findViewById(R.id.sizeList)
        sizeRecycler?.layoutManager =
            CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sizeRecycler?.adapter = sizeAdapter
    }

    private fun initBgAdapter(bgList: MutableList<BgModel>) {
        bgAdapter = BgAdapter(bgList, object : BgAdapter.FileListener {
            override fun onClickImage(item: BgModel) {
                try {
                    if (item.isColor)
                        puzzleView?.setBackgroundColor(item.background)
                    else
                        puzzleView?.background = getMyDrawable(item.background)
                } catch (exp: Resources.NotFoundException) {
                }
            }
        })
        bgRecycler = findViewById(R.id.bgRecycler)
        bgRecycler.layoutManager =
            CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bgRecycler.adapter = bgAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_collage_process)

        val adRoot = findViewById<ConstraintLayout>(R.id.nativeLayout)
        val adFrameLarge = findViewById<FrameLayout>(R.id.adFrameLarge)
        loadNativeAdmob(
            adRoot, adFrameLarge,
            R.layout.admob_native_small,
            R.layout.max_native_small, 2,
            RemoteDateConfig.remoteAdSettings.native_inner,{},{},adId = RemoteDateConfig.remoteAdSettings.getAdmobSplashNativeId2()
        )
        SplashActivity.fbAnalytics?.sendEvent("CollageActivity_Open")

        deviceWidth = resources.displayMetrics.widthPixels
        type = intent.getIntExtra("type", 0)
        pieceSize = intent.getIntExtra("piece_size", 0)
        themeId = intent.getIntExtra("theme_id", 0)
        bitmapPaint = intent.getStringArrayListExtra("photo_path")
        model.type.value = type
        model.pieceSize.value = pieceSize
        model.themeId.value = themeId
        model.bitmapPaint.value = bitmapPaint

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Size"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        layoutSaveImage = findViewById(R.id.save_layout)
        puzzleList = findViewById(R.id.puzzle_list)
        Log.e("sizeTag", "Size: $pieceSize")
        puzzleLayout = PuzzleUtils.getPuzzleLayout(type, pieceSize, themeId)
        puzzleView = findViewById(R.id.puzzle_view)
        bgLayout = findViewById(R.id.bgLayout)
        layoutPuzzle = findViewById(R.id.layoutPuzzle)
        layoutSize = findViewById(R.id.layoutSize)

        seekbarBorder = findViewById(R.id.seekbarBorder)
        seekbarRound = findViewById(R.id.seekbarRound)

        init()
        initSizeAdapter()


        puzzleView?.isTouchEnable = true
        puzzleView?.isNeedDrawLine = false
        puzzleView?.isNeedDrawOuterLine = false
        puzzleView?.lineSize = 2
        puzzleView?.lineColor = Color.BLACK
        puzzleView?.selectedLineColor = Color.BLACK
        puzzleView?.handleBarColor = Color.BLACK
        puzzleView?.setAnimateDuration(300)
        puzzleView?.piecePadding = 10f


        val pLayoutManager =
            CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        puzzleList.layoutManager = pLayoutManager
        val puzzleAdapter = PuzzleAdapter(1)
        puzzleList.adapter = puzzleAdapter
        puzzleAdapter.setOnItemClickListener { puzzleLayout, themeId ->
            type = if (puzzleLayout is SlantPuzzleLayout) {
                0
            } else {
                1
            }
            pieceSize = puzzleLayout.areaCount
            this@CollageProcessActivity.themeId = themeId
            this@CollageProcessActivity.puzzleLayout =
                PuzzleUtils.getPuzzleLayout(type, pieceSize, themeId)

            PuzzleUtils.getPuzzleLayout(type, pieceSize, themeId)

            puzzleView?.setPuzzleLayout(this@CollageProcessActivity.puzzleLayout)

            loadPhoto()
            Log.e("atgPzl*", "pieceSize $pieceSize, themeId: $themeId, $puzzleLayout")
        }
        puzzleAdapter.refreshData(
            PuzzleUtils.getPuzzleLayoutsCustomOnDemand(pieceSize), null
        )

        imgLeftP = findViewById(R.id.imgLeftP)
        imgRightP = findViewById(R.id.imgRightP)
        imgLeftB = findViewById(R.id.imgLeftB)
        imgRightB = findViewById(R.id.imgRightB)
        imgLeftS = findViewById(R.id.imgLeftS)
        imgRightS = findViewById(R.id.imgRightS)

        setBottomNav()
        setSeekbar()
        setBgStyle()
        initBgAdapter(LoadSticker.colorList)
        setScrollClicks()

    }

    private fun setScrollClicks() {

        imgLeftP.setOnClickListener {
            puzzleList.layoutManager?.apply {
                val pos =
                    (puzzleList.layoutManager as CenterLayoutManager).findFirstVisibleItemPosition() + 1
                puzzleList.smoothScrollToPosition(pos)
            }
        }
        imgRightP.setOnClickListener {
            puzzleList.layoutManager?.apply {
                val pos =
                    (puzzleList.layoutManager as CenterLayoutManager).findLastVisibleItemPosition() + 1
                puzzleList.smoothScrollToPosition(pos)
            }
        }

        imgLeftB.setOnClickListener {
            bgRecycler.layoutManager?.apply {
                val pos =
                    (bgRecycler.layoutManager as CenterLayoutManager).findFirstVisibleItemPosition() + 1
                bgRecycler.smoothScrollToPosition(pos)
            }
        }
        imgRightB.setOnClickListener {
            bgRecycler.layoutManager?.apply {
                val pos =
                    (bgRecycler.layoutManager as CenterLayoutManager).findLastVisibleItemPosition() + 1
                bgRecycler.smoothScrollToPosition(pos)
            }
        }

        imgLeftS.setOnClickListener {
            sizeRecycler?.layoutManager?.apply {
                val pos =
                    (sizeRecycler?.layoutManager as CenterLayoutManager).findFirstVisibleItemPosition() + 1
                sizeRecycler?.smoothScrollToPosition(pos)
            }
        }
        imgRightS.setOnClickListener {
            sizeRecycler?.layoutManager?.apply {
                val pos =
                    (sizeRecycler?.layoutManager as CenterLayoutManager).findLastVisibleItemPosition() + 1
                sizeRecycler?.smoothScrollToPosition(pos)
            }
        }
    }

    private fun setSeekbar() {
//
        seekbarBorder.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                puzzleView?.isNeedDrawLine = true
                puzzleView?.lineSize = progress
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
            }
        })
        seekbarRound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                puzzleView?.pieceRadian = progress.toFloat()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    private fun setBottomNav() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.SizeFragment -> {
                    SplashActivity.fbAnalytics?.sendEvent("collage_size_click")
                    supportActionBar?.title = "Size"
                    layoutSize.visibility = View.VISIBLE
                    layoutPuzzle.visibility = View.GONE
                    bgLayout.visibility = View.GONE
                }
                R.id.LayoutsFragment -> {
                    SplashActivity.fbAnalytics?.sendEvent("collage_layout_click")
                    supportActionBar?.title = "Layouts"
                    layoutSize.visibility = View.GONE
                    layoutPuzzle.visibility = View.VISIBLE
                    bgLayout.visibility = View.GONE
                }
                R.id.BackgroundFragment -> {
                    SplashActivity.fbAnalytics?.sendEvent("collage_bg_click")
                    supportActionBar?.title = "Background"
                    layoutSize.visibility = View.GONE
                    layoutPuzzle.visibility = View.GONE
                    bgLayout.visibility = View.VISIBLE
                }
            }

            return@setOnItemSelectedListener true

        }
    }

    private fun AppCompatButton.resetTab(btn1: AppCompatButton, btn2: AppCompatButton) {
        background = getMyDrawable(R.drawable.round_left_stroke)
        backgroundTintList = getColorTint(R.color.transparent)
        setTextColor(getMyColor(R.color.dark_grey))
        tag = getString(R.string.false_)

        btn1.background = getMyDrawable(R.drawable.round_stroke_)
        btn1.backgroundTintList = null//getColorState(R.color.transparent)
        btn1.setTextColor(getMyColor(R.color.dark_grey))
        btn1.tag = getString(R.string.false_)

        btn2.background = getMyDrawable(R.drawable.round_right_stroke)
        btn2.backgroundTintList = getColorTint(R.color.transparent)
        btn2.setTextColor(getMyColor(R.color.dark_grey))
        btn2.tag = getString(R.string.false_)
    }

    private fun AppCompatButton.setTab() {
        backgroundTintList = getColorTint(R.color.colorPrimaryDark)
        setTextColor(getMyColor(R.color.white))
        tag = getString(R.string.true_)
    }

    private fun setBgStyle() {
        val btnColors = findViewById<AppCompatButton>(R.id.btnColors)
        val btnGradients = findViewById<AppCompatButton>(R.id.btnGradients)
        val btnPatterns = findViewById<AppCompatButton>(R.id.btnPatterns)

        btnColors.setOnClickListener {
            btnColors.resetTab(btnGradients, btnPatterns)
            btnColors.setTab()
            initBgAdapter(LoadSticker.colorList)
        }
        btnGradients.setOnClickListener {
            btnColors.resetTab(btnGradients, btnPatterns)
            btnGradients.setTab()
            initBgAdapter(LoadSticker.gradientList)
        }
        btnPatterns.setOnClickListener {
            btnColors.resetTab(btnGradients, btnPatterns)
            btnPatterns.setTab()
            initBgAdapter(LoadSticker.effectList)
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

    private val pieces: MutableList<Bitmap> = ArrayList()
    private fun loadPhoto() {
        Log.e("atg**co", "$bitmapPaint")
        if (bitmapPaint == null) {
            return
        }
        pieces.clear()
        val count =
            if (bitmapPaint!!.size > puzzleLayout!!.areaCount) puzzleLayout!!.areaCount else bitmapPaint!!.size
        for (i in 0 until count) {
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    pieces.add(bitmap)
                    if (pieces.size == count) {
                        if (bitmapPaint?.size!! < puzzleLayout?.areaCount!!) {
                            for (i2 in 0 until puzzleLayout!!.areaCount) {
                                puzzleView?.addPiece(pieces[i2 % count])
                            }
                        } else {
                            puzzleView?.addPieces(pieces)
                        }
                    }
                    targets.remove(this)
                }

                override fun onBitmapFailed(errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            }
            if(bitmapPaint!![i]!=null) {
                try {
                    Picasso.with(this)
                        .load("file:///" + bitmapPaint!![i])
                        .resize(deviceWidth, deviceWidth)
                        .centerInside()
                        .config(Bitmap.Config.RGB_565)
                        .into(target)
                    targets.add(target)
                }
                catch(e: Exception)
                {
                    e.printStackTrace()
                }
            }

        }
    }

    fun init() {
        puzzleView?.setPuzzleLayout(puzzleLayout)
        puzzleView?.isTouchEnable = true
        puzzleView?.isNeedDrawLine = false
        puzzleView?.isNeedDrawOuterLine = false
        puzzleView?.lineSize = 2
        puzzleView?.lineColor = Color.BLACK
        puzzleView?.selectedLineColor = Color.BLACK
        puzzleView?.handleBarColor = Color.BLACK
        puzzleView?.setAnimateDuration(300)
        // currently the SlantPuzzleLayout do not support padding
        puzzleView?.piecePadding = 20f
        puzzleView?.post { loadPhoto() }
    }


    private fun gotoNextTask() {
        openActivity<CollageFilesActivity>()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val paths: List<String>? = data!!.getStringArrayListExtra(Define.PATHS)
            val path = paths!![0]
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    puzzleView!!.replace(bitmap)
                }

                override fun onBitmapFailed(errorDrawable: Drawable?) {
                    showSnackBar(puzzleView!!, "Replace Failed!")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            }
            Picasso.with(this)
                .load("file:///$path")
                .resize(deviceWidth, deviceWidth)
                .centerInside()
                .config(Bitmap.Config.RGB_565)
                .into(target)
        }
    }

    override fun ImageTagData(path: String) {
    }

    override fun SingleImageTagData(path: String) {

    }
}