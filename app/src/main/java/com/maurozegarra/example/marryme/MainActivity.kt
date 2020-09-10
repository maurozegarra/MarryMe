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
    private var widthNo = 0f
    private var heightNo = 0f
    private var spaceLeft = 0f
    private var spaceRight = 0f
    private var spaceUp = 0f
    private var spaceDown = 0f
    private var jumpLeft = false
    private var jumpUp = false
    private var hasLogYes = false

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
        widthNo = binding.buttonNo.width.toFloat()
        heightNo = binding.buttonNo.height.toFloat()

        // moveHorizontal depende de moveVertical.
        // Para evitar la sobreposicion de "No", primero debo conocer el desplazamiento en Y
        moveHorizontal()
        moveVertical()
    }

    private fun moveHorizontal() {
        logYes()
        spaceLeft = binding.buttonNo.x
        spaceRight = getSpaceRight()

        shiftX = getShiftX()

        if (jumpLeft)
            positionX -= shiftX
        else
            positionX += shiftX

        //logPrevPositionX()

        val moverHorizontal =
            ObjectAnimator.ofFloat(binding.buttonNo, View.TRANSLATION_X, positionX)
        moverHorizontal.interpolator = AccelerateInterpolator(1f)
        moverHorizontal.duration = 100
        moverHorizontal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                //logCurrentPositionX()
            }
        })
        moverHorizontal.start()
    }

    private fun getSpaceRight(): Float {
        val container = binding.buttonNo.parent as ViewGroup
        val containerW = container.width
        return containerW - (spaceLeft + widthNo)
    }

    private fun getShiftX(): Float {
        val minShift = getMinShiftX()
        val maxShift = getMaxShiftX()

        return minShift + maxShift
    }

    private fun getMinShiftX(): Float {
        return widthNo * minimumFactorX
    }

    private fun getMaxShiftX(): Float {
        val remainingFactor = getRemainingFactorX()
        return round(Math.random().toFloat() * (remainingFactor * widthNo))
    }

    private fun getRemainingFactorX(): Float {
        val buttonsContained = getButtonsContainedX()
        return buttonsContained - minimumFactorX
    }

    private fun getButtonsContainedX(): Int {
        jumpLeft = getJumpLeft()

        return if (jumpLeft)
            (spaceLeft / widthNo).toInt()
        else
            (spaceRight / widthNo).toInt()
    }

    private fun getJumpLeft(): Boolean {
        val minimumShift = widthNo * minimumFactorX

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

        shiftY = avoidOverlapY(shiftY)

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
                logCurrentPositionY()
            }
        })
        moverHorizontal.start()
    }

    private fun getSpaceDown(): Float {
        val container = binding.buttonNo.parent as ViewGroup
        val containerH = container.height
        return containerH - (spaceUp + heightNo)
    }

    private fun getShiftY(): Float {
        val minShift = getMinShiftY()
        val maxShift = getMaxShiftY()

        return minShift + maxShift
    }

    private fun avoidOverlapY(shiftY: Float): Float {
        var tmpShiftY = shiftY

        val topNo: Float
        val bottomNo: Float
        if (jumpUp) {
            topNo = spaceUp - tmpShiftY
            bottomNo = spaceUp + heightNo - tmpShiftY
        } else {
            topNo = spaceUp + tmpShiftY
            bottomNo = spaceUp + heightNo + tmpShiftY
        }

        val yYes = binding.buttonYes.y
        val heightYes = binding.buttonYes.height
        val yRangeYes = yYes..(yYes + heightYes)

        if (topNo in yRangeYes) {
            Log.e("moveVertical","overlap topNo = $topNo, iniShiftY = $shiftY, heightNo = $heightNo")
            if (jumpUp)
                tmpShiftY += 1.5f * heightNo
            else
                tmpShiftY += heightNo
        } else if (bottomNo in yRangeYes) {
            Log.e("moveVertical","overlap bottomNo = $bottomNo, iniShiftY = $shiftY, heightNo = $heightNo")
            if (jumpUp)
                tmpShiftY += heightNo
            else
                tmpShiftY += 1.5f * heightNo
        }

        return tmpShiftY
    }

    private fun getMinShiftY(): Float {
        return heightNo * minimumFactorY
    }

    private fun getMaxShiftY(): Float {
        val remainingFactorY = getRemainingFactorY()
        return round(Math.random().toFloat() * (remainingFactorY * heightNo))
    }

    private fun getRemainingFactorY(): Float {
        val buttonsContainedY = getButtonsContainedY()
        return buttonsContainedY - minimumFactorY
    }

    private fun getButtonsContainedY(): Int {
        jumpUp = getJumpUp()

        return if (jumpUp)
            (spaceUp / heightNo).toInt()
        else
            (spaceDown / heightNo).toInt()
    }

    private fun getJumpUp(): Boolean {
        val minimumShift = heightNo * minimumFactorY

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

    private fun logCurrentPositionX() {
        Log.i(
            "moveHorizontal",
            "logCurrentPositionX:" +
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

    private fun logCurrentPositionY() {
        Log.i(
            "moveVertical",
            "logCurrentPositionY:" +
                    "\ncurrentYNo = ${binding.buttonNo.y} to ${binding.buttonNo.y + binding.buttonNo.height}"
        )
    }

    private fun logYes() {
        if (!hasLogYes) {
            Log.e(
                "logYes",
                "\nxYes = ${binding.buttonYes.x} to ${binding.buttonYes.x + binding.buttonYes.width}" +
                        " | yYes = ${binding.buttonYes.y} to ${binding.buttonYes.y + binding.buttonYes.height}"
            )
        }

        hasLogYes = true
    }
}
