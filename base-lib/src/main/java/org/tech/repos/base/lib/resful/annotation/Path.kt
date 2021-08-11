package org.tech.repos.base.lib.resful.annotation

/**
 * Author: xuan
 * Created on 2021/6/29 11:29.
 *
 * Describe:
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Path(val value:String)