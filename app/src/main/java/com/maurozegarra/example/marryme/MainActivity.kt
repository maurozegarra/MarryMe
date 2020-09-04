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
    private var previousPositionX = 0f

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
        val posX = binding.buttonNo.x
        val posY = binding.buttonNo.y

        val spaceDownButton = containerH - (posY + buttonHeight * 2)
        val spaceUpButton = posY - buttonHeight

        val jumpUp = Random().nextBoolean()

        var shift = buttonHeight

        if (jumpUp) {
            shift = Math.random().toFloat() * spaceDownButton
        } else {
            shift = Math.random().toFloat() * -spaceUpButton
        }

        previousPositionY += shift

        val moverVertical = ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_Y, previousPositionY)
        moverVertical.interpolator = AccelerateInterpolator(1f)
        moverVertical.duration = 100
        moverVertical.start()

        ////////////////////////////////////////////////////////////////////////////////////////////

        val spaceRightButton = containerW - (posX + buttonWidth * 2)
        val spaceLeftButton = posX - buttonWidth

        val jumpRight = Random().nextBoolean()

        val shiftHorizontal = if (jumpRight) {
            Math.random().toFloat() * spaceRightButton
        } else {
            Math.random().toFloat() * -spaceLeftButton
        }

        previousPositionX += shiftHorizontal

        val moverHorizontal = ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_X, previousPositionX)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.start()
    }
}
