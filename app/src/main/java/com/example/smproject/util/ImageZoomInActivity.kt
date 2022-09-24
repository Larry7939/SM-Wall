package com.example.smproject.util

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smproject.R
import com.example.smproject.config.BaseActivity
import com.example.smproject.databinding.ActivityImageZoomInBinding
import com.example.smproject.src.main.posted.PostedView
import com.example.smproject.src.main.posted.models.PostedResponse

class ImageZoomInActivity : BaseActivity<ActivityImageZoomInBinding>(ActivityImageZoomInBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val img = intent.getByteArrayExtra("byteArray")
        val bm = img?.let { BitmapFactory.decodeByteArray(img,0, it.size) }
        binding.imageFull.setImageBitmap(bm)
        binding.imageFull.setOnClickListener {
            supportFinishAfterTransition()
        }
    }
}