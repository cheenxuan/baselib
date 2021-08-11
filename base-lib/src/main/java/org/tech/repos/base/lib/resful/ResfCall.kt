package org.tech.repos.base.lib.resful

import java.io.IOException

/**
 * Author: xuan
 * Created on 2021/6/29 14:25.
 *
 * Describe:
 */
interface ResfCall<T> {
    
    @Throws(IOException::class)
    fun execute(): ResfResponse<T>
    
    fun enqueue(callback: ResfCallback<T>)

    interface Facetory {
        fun newCall(request: ResfRequest): ResfCall<*>
    }
}