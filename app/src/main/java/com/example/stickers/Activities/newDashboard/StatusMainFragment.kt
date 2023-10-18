package com.example.stickers.Activities.newDashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stickers.Activities.newDashboard.ui.images.ImagesFragment
import com.example.stickers.Activities.newDashboard.ui.savedstatuses.SavedStatusesFragment
import com.example.stickers.Activities.newDashboard.ui.videodownloader.VideoDownloaderMainFragment
import com.example.stickers.Activities.newDashboard.ui.videos.VideosFragment
import com.example.stickers.R
import com.example.stickers.databinding.FragmentStatusMainBinding
import com.google.android.material.tabs.TabLayoutMediator

data class StatusFragModel(val name: String, val fragment: Fragment)
class StatusMainFragment : Fragment() {
    private lateinit var binding: FragmentStatusMainBinding

    private val fragmentList = arrayListOf<StatusFragModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStatusMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentList.clear()
        fragmentList.add(StatusFragModel("Live Images", ImagesFragment()))
        fragmentList.add(StatusFragModel("Live Videos", VideosFragment()))
        fragmentList.add(StatusFragModel("Saved", SavedStatusesFragment()))


        val adapter = StatusPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter



        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabLayout.tabLayoutNav, binding.viewPager) { tab, position ->
            tab.text = fragmentList[position].name
        }.attach()

    }

    inner class StatusPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position].fragment
        }
    }
}