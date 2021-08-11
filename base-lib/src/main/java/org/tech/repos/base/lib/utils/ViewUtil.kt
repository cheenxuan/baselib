package org.tech.repos.base.lib.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Author: xuan
 * Created on 2021/7/10 00:16.
 *
 * Describe:
 */
object ViewUtil {
    /**
     * 获取指定类型的子View
     * @param group ViewGroup
     * @param clazz 如：RecyclerView
     *
     * @return 指定类型的View
     */
    fun <T> findTypeView(group: ViewGroup?, clazz: Class<T>): T? {
        if (group == null) {
            return null
        }

        val deque = ArrayDeque<View>()
        deque.add(group)
        while (!deque.isEmpty()) {
            val node = deque.removeFirst()
            if (clazz.isInstance(node)) {
                return clazz.cast(node)
            }else if (node is ViewGroup) {
                val container:ViewGroup = node
                for (child in container.children) {
                    deque.add(child)
                }
            }
        }
        return null
    }

    fun isActivityDestroy(context: Context): Boolean {
        val activity = findActivity(context)
        return if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activity.isDestroyed || activity.isFinishing
            } else activity.isFinishing
        } else true
    }

    private fun findActivity(context: Context): Activity? {
        //怎么判断一个context  是不是Activity
        if (context is Activity) return context else if (context is ContextWrapper) {
            return findActivity(context.baseContext)
        }
        return null
    }

}