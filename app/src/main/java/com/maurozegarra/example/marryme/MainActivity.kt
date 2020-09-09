package com.maurozegarra.example.marryme

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maurozegarra.example.marryme.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var positionY = 0f
    private var positionX = 0f
    private var shiftY = 0f

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
            //.setMessage(resources.getString(R.string.i_knew_it))
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                // Respond to positive button press
            }
            .setTitle(resources.getString(R.string.i_knew_it))
            .setIcon(R.drawable.ic_heart)
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
        val spaceAboveButton = posY
        val spaceBellowButton = containerH - (posY + buttonHeight)

        val jumpTop = mayJumpTop(spaceAboveButton, spaceBellowButton, minimumShift)
        val buttonsContained =
            getButtonsContained(jumpTop, spaceAboveButton, spaceBellowButton, buttonHeight)
        val remainingFactor = buttonsContained - minimumFactor
        shiftY = getShiftY(minimumShift, remainingFactor, buttonHeight)

        if (jumpTop)
            positionY -= shiftY
        else
            positionY += shiftY

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
        return false
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
    private fun getShiftY(minShift: Float, remainingFactor: Int, buttonHeight: Float): Float {
        val maxShift = round(Math.random().toFloat() * (remainingFactor * buttonHeight))
        return minShift + maxShift
    }

    private fun moveHorizontal(containerW: Int, posX: Float, buttonWidth: Float) {
        val minimumFactor = 1f
        val minimumShift = buttonWidth * minimumFactor
        val spaceLeft = posX
        val spaceRight = containerW - (posX + buttonWidth)

        val jumpLeft = mayJumpLeft(spaceLeft, spaceRight, minimumShift)
        val buttonsContained = getButtonsContainedX(jumpLeft, spaceLeft, spaceRight, buttonWidth)
        val remainingFactor = buttonsContained - minimumFactor
        val shiftX = getShiftX(minimumShift, remainingFactor, buttonWidth)

        if (jumpLeft)
            positionX -= shiftX
        else
            positionX += shiftX

        /*
        // must inside getShiftX
        if (X and Y overlap) {
            shift
        }
        */

        Log.i(
            "moveHorizontal",
            "loggin:" +
                    "\nwidth_No = $buttonWidth" +
                    "\nheight_No = ${binding.buttonNo.height}" +
                    "\ncurPosX_No = $posX to ${posX + buttonWidth}" +
                    "\ncurPosY_No = ${binding.buttonNo.y} to ${binding.buttonNo.y + binding.buttonNo.height}" +
                    "\ncurPosX_Yes = ${binding.buttonYes.x} to ${binding.buttonYes.x + buttonWidth}" +
                    "\ncurPosY_Yes = ${binding.buttonYes.y} to ${binding.buttonYes.y + binding.buttonYes.height}" +
                    "\njump = ${if (jumpLeft) "left" else "right"}" +
                    "\nspaceLeft = $spaceLeft" +
                    "\nspaceRight = $spaceRight" +
                    "\nshiftX = $shiftX" +
                    "\nshiftY = $shiftY"
        )

        val moverHorizontal =
            ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_X, positionX)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.i("moveHorizontal", "endPosX_No: ${binding.buttonNo.x}")
                Log.i("moveHorizontal", "endPosY_No: ${binding.buttonNo.y} to ${binding.buttonNo.y + binding.buttonNo.height}")
            }
        })
        moverHorizontal.start()
    }

    private fun mayJumpLeft(
        spaceLeft: Float,
        spaceRight: Float,
        minimumShift: Float
    ): Boolean {
        val couldJumpLeft = spaceLeft >= minimumShift
        val couldJumpRight = spaceRight >= minimumShift
        val spaceAreEquals = spaceLeft == spaceRight

        if (couldJumpLeft && couldJumpRight) {
            return if (spaceAreEquals)
                true
            else
                Random().nextBoolean()
        }

        if (couldJumpLeft) return true
        if (couldJumpRight) return false

        return false
    }

    private fun getButtonsContainedX(
        jumpPreferred: Boolean,
        spaceBefore: Float,
        spaceAfter: Float,
        buttonSize: Float
    ): Int {
        return if (jumpPreferred)
            spaceBefore.toInt() / buttonSize.toInt()
        else
            spaceAfter.toInt() / buttonSize.toInt()
    }

    private fun getShiftX(minShift: Float, remainingFactor: Float, buttonSize: Float): Float {
        val maxShift = round(Math.random().toFloat() * (remainingFactor * buttonSize))

        val yNo = binding.buttonNo.y
        val yEndNo = yNo + shiftY
        val yYes = binding.buttonYes.y
        val heightYes = binding.buttonYes.height

        val rangeY = yYes .. (yYes + heightYes)

        if (yEndNo in rangeY)
            Log.e("moveHorizontal", "overlap")

        return minShift + maxShift
    }
}
