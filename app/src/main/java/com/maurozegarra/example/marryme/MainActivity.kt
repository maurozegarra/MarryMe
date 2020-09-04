package com.maurozegarra.example.marryme

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.maurozegarra.example.marryme.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var previousPositionY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNo.setOnClickListener { runaway() }

        supportActionBar?.hide()
    }

    private fun runaway() {
        val container = binding.buttonNo.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var buttonWidth: Float = binding.buttonNo.width.toFloat()
        val buttonHeight: Float = binding.buttonNo.height.toFloat()
        //val posX = binding.buttonNo.x
        val posY = binding.buttonNo.y

        val spaceDownButton = containerH - (posY + buttonHeight * 2)
        val spaceUpButton = posY - buttonHeight

        val jumpDown = Random().nextBoolean()

        val shift = 200f

        previousPositionY += shift


//        moveToY = if (jumpDown) {
//            Math.random().toFloat() * spaceDownButton
//        } else {
//            Math.random().toFloat() * -spaceUpButton
//        }

        val mover = ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_Y, previousPositionY)
        mover.interpolator = AccelerateInterpolator(1f)
        mover.start()
    }


}
