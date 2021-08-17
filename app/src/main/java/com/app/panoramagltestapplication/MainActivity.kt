package com.app.panoramagltestapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.app.panoramagltestapplication.databinding.ActivityMainBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.utils.PLUtils
import android.app.ActivityManager
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import kotlin.math.roundToInt


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

        changeTo1()
    }

    @SuppressLint("SetTextI18n")
    private fun changeTo1() {
        Handler(Looper.getMainLooper()).postDelayed({
            count++
            Log.d("count", count.toString())
            binding.countView.text= "count= $count "
            changePanorama(1)
            changeTo0()
            printMemory()
        }, DELAY)
    }

    @SuppressLint("SetTextI18n")
    private fun changeTo0() {
        Handler(Looper.getMainLooper()).postDelayed({
            count++
            Log.d("count", count.toString())
            binding.countView.text= "count= $count "
            changePanorama(0)
            changeTo1()
            printMemory()
        }, DELAY)
    }

    private fun printMemory() {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)

        //Percentage can be calculated for API 16+
        val availableMegs: Int = (mi.availMem / 100000.0).roundToInt()
        val usedMegs: Int = ((mi.totalMem - mi.availMem)  / 100000.0).roundToInt()
        val totalMegs: Int = ((mi.totalMem)  / 100000.0).roundToInt()

        //Percentage can be calculated for API 16+
        val percentAvail: Int = (mi.availMem / mi.totalMem.toDouble() * 100.0).toInt()

        binding.memoryView.text = "total: ${mi.totalMem.humanReadableByteCountSI()}\navailable: ${mi.availMem.humanReadableByteCountSI()}\nused: ${(mi.totalMem - mi.availMem).humanReadableByteCountSI()}\n$percentAvail% free\nlow memory=${mi.lowMemory}"
    }

    private fun Long.humanReadableByteCountSI(): String {
        var bytes = this
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
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
        // by switching panoramas many times You can catch OOM error, because usage of RAM is increased and not cleared when changing panoramas,
        // i think that problem is in 65 line (when setting panorama to pLManager), because when i remove it there is no OOM error, but panoramas don't show

        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(this, resourceIds[index])
        val panorama = PLSphericalPanorama()
        panorama.setImage(PLImage(image3D, false))
        plManager.panorama = panorama
        currentIndex = index
    }

    companion object {
        var count = 0
        const val DELAY = 700L
    }
}