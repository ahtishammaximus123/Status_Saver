package com.example.stickers.Activities.newDashboard.ui.videodownloader

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.playerActivityBack
import com.example.stickers.R
import com.example.stickers.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch
import java.io.File

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
   // private var loadingDialog: LoadingDialog?=null
    var videoUrl : String? = null
    var player : SimpleExoPlayer? = null
    private val viewModel2: DownloadedFilesViewModel by viewModels()
    var id :String?= null
    var reverseList : List<Download?>? = ArrayList()
    var loaded : MutableLiveData<Boolean> = MutableLiveData()
    var service : String? = null
    var s = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel2.init(applicationContext)
        service = intent.extras?.getString("service")
        videoUrl = intent.extras?.getString("url")
        id = intent.extras?.getString("id")

        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.setShowNextButton(false)
        binding.playerView.setShowPreviousButton(false)

        binding.playerView.setShowRewindButton(false)
        binding.playerView.setShowFastForwardButton(false)
        binding.playerView.player = player


      //  loadingDialog?.show()
        if(service == "1") {
            s = 1

            service = "0"
            init()
            val mediaItem: com.google.android.exoplayer2.MediaItem? = videoUrl?.let {
                com.google.android.exoplayer2.MediaItem.fromUri(
                    it
                )
            }
            if (mediaItem != null) {
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true

            }
        }
        else init()

    }
    fun init(){
        if(videoUrl == null) finish()

        if(id != null) {
            lifecycleScope.launch {
                val folder =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath.toString() + "/" + "videodownloader")
                val downloads = viewModel2.getFiles(folder)
                reverseList = downloads?.reversed()
                loaded.postValue(true)

            }
            loaded.observe(this, Observer { t ->
                if (t) {

                    reverseList?.forEach {
                        if (it?.path != videoUrl) {
                            val mediaItem: MediaItem? =
                                it?.path?.let { itt ->
                                    MediaItem.fromUri(
                                        itt
                                    )
                                }
                            if (mediaItem != null) {
                                player?.addMediaItem(mediaItem)
                            }
                        }
                    }
                    if (reverseList != null) {
                        if (reverseList!!.count() > 1) {
                            if (id != null) {

                                binding.playerView.setShowPreviousButton(true)

                                binding.playerView.setShowNextButton(true)

                            }

                        }
                    }

                    player?.prepare()
                    player?.playWhenReady = true

                }
            })
        }


    }
    override fun onResume() {
        if(service != "1") {

            if (player == null) {
                player = SimpleExoPlayer.Builder(this).build()
                binding.playerView.setShowNextButton(false)
                binding.playerView.setShowPreviousButton(false)

                binding.playerView.setShowRewindButton(false)
                binding.playerView.setShowFastForwardButton(false)
                binding.playerView.player = player
                init()
            }

            val mediaItem: com.google.android.exoplayer2.MediaItem? = videoUrl?.let {
                com.google.android.exoplayer2.MediaItem.fromUri(
                    it
                )
            }
            if (mediaItem != null) {
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true


            }

        }
        super.onResume()
        player?.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {

         //           loadingDialog?.dismiss()

                }
                if (state == Player.STATE_ENDED) {

                }
            }

            override fun onPlayerError(error: PlaybackException) {

            }


        })
    }


    override fun onPause() {
        player?.pause()
        player?.release()
        player = null
        super.onPause()
    }
    override fun onBackPressed() {
        player?.pause()
        player?.release()
        player = null
        playerActivityBack=true

        this.finish()


    }

}