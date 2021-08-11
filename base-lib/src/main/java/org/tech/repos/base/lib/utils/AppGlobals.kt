package org.tech.repos.base.lib.utils

import android.app.Application
import java.lang.Exception

/**
 * Author: xuan
 * Created on 2021/7/5 16:38.
 *
 * Describe:
 */
object AppGlobals {

    var application: Application? = null

    fun get(): Application? {
        if (application == null) {
            try {
                application = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            } catch (ex: Exception) {
                ex.printStackTrace()
            }


        }
        return application

    }
} 