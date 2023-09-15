package com.example.stickers.Activities

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide

import com.example.stickers.app.AppClass.Companion.fileListCollage
import com.example.stickers.app.BillingBaseActivity
import com.example.stickers.app.RemoteDateConfig
import com.example.stickers.app.getUriPath
import com.example.stickers.app.shareFile
import com.example.stickers.databinding.ActivityViewCollageImageBinding


class ViewCollageImageActivity : BillingBaseActivity() {

    private val binding by lazy {
        ActivityViewCollageImageBinding.inflate(layoutInflater)
    }
    private var path = ""
    override fun onResume() {
        super.onResume()

    }
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        path = intent.getStringExtra("path") ?: ""
        if (path.isNotEmpty())
            Glide.with(this).load(path).into(binding.imgFull)


//        if (is30Plus) {
//            Picasso.with(getApplicationContext()).load(ImagesFragment.ItemsViewModel.getFile().getUri()).into(binding.imgFull);
//        } else {
//            Picasso.with(getApplicationContext()).load(status.getFile()).into(binding.imgFull);
//        }

        val list = ArrayList<String>()
        if (fileListCollage != null && !fileListCollage!!.isEmpty()) {
            var na = 0
            for (a in fileListCollage!!) {

                list.add(a.filePath)
                na += 1
            }
        }
       // loadMediaSliderView(list, "image", false, true, true, "Image-Slider", "#000000", null, pos)
        binding.imageView25.setOnClickListener(
            View.OnClickListener {
                if (list.size > position + 1) {
                    position = position + 1

                    if (list.get(position).isNotEmpty())
                        Glide.with(this).load(list.get(position)).into(binding.imgFull)
                }
            }
        )
        binding.imageView26.setOnClickListener(
            View.OnClickListener {
                if (position - 1 >= 0) {
                    position = position - 1

                    if (list.get(position).isNotEmpty())
                        Glide.with(this).load(list.get(position)).into(binding.imgFull)
                }
            }
        )
        initViews()
    }

    private fun initViews() {
        with(binding) {
            imgBackFullScreen.setOnClickListener { onBackPressed() }
            imgShare.setOnClickListener {
                if (path.isNotEmpty()) {
                    shareFile(getUriPath(path), supportFragmentManager)
                }
            }
        }
    }
    override fun onBackPressed() {
        fileListCollage = null

            finish()

    }
}