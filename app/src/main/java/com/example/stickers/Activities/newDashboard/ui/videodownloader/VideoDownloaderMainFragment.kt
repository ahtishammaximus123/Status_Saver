package com.example.stickers.Activities.newDashboard.ui.videodownloader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stickers.Activities.newDashboard.StatusFragModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.savedstatuses.SavedStatusesFragment
import com.example.stickers.Activities.newDashboard.ui.videos.VideosFragment
import com.example.stickers.R
import com.example.stickers.databinding.FragmentStatusMainBinding
import com.example.stickers.databinding.FragmentVideoDownloaderMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class VideoDownloaderMainFragment : Fragment() {
    companion object {
        lateinit var changePositionListener:()-> Unit

    }
    private lateinit var binding: FragmentVideoDownloaderMainBinding

    private val fragmentList = arrayListOf<StatusFragModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVideoDownloaderMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentList.clear()
        fragmentList.add(StatusFragModel("Paste URL", PasteUrlFragment()))
        fragmentList.add(StatusFragModel("Downloads", DownloadsFragment()))

        changePositionListener={
            binding.viewPager.currentItem = 1
        }

        val adapter = DownloaderPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter


        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabLayout.tabLayoutNav, binding.viewPager) { tab, position ->
            tab.text = fragmentList[position].name
        }.attach()
    }

    inner class DownloaderPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position].fragment
        }
    }

}