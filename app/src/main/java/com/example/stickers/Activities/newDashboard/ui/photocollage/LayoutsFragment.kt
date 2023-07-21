package com.example.stickers.Activities.newDashboard.ui.photocollage

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stickers.Activities.PhotoCollage.PuzzleAdapter
import com.example.stickers.Activities.PhotoCollage.PuzzleUtils
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.R
import com.example.stickers.app.AppClass
import com.example.stickers.databinding.PhotoCollageLayoutsBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.xiaopo.flying.puzzle.PuzzleLayout
import com.xiaopo.flying.puzzle.PuzzleView


class LayoutsFragment : Fragment() {

    private var _binding: PhotoCollageLayoutsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var puzzleLayout: PuzzleLayout? = null
    private var bitmapPaint: List<String>? = null
    private var puzzleView: PuzzleView? = null
    private val targets: MutableList<Target> = ArrayList()
    private var deviceWidth = 0
//    var type =0
//    var pieceSize =0
//    var themeId = 0
    private val collageViewModel: PhotoCollageViewModel by activityViewModels() {
        PhotoCollageViewModelFactory((activity?.application as AppClass).photosRep)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotoCollageLayoutsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
//    fun init(){
//        puzzleLayout = type?.let { pieceSize?.let { it1 ->
//            themeId?.let { it2 ->
//                PuzzleUtils.getPuzzleLayout(it,
//                    it1, it2
//                )
//            }
//        } }
//        puzzleView = _binding?.puzzleView
//        puzzleView?.setPuzzleLayout(puzzleLayout)
//        puzzleView?.setTouchEnable(true)
//        puzzleView?.setNeedDrawLine(false)
//        puzzleView?.setNeedDrawOuterLine(false)
//        puzzleView?.setLineSize(2)
//        puzzleView?.setLineColor(Color.BLACK)
//        puzzleView?.setSelectedLineColor(Color.BLACK)
//        puzzleView?.setHandleBarColor(Color.BLACK)
//        puzzleView?.setAnimateDuration(300)
//        puzzleView?.setOnPieceSelectedListener(PuzzleView.OnPieceSelectedListener { piece, position ->
//           // isSelected = true
//        })
//
//        // currently the SlantPuzzleLayout do not support padding
//        puzzleView?.setPiecePadding(20f)
//        puzzleView!!.post { loadPhoto() }
//        val puzzleList = binding.puzzleList
//        puzzleList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
//        val puzzleAdapter = PuzzleAdapter(1)
//        puzzleAdapter.setOnItemClickListener { puzzleLayout, themeId ->
//            puzzleView?.setPuzzleLayout(puzzleLayout)
//        }
//        puzzleList.adapter = puzzleAdapter
//        puzzleAdapter.refreshData(PuzzleUtils.getAllPuzzleLayouts(), null)
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        deviceWidth = resources.displayMetrics.widthPixels
//
//        collageViewModel.bitmapPaint.observe(viewLifecycleOwner, Observer {
//            type = collageViewModel.type.value!!
//            pieceSize = collageViewModel.pieceSize.value!!
//            //themeId = collageViewModel.themeId.value!!
//            bitmapPaint = collageViewModel.bitmapPaint.value
//            init()
//        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadPhoto() {
        if (bitmapPaint == null) {
            return
        }
        val pieces: MutableList<Bitmap> = ArrayList()
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
            Picasso.with(activity)
                .load("file:///" + bitmapPaint!![i])
                .resize(deviceWidth, deviceWidth)
                .centerInside()
                .config(Bitmap.Config.RGB_565)
                .into(target)
            targets.add(target)
        }
    }
}