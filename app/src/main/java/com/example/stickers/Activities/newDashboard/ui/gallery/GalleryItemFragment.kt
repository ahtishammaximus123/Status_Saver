package com.example.stickers.Activities.newDashboard.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stickers.Activities.PhotoCollage.CollageProcessActivity
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.ImageGalleryCallBack
import com.example.stickers.R
import com.example.stickers.app.AppClass
import com.example.stickers.databinding.FragmentGalleryItemListBinding
import com.xiaopo.flying.puzzle.slant.SlantPuzzleLayout
import java.io.File

/**
 * A fragment representing a list of Items.
 */
class GalleryItemFragment : Fragment(), ImageGalleryCallBack {

    private var _binding: FragmentGalleryItemListBinding? = null
    var files : ArrayList<RadioFile> = ArrayList()
    var selectedfiles : ArrayList<String> = ArrayList()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var adapter: GalleryItemRecyclerViewAdapter? = null

    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imagesViewModel.loadImages()
        _binding = FragmentGalleryItemListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set the adapter
        adapter = GalleryItemRecyclerViewAdapter(this)
        with(_binding?.list) {
                this?.layoutManager = GridLayoutManager(context, 2)
                this?.adapter = adapter
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val menuHost: MenuHost = requireHost() as MenuHost
//        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

       // _binding?.appBarLayout.hom
        _binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        _binding?.done?.setOnClickListener {


            val intent = Intent(
                activity,
                CollageProcessActivity::class.java
            )
            intent.putStringArrayListExtra("photo_path", selectedfiles)
//            if (puzzleLayout is SlantPuzzleLayout) {
//                intent.putExtra("type", 0)
//            } else {
                intent.putExtra("type", 1)
           // }
            intent.putExtra("piece_size", selectedfiles.size)
            //intent.putExtra("theme_id", themeId)

            startActivity(intent)
        }


        imagesViewModel.photos.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            if(it.isEmpty()){
                _binding?.done?.visibility = View.GONE
                _binding?.textView4?.visibility = View.VISIBLE
            }else {
                it.forEach { file ->
                    files.add(RadioFile(file, false))
                }
                adapter?.submitList(files)
            }
        })

    }

    override fun onImageViewClicked(file: String?, tag: Int) {
        if (file != null) {
            if(selectedfiles.contains(file)) {
                selectedfiles.remove(file)
                files.firstOrNull { t->t.file.absolutePath == file }?.selected = false
            }
                else {
                selectedfiles.add(file)
                files.firstOrNull { t->t.file.absolutePath == file }?.selected = true
            }
        }

        adapter?.submitList(files)
    }
//
//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.done_menu, menu)
//    }
//
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        // Handle the menu selection
//        return when (menuItem.itemId) {
//            R.id.action_done -> {
//                // Do stuff...
//
//                true
//            }
//            else -> false
//        }
//    }


}