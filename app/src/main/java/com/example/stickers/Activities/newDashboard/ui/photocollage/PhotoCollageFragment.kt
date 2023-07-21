package com.example.stickers.Activities.newDashboard.ui.photocollage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stickers.Activities.newDashboard.base.BaseLiveStatusFragment
import com.example.stickers.R
import com.example.stickers.databinding.FragmentPhotoCollageBinding

class PhotoCollageFragment : Fragment() {

    private var _binding: FragmentPhotoCollageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPhotoCollageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.btnSelectPhotos?.setOnClickListener {
            findNavController().navigate(R.id.action_photoCollageFragment_to_galleryItemFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.folder)
        item.isVisible = true

        val menuWa: MenuItem = menu.findItem(R.id.action_wa)
        val menuBa: MenuItem = menu.findItem(R.id.action_ba)
        menuWa.isVisible = false
        menuBa.isVisible = false
    }

}