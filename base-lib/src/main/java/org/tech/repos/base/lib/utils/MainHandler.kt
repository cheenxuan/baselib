package org.tech.repos.base.lib.utils

import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Author: xuan
 * Created on 2021/7/9 14:29.
 *
 * Describe:
 */
object MainHandler {
    
    private val handler = Handler(Looper.getMainLooper())

    fun post(runnable: Runnable) {
        handler.post(runnable)
    }

    fun postDelay(delayMills: Long, runnable: Runnable) {
        handler.postDelayed(runnable, delayMills)
    }

    fun sendAtFrontOfQueue(runnable: Runnable) {
        val msg = Message.obtain(handler,runnable)
        handler.sendMessageAtFrontOfQueue(msg)
    }

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run()
        } else {
            handler.post(runnable)
        }
    }
    
    
}