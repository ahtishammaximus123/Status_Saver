package com.example.stickers.Activities.sticker

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stickers.R
import com.example.stickers.databinding.FragmentStickerMakerBinding

class StickerMakerFragment : Fragment() {

    private var _binding: FragmentStickerMakerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStickerMakerBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        val openWhat = requireActivity().findViewById<ImageView>(R.id.open_whatsApp_icon)
        openWhat.visibility=View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            btnAddNewPack.setOnClickListener {
//                findNavController().navigate(R.id.action_stickerMakerFragment_to_liveStatusFragment)
//                findNavController().navigate(R.id.action_stickerMakerFragment_to_stickerActivity)
//                findNavController().navigate(R.id.action_stickerMakerFragment_to_galleryFragment)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.folder)
        item.isVisible = true

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}