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
    private var positionX = 0f
    private var positionY = 0f
    private var shiftX = 0f
    private var shiftY = 0f

    // Cons
    private val minimumFactorX = 1f
    private val minimumFactorY = 1f

    // States
    private var noWidth = 0f
    private var noHeight = 0f
    private var spaceLeft = 0f
    private var spaceRight = 0f
    private var spaceUp = 0f
    private var spaceDown = 0f
    private var jumpLeft = false
    private var jumpUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Buttons
        binding.buttonNo.setOnClickListener { runaway() }
        binding.buttonYes.setOnClickListener { openDialog() }

        supportActionBar?.hide()
    }

    private fun runaway() {
        noWidth = binding.buttonNo.width.toFloat()
        noHeight = binding.buttonNo.height.toFloat()

        moveHorizontal()
        moveVertical()
    }

    private fun moveHorizontal() {
        spaceLeft = binding.buttonNo.x
        spaceRight = getSpaceRight()

        shiftX = getShiftX()

        if (jumpLeft)
            positionX -= shiftX
        else
            positionX += shiftX

        logPrevPositionX()

        val moverHorizontal =
            ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_X, positionX)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                logFinalPositionX()
            }
        })
        moverHorizontal.start()
    }

    private fun getSpaceRight(): Float {
        val container = binding.buttonNo.parent as ViewGroup
        val containerW = container.width
        return containerW - (spaceLeft + noWidth)
    }

    private fun getShiftX(): Float {
        /*
        val yNo = binding.buttonNo.y
        val yEndNo = yNo + shiftY
        val yYes = binding.buttonYes.y
        val heightYes = binding.buttonYes.height

        val rangeY = yYes..(yYes + heightYes)

        if (yEndNo in rangeY)
            Log.e("moveHorizontal", "overlap")
        */
        val minShift = getMinShiftX()
        val maxShift = getMaxShiftX()

        // avoid overlap

        return minShift + maxShift
    }

    private fun getMinShiftX(): Float {
        return noWidth * minimumFactorX
    }

    private fun getMaxShiftX(): Float {
        val remainingFactor = getRemainingFactorX()
        return round(Math.random().toFloat() * (remainingFactor * noWidth))
    }

    private fun getRemainingFactorX(): Float {
        val buttonsContained = getButtonsContainedX()
        return buttonsContained - minimumFactorX
    }

    private fun getButtonsContainedX(): Float {
        jumpLeft = getJumpLeft()

        return if (jumpLeft)
            spaceLeft / noWidth
        else
            spaceRight / noWidth
    }

    private fun getJumpLeft(): Boolean {
        val minimumShift = noWidth * minimumFactorX

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

    private fun moveVertical() {
        spaceUp = binding.buttonNo.y
        spaceDown = getSpaceDown()

        shiftY = getShiftY()

        if (jumpUp)
            positionY -= shiftY
        else
            positionY += shiftY

        logPrevPositionY()

        val moverHorizontal =
            ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_Y, positionY)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                logFinalPositionY()
            }
        })
        moverHorizontal.start()
    }

    private fun getSpaceDown(): Float {
        val container = binding.buttonNo.parent as ViewGroup
        val containerH = container.height
        return containerH - (spaceUp + noHeight)
    }

    private fun getShiftY(): Float {
        val minShift = getMinShiftY()
        val maxShift = getMaxShiftY()

        return minShift + maxShift
    }

    private fun getMinShiftY(): Float {
        return noHeight * minimumFactorY
    }

    private fun getMaxShiftY(): Float {
        val remainingFactorY = getRemainingFactorY()
        return round(Math.random().toFloat() * (remainingFactorY * noHeight))
    }

    private fun getRemainingFactorY(): Float {
        val buttonsContainedY = getButtonsContainedY()
        return buttonsContainedY - minimumFactorY
    }

    private fun getButtonsContainedY(): Float {
        jumpUp = getJumpUp()

        return if (jumpUp)
            spaceUp / noHeight
        else
            spaceDown / noHeight
    }

    private fun getJumpUp(): Boolean {
        val minimumShift = noHeight * minimumFactorY

        val couldJumpUp = spaceUp >= minimumShift
        val couldJumpDown = spaceDown >= minimumShift
        val spaceAreEquals = spaceUp == spaceDown

        if (couldJumpUp && couldJumpDown) {
            return if (spaceAreEquals)
                true
            else
                Random().nextBoolean()
        }

        if (couldJumpUp) return true
        if (couldJumpDown) return false

        return false
    }

    private fun openDialog() {
        MaterialAlertDialogBuilder(this)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            }
            .setTitle(resources.getString(R.string.i_knew_it))
            .setIcon(R.drawable.ic_heart)
            .show()
    }

    private fun logPrevPositionX() {
        Log.i(
            "moveHorizontal",
            "logPrevPositionX:========================================================================" +
                    "\nprevXNo = ${binding.buttonNo.x} to ${binding.buttonNo.x + binding.buttonNo.width}" +
                    "\nshiftX = $shiftX" +
                    "\njump = ${if (jumpLeft) "left" else "right"}"
        )
    }

    private fun logFinalPositionX() {
        Log.i(
            "moveHorizontal",
            "logFinalPositionX:" +
                    "\ncurrentXNo = ${binding.buttonNo.x} to ${binding.buttonNo.x + binding.buttonNo.width}"
        )
    }

    private fun logPrevPositionY() {
        Log.i(
            "moveVertical",
            "logPrevPositionY:" +
                    "\nprevYNo = ${binding.buttonNo.y} to ${binding.buttonNo.y + binding.buttonNo.height}" +
                    "\nshiftY = $shiftY" +
                    "\njumpUp = ${if (jumpUp) "up" else "down"}"
        )
    }

    private fun logFinalPositionY() {
        Log.i(
            "moveVertical",
            "logFinalPositionY:" +
                    "\ncurrentYNo = ${binding.buttonNo.y} to ${binding.buttonNo.y + binding.buttonNo.height}"
        )
    }
}
