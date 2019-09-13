package com.sirelon.discover.location.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.sirelon.discover.location.R
import java.util.Random

/**
 * Created on 2019-09-13 11:42 for DiscoverLocationHere.
 */
class ColorUtils(context: Context) {

    val slithyWhite = ContextCompat.getColor(context, R.color.slithyWhite)
    val slithyBlack = ContextCompat.getColor(context, R.color.slithyBlack)

    fun getContrastColor(color: Int): Int {
        val y =
            ((299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000).toDouble()
        return if (y >= 128) slithyBlack else slithyWhite
    }

    fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(230, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}