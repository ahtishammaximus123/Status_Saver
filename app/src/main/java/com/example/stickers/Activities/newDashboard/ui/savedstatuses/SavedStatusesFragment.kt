package com.example.stickers.Activities.newDashboard.ui.savedstatuses


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.stickers.Activities.FullScreenImageActivity.Companion.savedStatusListFiles29
import com.example.stickers.Activities.FullScreenImageActivity.Companion.savedVideoStatusListFiles29
import com.example.stickers.Activities.SplashActivity
import com.example.stickers.Activities.newDashboard.MainDashActivity
import com.example.stickers.Activities.newDashboard.base.BaseLiveStatusFragment
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModel
import com.example.stickers.Activities.newDashboard.ui.images.ImagesViewModelFactory
import com.example.stickers.Activities.repositories.Coroutines
import com.example.stickers.Adapter.SavedStatusAdapter
import com.example.stickers.DeletedCallback
import com.example.stickers.Models.Status
import com.example.stickers.Models.StatusesHeaders
import com.example.stickers.R
import com.example.stickers.SavedMultiSelectCallback
import com.example.stickers.Utils.Common
import com.example.stickers.Utils.WAoptions
import com.example.stickers.app.AppClass
import com.example.stickers.app.AppClass.Companion.file30List
import com.example.stickers.app.AppClass.Companion.fileList
import com.example.stickers.databinding.FragmentSavedStatusesBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SavedStatusesFragment : BaseLiveStatusFragment(), DeletedCallback,SavedMultiSelectCallback {

    private var _binding: FragmentSavedStatusesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private val savedFilesList: ArrayList<Status> = ArrayList()
    private val savedVideoFilesList: ArrayList<Status> = ArrayList()
    private val savedFilesWithHeadersList: ArrayList<StatusesHeaders> = ArrayList()
    private val handler = Handler()
    private var filesAdapter: SavedStatusAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var messageTextView: TextView? = null
    private var imgNoFound: ImageView? = null
    private val s: Status? = null
    private var countIsSaved = 0
    private var loadImages =true
    var anyImages = MutableLiveData<Int>()

    //private val imagesViewModel: ImagesViewModel by activityViewModels()
    private val imagesViewModel: ImagesViewModel by activityViewModels() {
        ImagesViewModelFactory((activity?.application as AppClass).photosRep)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val savedStatusesViewModel =
            ViewModelProvider(this)[SavedStatusesViewModel::class.java]

        _binding = FragmentSavedStatusesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tabLayoutNav = binding.tabLayout.tabLayoutNav
        selectTabCustom(2)
        SplashActivity.fbAnalytics?.sendEvent("SavedStatusActy_Open")

        return root
    }

    private fun showHowToUse() {
        val dialog2 = activity?.let {
            Dialog(
                it,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
        }

        dialog2?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2?.setCancelable(false)
        dialog2?.setContentView(R.layout.dialog_open_whatsapp_actual)
        dialog2?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val okButton = dialog2?.findViewById<ImageView>(R.id.open_whatsApp_img)
        val cancelDialog = dialog2?.findViewById<ImageView>(R.id.close_open_whatsapp_dialog)
        val etxt = dialog2?.findViewById<TextView>(R.id.open_text)
        val i = activity?.packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
        if (i != null&&i.`package`=="com.whatsapp") {
            Log.e("showHowToUse", "showHowToUse: ${i.`package`}", )
            etxt?.setText("WhatsApp")
        }
        else if (i != null&&i.`package`=="com.whatsapp.w4b")
        {
            etxt?.setText("WA Business")
        }
        okButton?.setOnClickListener { //open whatsapp
            val i = activity?.packageManager?.getLaunchIntentForPackage(WAoptions.appPackage)
            if (i != null) {
                startActivity(i)
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "whatsApp is not installed",
                    Toast.LENGTH_LONG
                ).show()
            }
            dialog2.dismiss()
        }
        cancelDialog?.setOnClickListener {
            dialog2.dismiss()
        }
        dialog2?.show()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewImage)
        progressBar = view.findViewById<ProgressBar>(com.example.stickers.R.id.progressBarImage)
        imgNoFound = view.findViewById<ImageView>(R.id.img_no_saved)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        messageTextView = view.findViewById<TextView>(R.id.messageTextImage)
        countIsSaved = 0

        binding.loadVideos.setOnClickListener {
            binding.loadImages.setBackgroundResource(R.drawable.whitestrokes)
            binding.loadVideos.setBackgroundResource(R.drawable.btn_back_green)
            binding.loadVideos.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.loadImages.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_backgroud))
            loadImages=false
            getFiles()

        }

        binding.loadImages.setOnClickListener {
          //  openSingleTranslateFragment()
            binding.loadVideos.setBackgroundResource(R.drawable.whitestrokes)
            binding.loadImages.setBackgroundResource(R.drawable.btn_back_green)
            binding.loadVideos.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_backgroud))
            binding.loadImages.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            loadImages=true
            getFiles()
        }
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
            ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
            ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
        )

        try {
            swipeRefreshLayout?.performClick()
            swipeRefreshLayout?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { this.getFiles() })
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }

        recyclerView?.setHasFixedSize(true)

        _binding?.grantPermission?.setOnClickListener {
            showHowToUse()
        }
        imagesViewModel.getAllFiles.observe(viewLifecycleOwner) {
            if (it)
                getFiles()
        }
        imagesViewModel.selected.observe(viewLifecycleOwner) { item ->
            // Update the UI
            if (item == 1) {
                getFiles()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkFiles() {
        try {
            Log.e("tag**", "checkFiles ${filesAdapter?.itemCount}")
            swipeRefreshLayout?.isRefreshing = false
            if (filesAdapter?.itemCount == 0 || filesAdapter == null) {

                messageTextView?.visibility = View.VISIBLE
                _binding?.grantPermission?.visibility = View.VISIBLE
                imgNoFound?.visibility = View.VISIBLE
            } else {

                messageTextView?.visibility = View.GONE
                _binding?.grantPermission?.visibility = View.GONE
                imgNoFound?.visibility = View.GONE
            }
        } catch (exp: Exception) {

            dismissDialog()
        }
        dismissDialog()
    }


    private fun getFiles() {
        if(loadImages==true)
        {
            getImages()
        }
        else{
            getVideos()
        }


    }

    private fun getVideos() {
        showDialog()
        val app_dir = File(Common.APP_DIR)
        if (app_dir.exists()) {
            messageTextView!!.visibility = View.GONE
            _binding?.messageTextImage?.visibility = View.GONE
            _binding?.grantPermission?.visibility = View.GONE
            _binding?.imgNoSaved?.visibility = View.GONE

            Coroutines.ioThenMain({
                savedFilesList.clear()
                savedVideoFilesList.clear()
                savedStatusListFiles29.clear()
                savedVideoStatusListFiles29.clear()
                savedFilesWithHeadersList.clear()
                app_dir.listFiles()?.forEach { file ->
                    val status =
                        Status(
                            file,
                            file.name,
                            file.absolutePath
                        )

                    if(status.isVideo)
                    {
                        savedVideoStatusListFiles29.add(status)
                        savedVideoFilesList.add(status)
                    }
                    else{
                        savedStatusListFiles29.add(status)
                        savedFilesList.add(status)
                    }

                    countIsSaved++
                }
                try {
                    savedVideoFilesList.reversed()
                    savedVideoFilesList.distinct().forEach { status ->

                        val time = status.file.lastModified()
                        val date = getDate(status.file.lastModified())
                        val title = time.millisToDate().formatToDMY()
//                                if (isToday(date) && isRecent(time)) "Recent" else if (isToday(date)) "Today" else date

                        val sh = StatusesHeaders(title, null)
                        val i =
                            savedFilesWithHeadersList.firstOrNull { f -> f.title.equals(title) }
                        if (i == null) {
                            savedFilesWithHeadersList.add(sh)
//                                savedFilesList.forEach { statuso ->
                            val sh1 = StatusesHeaders(title, status)
                            savedFilesWithHeadersList.add(sh1)
//                                }
                        } else {
                            val sh1 = StatusesHeaders(title, status)
                            savedFilesWithHeadersList.add(sh1)
                        }

                    }
                } catch (exp: Exception) {
                }
            }, {


                activity?.runOnUiThread {
                    if (countIsSaved <= 0) {
                        progressBar!!.visibility = View.GONE
                        messageTextView!!.visibility = View.VISIBLE
                        _binding?.grantPermission?.visibility = View.VISIBLE
                        imgNoFound!!.visibility = View.VISIBLE
                        anyImages.postValue(0)
                    }
                }
//                savedFilesWithHeadersList.reverse()
                val updatedSavedFilesList = mutableListOf<Status>()

                for (i in 0 until savedVideoFilesList.size) {
                    updatedSavedFilesList.add(savedVideoFilesList[i])
                    if (i == 2 && savedVideoFilesList.size >= 3) {
                        updatedSavedFilesList.add(savedVideoFilesList[i])
                    }
                }
                savedVideoFilesList.clear()
                savedVideoFilesList.addAll(updatedSavedFilesList)
                filesAdapter = SavedStatusAdapter(
                    savedVideoFilesList,
                    this,
                    this,requireActivity(),
                    recyclerView!!,
                    requireActivity().supportFragmentManager
                )
                recyclerView!!.adapter = filesAdapter
//                val g = GridLayoutManager(activity, Common.GRID_COUNT)
//                recyclerView?.setLayoutManager(g)
//                g.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//                    override fun getSpanSize(position: Int): Int {
//                        return when (filesAdapter?.getItemViewType(position)) {
//                            0 -> 2
//                            else -> 1
//                        }
//                    }
//                }
                filesAdapter?.setLayoutManager()
                filesAdapter?.notifyDataSetChanged()
                progressBar!!.visibility = View.GONE
                checkFiles()

                progressBar!!.visibility = View.GONE
                messageTextView!!.visibility = View.VISIBLE
                _binding?.grantPermission?.visibility = View.VISIBLE
                imgNoFound!!.visibility = View.VISIBLE
                anyImages.postValue(0)

                swipeRefreshLayout!!.isRefreshing = false
                fileList = savedFilesList
                file30List = null
                checkFiles()
            })
        } else {
            messageTextView?.visibility = View.VISIBLE
            _binding?.grantPermission?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            imgNoFound?.visibility = View.VISIBLE
            anyImages.postValue(0)
            checkFiles()
        }
    }

    private fun getImages() {
        showDialog()
        val app_dir = File(Common.APP_DIR)
        if (app_dir.exists()) {
            messageTextView!!.visibility = View.GONE
            _binding?.messageTextImage?.visibility = View.GONE
            _binding?.grantPermission?.visibility = View.GONE
            _binding?.imgNoSaved?.visibility = View.GONE

            Coroutines.ioThenMain({
                savedFilesList.clear()
                savedVideoFilesList.clear()
                savedStatusListFiles29.clear()
                savedVideoStatusListFiles29.clear()
                savedFilesWithHeadersList.clear()
                app_dir.listFiles()?.forEach { file ->
                    val status = Status(file, file.name, file.absolutePath)

                    if (status.isVideo) {
                        savedVideoStatusListFiles29.add(status)
                        savedVideoFilesList.add(status)
                    } else {
                        savedStatusListFiles29.add(status)
                        savedFilesList.add(status)
                    }

                    countIsSaved++
                }




                try {

                    savedFilesList.distinct().forEach { status ->

                        val time = status.file.lastModified()
                        val date = getDate(status.file.lastModified())
                        val title = time.millisToDate().formatToDMY()
    //                   if (isToday(date) && isRecent(time)) "Recent" else if (isToday(date)) "Today" else date

                        val sh = StatusesHeaders(title, null)
                        val i =
                            savedFilesWithHeadersList.firstOrNull { f -> f.title.equals(title) }
                        if (i == null) {
                            savedFilesWithHeadersList.add(sh)
    //                                savedFilesList.forEach { statuso ->
                            val sh1 = StatusesHeaders(title, status)
                            savedFilesWithHeadersList.add(sh1)
    //                                }
                        } else {
                            val sh1 = StatusesHeaders(title, status)
                            savedFilesWithHeadersList.add(sh1)
                        }

                    }
                } catch (exp: Exception) {
                }
            }, {


                activity?.runOnUiThread {
                    if (countIsSaved <= 0) {
                        progressBar!!.visibility = View.GONE
                        messageTextView!!.visibility = View.VISIBLE
                        _binding?.grantPermission?.visibility = View.VISIBLE
                        imgNoFound!!.visibility = View.VISIBLE
                        anyImages.postValue(0)
                    }
                }
                savedFilesList.reverse()
                val updatedSavedFilesList = mutableListOf<Status>()

                for (i in 0 until savedFilesList.size) {
                    updatedSavedFilesList.add(savedFilesList[i])
                    if (i == 2 && savedFilesList.size >= 3) {
                        updatedSavedFilesList.add(savedFilesList[i])
                    }
                }
                savedFilesList.clear()
                savedFilesList.addAll(updatedSavedFilesList)
                filesAdapter = SavedStatusAdapter(savedFilesList, this, this,requireActivity(),binding.recyclerViewImage,requireActivity().supportFragmentManager)
                recyclerView!!.adapter = filesAdapter
                val g = GridLayoutManager(activity, Common.GRID_COUNT)
//                recyclerView?.setLayoutManager()
//                g.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//                    override fun getSpanSize(position: Int): Int {
//                        return when (filesAdapter?.getItemViewType(position)) {
//                            0 -> 2
//                            else -> 1
//                        }
//                    }
//                }
                filesAdapter?.setLayoutManager()
                filesAdapter?.notifyDataSetChanged()
                progressBar!!.visibility = View.GONE
                checkFiles()

                progressBar!!.visibility = View.GONE
                messageTextView!!.visibility = View.VISIBLE
                _binding?.grantPermission?.visibility = View.VISIBLE
                imgNoFound!!.visibility = View.VISIBLE
                anyImages.postValue(0)

                swipeRefreshLayout!!.isRefreshing = false
                fileList = savedFilesList
                file30List = null
                checkFiles()
            })
        } else {
            messageTextView?.visibility = View.VISIBLE
            _binding?.grantPermission?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            imgNoFound?.visibility = View.VISIBLE
            anyImages.postValue(0)
            checkFiles()
        }
    }

    fun Long.millisToDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar.time
    }

    fun Date.formatToDMY(): String {
        val df = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        return df.format(this)
    }

    fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd MMM", calendar).toString()
        return date
    }

    fun isToday(date: String): Boolean {
        val today = Calendar.getInstance(Locale.ENGLISH)
        val tdate = DateFormat.format("dd MMM", today).toString()
        return date == tdate
    }

    fun isRecent(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val cTime = calendar.timeInMillis
        val currentTime = Calendar.getInstance(Locale.ENGLISH).timeInMillis
        val differenceInMillis: Long = currentTime - cTime
        val differenceInHours =
            differenceInMillis / 1000L / 60L / 60L // Divide by millis/sec, secs/min, mins/hr
        return differenceInHours < 2
    }

    override fun onDeleted(flag: Boolean) {
        countIsSaved = 0
        getFiles()
        Log.e("getFilesx", "call again $countIsSaved")
    }

    override fun onResume() {
        super.onResume()

        getFiles()
    }

    override fun onSavedMultiSelectModeActivated() {
        val parentActivity = activity as? MainDashActivity
        parentActivity?.onSavedMultiSelectMode(
            {
                getFiles()
            },
            {
                //deleteListener

            },
            {
                //ShareListener

            })
    }
}