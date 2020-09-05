package com.maurozegarra.example.marryme

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurozegarra.example.marryme.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var positionY = 0f
    private var positionX = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNo.setOnClickListener { runaway() }
        binding.buttonYes.setOnClickListener { openDialog() }

        supportActionBar?.hide()
    }

    private fun openDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.i_knew_it))
            .show()
    }

    private fun runaway() {
        val container = binding.buttonNo.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        val buttonWidth: Float = binding.buttonNo.width.toFloat()
        val buttonHeight: Float = binding.buttonNo.height.toFloat()
        val posX = binding.buttonNo.x
        val posY = binding.buttonNo.y

        moveVertical(containerH, posY, buttonHeight)
        moveHorizontal(containerW, posX, buttonWidth)
    }

    private fun moveVertical(containerH: Int, posY: Float, buttonHeight: Float) {
        val minimumFactor = 2
        val minimumShift = buttonHeight * minimumFactor
        val spaceAboveButton = posY - buttonHeight
        val spaceBellowButton = containerH - (posY + buttonHeight * 2)

        val jumpTop = mayJumpTop(spaceAboveButton, spaceBellowButton, minimumShift)
        val buttonsContained =
            getButtonsContained(jumpTop, spaceAboveButton, spaceBellowButton, buttonHeight)
        val remainingFactor = buttonsContained - minimumFactor
        val shift = getShift(minimumShift, remainingFactor, buttonHeight)

        if (jumpTop)
            positionY -= shift
        else
            positionY += shift

        val moverVertical = ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_Y, positionY)
        moverVertical.interpolator = AccelerateInterpolator(1f)
        moverVertical.duration = 100
        moverVertical.start()
    }

    private fun mayJumpTop(
        spaceAboveButton: Float,
        spaceBellowButton: Float,
        minimumShift: Float
    ): Boolean {
        val couldJumpTop = spaceAboveButton >= minimumShift
        val couldJumpBottom = spaceBellowButton >= minimumShift
        val spaceAreEquals = spaceAboveButton == spaceBellowButton

        if (couldJumpTop && couldJumpBottom) {
            return if (spaceAreEquals)
                true
            else
                Random().nextBoolean()
        }

        if (couldJumpTop) return true
        if (couldJumpBottom) return false

        // hasta aquí todas las posibilidades están agotadas, pongo true solo por preferir un salto
        // hacia arriba pero, no debería pasar
        return true
    }

    private fun getButtonsContained(
        jumpTop: Boolean,
        spaceAboveButton: Float,
        spaceBellowButton: Float,
        buttonHeight: Float
    ): Int {
        return if (jumpTop)
            spaceAboveButton.toInt() / buttonHeight.toInt()
        else
            spaceBellowButton.toInt() / buttonHeight.toInt()
    }

    // Mínimo debe devolver 3 veces el alto del botón y máximo el número de veces contenido el botón
    // en el espacio disponible
    private fun getShift(minimumShift: Float, remainingFactor: Int, buttonHeight: Float): Float {
        return minimumShift + Math.random().toFloat() * (remainingFactor * buttonHeight)
    }

    private fun moveHorizontal(containerW: Int, posX: Float, buttonWidth: Float) {
        val spaceRightButton = containerW - (posX + buttonWidth * 2)
        val spaceLeftButton = posX - buttonWidth

        val jumpRight = Random().nextBoolean()

        val shiftHorizontal = if (jumpRight) {
            Math.random().toFloat() * spaceRightButton
        } else {
            Math.random().toFloat() * -spaceLeftButton
        }

        positionX += shiftHorizontal

        val moverHorizontal =
            ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_X, positionX)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.start()
    }
}
