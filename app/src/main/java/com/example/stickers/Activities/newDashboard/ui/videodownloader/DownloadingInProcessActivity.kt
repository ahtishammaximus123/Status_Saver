package com.example.stickers.Activities.newDashboard.ui.videodownloader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.downloadInProcessActivityBack
import com.example.stickers.Activities.newDashboard.ui.videodownloader.PasteUrlFragment.Companion.errorDownloading
import com.example.stickers.Activities.newDashboard.ui.videodownloader.PasteUrlFragment.Companion.videoAlready
import com.example.stickers.BuildConfig
import com.example.stickers.R
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.databinding.ActivityDownloadingInProcessBinding
import com.example.stickers.dialog.ProgressDialog
import com.example.stickers.dialog.ShareFragment
import java.io.File

class DownloadingInProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadingInProcessBinding
    private var adisready = "notshowed"
    private var isActivityRunning: Boolean = false

    var loadingDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadingInProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed(
            { binding.backPress.visibility = View.VISIBLE },
            2000
        )
        errorDownloading = { errorTitle, error ->
            binding.videoAlready.text = error
            binding.videoAlreadyTitle.text = errorTitle
            binding.videoAlready.visibility = View.VISIBLE
            binding.videoAlreadyTitle.visibility = View.VISIBLE
            binding.goToDownloads.visibility = View.GONE
            binding.downloaded.visibility = View.INVISIBLE
            binding.dramaLayout.visibility = View.INVISIBLE

        }

        videoAlready = {
            binding.videoAlready.visibility = View.VISIBLE
            binding.videoAlreadyTitle.visibility = View.VISIBLE
            binding.goToDownloads.visibility = View.VISIBLE
            binding.downloaded.visibility = View.INVISIBLE
            binding.dramaLayout.visibility = View.INVISIBLE
        }
        binding.goToDownloads.setOnClickListener {
            onBackPressed()

        }
        binding.backPress.setOnClickListener {
            onBackPressed()

        }


        DownloadService.updated.observe(this, Observer { download ->
            if (download != null) {
                if (download.progress in 1..45) {
                    binding.dramaText.text = "Processing..."
                } else if (download.progress in 50..90) {
                    binding.dramaText.text = "Downloading..."
                    binding.textView14.text = "Please be patient we are downloading your video."
                } else if (download.progress == 100) {
                    binding.downloaded.visibility = View.VISIBLE
                    binding.dramaLayout.visibility = View.INVISIBLE
                    DownloadService.updated.postValue(null)
                    DownloadService.canceled.postValue(null)
                    DownloadService.completed.postValue(null)

                }

            }
        })



        binding.downloaded.setOnClickListener {
//            NativeAdmobClass.downloadBtnNativeAd?.destroy()
//            NativeAdmobClass.downloadBtnNativeAd=null
            val directoryName = "Download/videodownloader"
            val latestVideo = getLatestVideoFromInternalStorage(this, directoryName)

            if (latestVideo != null) {
                val latestVideoPath = latestVideo.absolutePath
                startActivity(
                    Intent(
                        applicationContext,
                        PlayerActivity::class.java
                    ).putExtra("url", latestVideoPath.toString()).putExtra("id", " w.name")
                )
                this.finish()

            }
        }
        binding.share.setOnClickListener {
            val directoryName = "Download/videodownloader"
            val latestVideo = getLatestVideoFromInternalStorage(this, directoryName)
            if (latestVideo != null) {
                shareVideoWithOtherApps(this, latestVideo)
            } else {
                println("No video files found in the directory.")
            }

        }
        Handler(Looper.getMainLooper()).postDelayed(
            { binding.backPress.visibility = View.VISIBLE },
            3000
        )
        binding.backPress.setOnClickListener {
            onBackPressed()
        }
        binding.downloaded.visibility = View.INVISIBLE
        binding.dramaLayout.visibility = View.VISIBLE

    }


    override fun onBackPressed() {

        downloadInProcessActivityBack = true
        MainDashActivity.playerActivityBack = true
        this.finish()
        this.overridePendingTransition(0, 0)


    }

    override fun onResume() {
        super.onResume()
        isActivityRunning = true

        loadNativeAd(this,
            binding.downloadInProcessNative,
            RemoteDateConfig.remoteAdSettings.admob_native_videos_downloaded_native_ad.value,
            this.layoutInflater,
            R.layout.gnt_medium_template_view,
            { },
            {})


    }

    override fun onPause() {
        super.onPause()
        isActivityRunning = false
    }

    fun shareVideoWithOtherApps(context: Context, videoFile: File) {
        val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}", videoFile)
        var shareFragment: ShareFragment? = null
        shareFragment = ShareFragment(uri, {}, {})
        shareFragment.show(supportFragmentManager, "exit_dialog_tag")

    }

    private fun getLatestVideoFromInternalStorage(context: Context, directoryName: String): File? {
        val internalStorageDir = context.getExternalFilesDir(null)
        val directory = File(internalStorageDir, directoryName)
        if (!directory.exists() || !directory.isDirectory) {

            return null
        }
        val videoFiles = directory.listFiles { file ->
            file.isFile && file.extension.matches(Regex("mp4", RegexOption.IGNORE_CASE))
        }

        if (videoFiles.isNullOrEmpty()) {
            return null
        }
        videoFiles.sortWith(Comparator { file1, file2 ->
            (file2.lastModified() - file1.lastModified()).toInt()
        })

        return videoFiles[0]
    }

    // Usage

}