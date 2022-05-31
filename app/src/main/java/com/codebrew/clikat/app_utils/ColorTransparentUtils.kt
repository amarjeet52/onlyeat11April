package com.codebrew.clikat.app_utils

import android.util.Log
import com.codebrew.clikat.R
import kotlin.math.roundToInt

object ColorTransparentUtils {
    // This default color int
    const val defaultColor = R.color.colorAccent
    const val TAG = "ColorTransparentUtils"
    /**
     * This method convert numver into hexa number or we can say transparent code
     * @param trans number of transparency you want
     * @return it return hex decimal number or transparency code
     */
    fun convert(trans: Int): String {
        val hexString = Integer.toHexString((255 * trans / 100.toFloat()).roundToInt())
        return (if (hexString.length < 2) "0" else "") + hexString
    }

    fun transparentColor10(colorCode: Int): String {
        return convertIntoColor(colorCode, 10)
    }

    fun transparentColor20(colorCode: Int): String {
        return convertIntoColor(colorCode, 20)
    }

    fun transparentColor30(colorCode: Int): String {
        return convertIntoColor(colorCode, 30)
    }

    fun transparentColor40(colorCode: Int): String {
        return convertIntoColor(colorCode, 40)
    }

    fun transparentColor50(colorCode: Int): String {
        return convertIntoColor(colorCode, 50)
    }

    fun transparentColor60(colorCode: Int): String {
        return convertIntoColor(colorCode, 60)
    }

    fun transparentColor70(colorCode: Int): String {
        return convertIntoColor(colorCode, 70)
    }

    fun transparentColor80(colorCode: Int): String {
        return convertIntoColor(colorCode, 80)
    }

    fun transparentColor90(colorCode: Int): String {
        return convertIntoColor(colorCode, 90)
    }

    fun transparentColor100(colorCode: Int): String {
        return convertIntoColor(colorCode, 100)
    }

    /**
     * Convert color code into transparent color code
     * @param colorCode color code
     * @param transCode transparent number
     * @return transparent color code
     */
    fun convertIntoColor(colorCode: Int, transCode: Int): String { // convert color code into hexa string and remove starting 2 digit
        val color = Integer.toHexString(colorCode).toUpperCase().substring(2)
        return if (!color.isEmpty() && transCode > 100) {
            if (color.trim { it <= ' ' }.length == 6) {
                "#" + convert(transCode) + color
            } else {
                Log.d(TAG, "Color is already with transparency")
                convert(transCode) + color
            }
        } else "#" + Integer.toHexString(defaultColor).toUpperCase().substring(2)
        // if color is empty or any other problem occur then we return deafult color;
    }
}