package org.tech.repos.base.lib.resful.annotation

import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention

/**
 * Author: xuan
 * Created on 2021/7/9 11:25.
 *
 * Describe:
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
annotation class CacheStrategy(
    val value: Int = NET_ONLY
) {
    companion object {
        const val CACHE_FIRST = 0//请求接口时候先读取本地缓存，再读取接口，接口成功后更新缓存 (页面初始化数据)
        const val NET_ONLY = 1//仅仅只请求接口 （一般是分页和独立页面）
        const val NET_CACHED = 2//先接口，接口成功后更新缓存(一般下来刷新)
    }
}

