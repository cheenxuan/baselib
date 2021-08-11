package org.tech.repos.base.lib.resful

/**
 * Author: xuan
 * Created on 2021/6/29 14:34.
 *
 * Describe:
 */
interface ResfInterceptor {
    fun intercept(chain: Chain): Boolean

    /**
     * Chain对象会在我们创建拦截器的时候创建
     */
    interface Chain {
        
        val isRequestPeriod:Boolean get() = false
        
        
        fun request(): ResfRequest

        /**
         * response对象 在网络发起之前，是为空的
         */
        fun response(): ResfResponse<*>?
    }
} 