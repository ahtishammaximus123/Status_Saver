package com.example.stickers.Activities.newDashboard.ui.videodownloader

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ParseException
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.stickers.Activities.newDashboard.MainDashActivity.Companion.downloadInProcessActivityBack
import com.example.stickers.Activities.newDashboard.ui.videodownloader.VideoDownloaderMainFragment.Companion.changePositionListener
import com.example.stickers.R
import com.example.stickers.ads.loadNativeAd
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.databinding.FragmentPasteUrlBinding
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit

class PasteUrlFragment : Fragment() {

    private var retrofit: Retrofit? = null
    private var retrofitLink: Retrofit? = null
    private var adisready = "notshowed"
    private var splashadisready = "notshowed"
    private var isActivityRunning: Boolean = false
    private var baseUrlAnything = ""
    private var baseUrlLink = ""
    private lateinit var binding: FragmentPasteUrlBinding

    companion object {
        var downloadListener: (() -> Unit)? = null
        var workAfterAd: (() -> Unit)? = null
        var videoAlready: (() -> Unit)? = null
        var errorDownloading: ((errorTitle: String, error: String) -> Unit)? = null
        private var clicked: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasteUrlBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if ( RemoteDateConfig.remoteAdSettings.base_url_anything.value.isNotEmpty()) {
            baseUrlAnything =  RemoteDateConfig.remoteAdSettings.base_url_anything.value
        } else {
            baseUrlAnything = "https://downloader.thinkshot.site/"
        }
        if ( RemoteDateConfig.remoteAdSettings.base_url_link.value.isNotEmpty()) {
            baseUrlLink =  RemoteDateConfig.remoteAdSettings.base_url_link.value
        } else {
            baseUrlLink = "https://downloader.thinkshot.site/"
        }

//        baseUrlAnything = "https://mocki.io/v1/"
//        baseUrlLink = "https://mocki.io/v1/"
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(80.toLong(), TimeUnit.SECONDS)
            .readTimeout(80.toLong(), TimeUnit.SECONDS)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrlAnything)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitLink = Retrofit.Builder()
            .baseUrl(baseUrlLink)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        binding.button.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextTextPersonName.windowToken, 0)
            val url = binding.editTextTextPersonName.text.toString()
            if (URLUtil.isValidUrl(url)) {
                binding.button.visibility = View.INVISIBLE
                binding.loadingVid.visibility = View.VISIBLE

                    processVideo(url) {
                        binding.editTextTextPersonName.setText("")
                        binding.button.visibility = View.VISIBLE
                        binding.loadingVid.visibility = View.GONE

                    }

                    startActivity(Intent(requireActivity(),DownloadingInProcessActivity::class.java))

            } else Toast.makeText(requireActivity(), "Not valid video URL.", Toast.LENGTH_SHORT)
                .show()
        }




        binding.editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0) {
                    binding.button.setBackgroundResource(R.drawable.btn_back_green)
                    binding.hint.visibility = View.GONE
                    binding.clearText.visibility = View.VISIBLE
                    binding.button.isClickable = true
                    binding.button.isEnabled = true
                    binding.paste.setBackgroundResource(R.drawable.background_light)

                    YoYo.with(Techniques.Shake)
                        .duration(800)
                        .repeat(1)
                        .playOn(binding.button)

                } else {
                    binding.hint.visibility = View.VISIBLE
                    binding.button.setBackgroundResource(R.drawable.background_light)
                    binding.paste.setBackgroundResource(R.drawable.btn_back_green)
                    binding.clearText.visibility = View.GONE
                    binding.button.isClickable = false
                    binding.button.isEnabled = false
                }
            }
        })
//        DownloadService.updated.observe(this, Observer { download ->
////            if (download != null) {
////                updateUI(download)
////            }
//        })

        binding.clearText.setOnClickListener {
            binding.editTextTextPersonName.setText("")
        }

        binding.paste.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextTextPersonName.windowToken, 0)
            val clipboard =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && clipboard.primaryClipDescription!!.hasMimeType(
                    ClipDescription.MIMETYPE_TEXT_PLAIN
                )
            ) {
                // The clipboard contains text data
                val item = clipboard.primaryClip!!.getItemAt(0)
                val text = item.text.toString().trim()
                binding.editTextTextPersonName.setText(text)


                binding.editTextTextPersonName.text?.let { it1 ->
                    binding.editTextTextPersonName.setSelection(
                        it1.length
                    )
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        isActivityRunning = true

        if(downloadInProcessActivityBack)
        {
            downloadInProcessActivityBack=false
            changePositionListener.invoke()

        }
        loadNativeAd(requireActivity(),binding.downloadlinkNative,
            RemoteDateConfig.remoteAdSettings.admob_native_download_link_native_ad.value,requireActivity().layoutInflater,R.layout.gnt_medium_template_view,{ },{})
    }


    override fun onPause() {
        super.onPause()
        isActivityRunning = false
    }

    private fun fetchCopyTextFun() {
        val clipboard: ClipboardManager? =
            requireActivity()?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
        clipboard?.clipboardText()
    }

    private fun ClipboardManager.clipboardText() {
        primaryClip?.apply {
            if (itemCount > 0)
                getItemAt(0)?.text?.let {
                    if (Patterns.WEB_URL.matcher(it.toString()).matches()) {
//                        if (StorageSharedPref.get("clipboard") != it.toString()) {
//                            StorageSharedPref.save("clipboard", it.toString())
//                            if (it.toString().isNotEmpty())
//                                binding?.editTextTextPersonName?.setText(it.toString().trim())
////                                binding?.btnDownloadConstraint?.performClick()
//                            //   Toast.makeText(this@DownloadLinkActivity,"Pasted",Toast.LENGTH_LONG).show()
////                            YoYo.with(Techniques.Shake)
////                                .duration(800)
////                                .repeat(1)
////                                .playOn(binding.button)
//                        } else {
////                            YoYo.with(Techniques.Shake)
////                                .duration(800)
////                                .repeat(1)
////                                .playOn(binding.button)
//                            //      Toast.makeText(this@DownloadLinkActivity,"No Pasted",Toast.LENGTH_LONG).show()
//                        }
                    }
                }
        }
    }

    fun processVideo(vidData: String?, listenerFromProcessVid: () -> Unit) {
        try {
            if (vidData?.contains("https://www.youtube.com/") == true || vidData?.contains("https://youtu.be/") == true || vidData?.contains(
                    "https://youtube.com/"
                ) == true
            ) {

                errorDownloading?.invoke(
                    "Restricted Content",
                    "Download videos from youtube is currently not supported in \"Video Downloader\""
                )
                Toast.makeText(requireActivity(), "Cannot process this Url.", Toast.LENGTH_SHORT)
                    .show()
                listenerFromProcessVid.invoke()

            } else {
                lifecycleScope.launch {
                    Log.e("testing", "By pass youtube check : ")
                    getDataFromNetwork(vidData!!) {
                        listenerFromProcessVid.invoke()
                    }

                }

            }

        } catch (e: Exception) {
        }
    }

    private fun getDataFromNetwork(url1: String, listenerFromProcess: () -> Unit) {
        if (url1.contains("facebook") || url1.contains("fb.watch") || url1.contains("dailymotion")) {
            Log.e("testing", "getAnythingResponse")
            getAnythingResponse(url1) {
                listenerFromProcess.invoke()
            }

        } else {
            Log.e("testing", "getAnythingResponse2")
            getAnythingResponse(url1) {
                listenerFromProcess.invoke()
            }
        }

    }

    private fun getAnythingResponse(url1: String, listenerFrom: () -> Unit) {
        try {
            val apiService = retrofit?.create(ApiService::class.java)
            val call = apiService?.postData(url1, "1234")

            call?.enqueue(object : Callback<List<VideoData>> {
                override fun onResponse(
                    call: Call<List<VideoData>>,
                    response: Response<List<VideoData>>
                ) {
                    if (response.isSuccessful) {

                        Log.e("testing", "isSuccessful: $response")
                        val videoDataList = response.body()
                        if (videoDataList != null && videoDataList.isNotEmpty()) {
                            val videoData = videoDataList[0]
                            val src = videoData.quality
                            val urlSd = videoData.url
                            if (videoDataList.size > 1) {

                                val videoDataHd = videoDataList[1]
                                val srchd = videoDataHd.quality
                                val urlHd = videoDataHd.url
                                listenerFrom.invoke()
                                Log.e("testing", "Starting Download ")
                                startDownload(urlSd, url1)
                            }

                        } else {
                            // dialogDown?.dismissAllowingStateLoss()
                            listenerFrom.invoke()
                            Toast.makeText(
                                requireActivity(),
                                "Video data not found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        //if didit get responce
                    }
                }

                override fun onFailure(call: Call<List<VideoData>>, t: Throwable) {
                    Log.e("testing", "onFailure: $t")
                    //  dialogDown?.dismiss()

                    getLinkResponse(url1) { listenerFrom.invoke() }
                    Toast.makeText(
                        requireActivity(),
                        "Hold on we are fetching Link",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (ex: IOException) {
            listenerFrom.invoke()
            Toast.makeText(
                requireActivity(),
                "Slow Network! Try Downloading on a faster network.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getLinkResponse(url1: String, listenerFrom: () -> Unit) {
        try {
            val apiService = retrofitLink?.create(LinkApiService::class.java)

            val call = apiService?.postData(url1, 0)
            call?.enqueue(object : Callback<LinkVideoData> {
                override fun onResponse(
                    call: Call<LinkVideoData>,
                    response: Response<LinkVideoData>
                ) {
                    if (response.isSuccessful) {
                        Log.e("testing", "isSuccessful: $response")
                        val videoData = response.body()

                        if (videoData != null) {

                            if (videoData.formats.size > 1) {

                                listenerFrom.invoke()
                                startDownload(videoData.formats[1].url, url1)
                            }


                        } else {
                            listenerFrom.invoke()
                            Toast.makeText(
                                requireActivity(),
                                "Video data not found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle unsuccessful response here
                    }
                }

                override fun onFailure(call: Call<LinkVideoData>, t: Throwable) {
                    listenerFrom.invoke()
                    Log.e("testing", "onFailure: $t")
                    errorDownloading?.invoke("Error!", "Video Not Available...")
                    Toast.makeText(
                        requireActivity(),
                        "Can't Process link this time",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
        } catch (ex: IOException) {
            listenerFrom.invoke()
            errorDownloading?.invoke("Error!", "Video Not Available...")
            Toast.makeText(
                requireActivity(),
                "Slow Network! Try Downloading on a faster network.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun startDownload(url: String?, nameUrl: String) {

        if (url != null && url != "" && url != "null") {
            try {
                val folder = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val mBaseFolderPath: String = folder.toString() + "/" + "videodownloader"
                if (!File(mBaseFolderPath).exists()) {
                    File(mBaseFolderPath).mkdir()
                }
                val filePath: String = folder.toString() + "/videodownloader/" + getRandomFileName("mp4")

                val request = Request(url, filePath)
                request.priority = Priority.HIGH
                request.networkType = NetworkType.ALL
                request.tag = nameUrl
                request.groupId = groupId

                // val it  = StorageVideos.getVids()

                val exists = DownloadService.downloadsList?.find { s -> s.tag == nameUrl }
                if (exists == null) {
                    if (DownloadService.rxFetch?.isClosed == true || DownloadService.rxFetch == null) {
                        requireActivity().stopService(
                            Intent(
                                requireActivity(),
                                DownloadService::class.java
                            )
                        )
                        requireActivity().startService(
                            Intent(
                                requireActivity(),
                                DownloadService::class.java
                            )
                        )
                    }
                    Toast.makeText(requireActivity(), "Starting Download", Toast.LENGTH_LONG).show()
                    DownloadService.rxFetch?.enqueue(request, {
                        DownloadService.vidCount++
                        DownloadService.videoDownloadStartedLiveData.postValue("1")
                    })
                    {
                        Log.e("downloadUrl", "Error: ${it}")
                        //   Toast.makeText(requireActivity(), "Video Cant Be Downloaded!", Toast.LENGTH_LONG).show()
                    }
                } else {

                    Toast.makeText(
                        requireActivity(),
                        "Video Already downloaded!",
                        Toast.LENGTH_LONG
                    ).show()
                    videoAlready?.invoke()

                }

            } catch (ex: IllegalArgumentException) {
                Toast.makeText(requireActivity(), "Not Available! " + ex.message, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: URISyntaxException) {
                Toast.makeText(requireActivity(), "Not Available! " + ex.message, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: ParseException) {
                Toast.makeText(requireActivity(), "Not Available! " + ex.message, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: Exception) {
                Toast.makeText(requireActivity(), "Not Available! " + ex.message, Toast.LENGTH_LONG)
                    .show()
            }
        }


    }

    fun getRandomFileName(fileExtension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val randomString = (1..6)
            .map { kotlin.random.Random.nextInt(0, 36) }
            .map { if (it < 10) it.toString() else ('a' + it - 10).toString() }
            .joinToString("")

        return "vid_downloader_$timestamp$randomString.$fileExtension"
    }
}