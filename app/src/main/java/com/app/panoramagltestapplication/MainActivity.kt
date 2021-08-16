package com.app.panoramagltestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import com.app.panoramagltestapplication.databinding.ActivityMainBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.utils.PLUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private val resourceIds = intArrayOf(R.raw.test, R.raw.test2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        plManager = PLManager(this).apply {
            setContentView(binding.contentView)
            onCreate()
            isAccelerometerEnabled = false
            isInertiaEnabled = false
            isZoomEnabled = false
        }
        changePanorama(0)
        binding.button1.setOnClickListener { changePanorama(0) }
        binding.button2.setOnClickListener { changePanorama(1) }
    }

    override fun onResume() {
        super.onResume()
        plManager.onResume()
    }

    override fun onPause() {
        plManager.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        plManager.onDestroy()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return plManager.onTouchEvent(event)
    }

    private fun changePanorama(index: Int) {
        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(this, resourceIds[index])
        val panorama = PLSphericalPanorama()
        panorama.setImage(PLImage(image3D, false))
        plManager.panorama = panorama
        currentIndex = index
    }
}