package org.tech.repos.base.lib.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * Author: xuan
 * Created on 2021/7/8 11:16.
 *
 * Describe:
 */
object ExtStatusBar {
    /**
     * @param darkContent true:意味着 白底黑字 false:黑底白字
     * @param statusColor 状态栏背景色
     * @param translucent 沉浸式效果 也就是页面的布局延伸到状态栏之下
     */
    fun setStatusBar(
        activity: Activity,
        darkContent: Boolean,
        statusColor: Int = Color.TRANSPARENT,
        translucent: Boolean = true
    ) {

        val window = activity.window
        val decorView = window.decorView
        var visibility = decorView.systemUiVisibility

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //这俩不能同时出现
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //请求系统 绘制状态栏的背景色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = statusColor
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visibility = if (darkContent) {
                //白底黑字--浅色主题
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                //黑底白字--深色主题
                // java  visibility &= ~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        if (translucent) {
            //此时 能够使得页面的布局延伸到状态栏之下，但是状图兰的图标 也看不见了-,使得状图兰的图标 恢复可见性
            //bugfix:手快写错了 应该是 SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN。
            visibility =
                visibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        decorView.systemUiVisibility = visibility
        
    }
}