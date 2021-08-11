package org.tech.repos.base.lib.resful

import java.lang.reflect.Type

/**
 * Author: xuan
 * Created on 2021/6/30 10:19.
 *
 * Describe:
 */
interface ResfConvert {
    fun <T> convert(rawData:String,dataType:Type): ResfResponse<T>
} 