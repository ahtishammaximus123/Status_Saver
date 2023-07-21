package com.example.stickers.Activities.newDashboard.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stickers.R
import com.example.stickers.ads.afterDelay
import com.example.stickers.dialog.ProgressDialog
import com.google.android.material.tabs.TabLayout

abstract class BaseLiveStatusFragment : Fragment(), TabLayout.OnTabSelectedListener {

    var progressDialog: ProgressDialog? = null

    fun showDialog() {
//        activity?.runOnUiThread {
//            dismissDialog()
//            progressDialog = ProgressDialog(requireActivity())
//            progressDialog?.show()
//        }
    }

    fun dismissDialog() {

        activity?.runOnUiThread {
            progressDialog?.apply {
                afterDelay(1000) {
                    if (isShowing)
                        dismiss()
                }
            }
        }
    }

    var tabLayoutNav: TabLayout? = null
    fun selectTabCustom(index: Int) {
        val tab: TabLayout.Tab? = tabLayoutNav?.getTabAt(index)
        tabLayoutNav?.removeOnTabSelectedListener(this)
        tab?.select()
        tabLayoutNav?.addOnTabSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        tabLayoutNav?.addOnTabSelectedListener(this)
    }

    override fun onPause() {
        super.onPause()
        tabLayoutNav?.removeOnTabSelectedListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayoutNav?.removeOnTabSelectedListener(this)
        tabLayoutNav?.addOnTabSelectedListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        /*when (tab?.text) {
            resources.getString(R.string.live_images) -> {
                when (findNavController().currentDestination?.id) {
                    R.id.LiveVideosFragment -> findNavController().navigate(R.id.action_LiveVideosFragment_to_LiveImagesFragment)
                    R.id.SavedStatusesFragment -> findNavController().navigate(R.id.action_SavedStatusesFragment_to_LiveImagesFragment)
                    else -> findNavController().navigate(R.id.LiveImagesFragment)
                }
            }
            resources.getString(R.string.live_videos) -> {
                when (findNavController().currentDestination?.id) {
                    R.id.LiveImagesFragment -> findNavController().navigate(R.id.action_LiveImagesFragment_to_LiveVideosFragment)
                    R.id.SavedStatusesFragment -> findNavController().navigate(R.id.action_SavedStatusesFragment_to_LiveVideosFragment)
                    else -> findNavController().navigate(R.id.LiveVideosFragment)
                }

            }
            resources.getString(R.string.saved_status) -> {
                when (findNavController().currentDestination?.id) {
                    R.id.LiveImagesFragment -> findNavController().navigate(R.id.action_LiveImagesFragment_to_SavedStatusesFragment)
                    R.id.LiveVideosFragment -> findNavController().navigate(R.id.action_LiveVideosFragment_to_SavedStatusesFragment)
                    else -> findNavController().navigate(R.id.SavedStatusesFragment)
                }

            }
        }*/

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}