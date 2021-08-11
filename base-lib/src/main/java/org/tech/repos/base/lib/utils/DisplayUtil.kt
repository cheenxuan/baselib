package org.tech.repos.base.lib.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.NonNull

/**
 * Author: xuan
 * Created on 2021/7/9 23:22.
 *
 * Describe:
 */
object DisplayUtil {
    fun dp2px(dp: Float, resources: Resources): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,resources.displayMetrics).toInt()
    }

    fun getDisplayWidthInPx(@NonNull context: Context): Int {
        val wm:WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (wm != null) {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }
        
        return 0
    }

    fun getDisplayHeightInPx(@NonNull context: Context): Int {
        val wm:WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (wm != null) {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y
        }

        return 0
    }
}