package org.tech.repos.base.lib.resful.annotation

/**
 * Author: xuan
 * Created on 2021/6/29 11:27.
 *
 * Describe:
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers(vararg val value:String) 