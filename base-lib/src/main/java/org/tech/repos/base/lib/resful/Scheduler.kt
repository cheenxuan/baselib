package org.tech.repos.base.lib.resful

import org.tech.repos.base.lib.cache.ExtStorage
import org.tech.repos.base.lib.executor.GlobalExecutor
import org.tech.repos.base.lib.resful.annotation.CacheStrategy
import org.tech.repos.base.lib.utils.MainHandler

/**
 * Author: xuan
 * Created on 2021/6/29 17:00.
 *
 * Describe:代理CallFactory创建出来的Call对象,从而实现拦截器的派发
 */
class Scheduler(
    val callFactory: ResfCall.Facetory,
    val interceptors: MutableList<ResfInterceptor>
) {
    fun newCall(request: ResfRequest): ResfCall<*> {
        val newCall: ResfCall<*> = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    internal inner class ProxyCall<T>(
        val delegate: ResfCall<T>,
        val request: ResfRequest
    ) : ResfCall<T> {
        override fun execute(): ResfResponse<T> {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                val cacheResponse = readCache<T>()
                if (cacheResponse != null) {
                    return cacheResponse
                }
            }
            val response = delegate.execute()
            saveCacheIfNeeded(response)

            dispatchInterceptor(request, response)
            return response
        }

        override fun enqueue(callback: ResfCallback<T>) {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                GlobalExecutor.execute(runnable = Runnable {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        //抛到主线程中
                        MainHandler.sendAtFrontOfQueue(runnable = Runnable {
                            callback.onSuccess(cacheResponse)
                        })
                    }
                })
            }

            delegate.enqueue(object : ResfCallback<T> {
                override fun onSuccess(response: ResfResponse<T>) {
                    dispatchInterceptor(request, response)
                    saveCacheIfNeeded(response)
                    if (callback != null) callback.onSuccess(response)
                }

                override fun onFailed(throwable: Throwable) {
                    val response = ResfResponse<T>()
                    response.code = ResfResponse.ERROR
                    response.error = throwable
                    dispatchInterceptor(request, response)
                    if (callback != null) callback.onFailed(throwable)
                }
            })
        }

        private fun saveCacheIfNeeded(response: ResfResponse<T>) {
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST || request.cacheStrategy == CacheStrategy.NET_CACHED) {
                if (response.data != null) {
                    GlobalExecutor.execute(runnable = Runnable {
                        ExtStorage.saveCache(request.getCacheKey(), response.data)
                    })
                }
            }
        }

        private fun <T> readCache(): ResfResponse<T> {
            //historage查询缓存  需要提供一个cache key
            //1.requst 拼接参数
            val cacheKey = request.getCacheKey()
            val cache = ExtStorage.getCache<T>(cacheKey)

            val cacheResponse = ResfResponse<T>()
            cacheResponse.data = cache
            cacheResponse.code = ResfResponse.CACHE_SUCCESS
            cacheResponse.msg = "缓存获取成功"
            return cacheResponse
        }

        private fun dispatchInterceptor(request: ResfRequest, response: ResfResponse<T>?) {
            InterceptorChain(request, response).dispatch()
        }

        internal inner class InterceptorChain(
            val request: ResfRequest,
            val response: ResfResponse<T>?
        ) : ResfInterceptor.Chain {
            //代表的是 分发的第几个拦截器
            var callIndex: Int = 0

            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): ResfRequest = request

            override fun response(): ResfResponse<*>? = response

            fun dispatch() {
                val interceptor = interceptors[callIndex]
                val intercept = interceptor.intercept(this)
                callIndex++
                if (!intercept && callIndex < interceptors.size) {
                    dispatch()
                }
            }
        }

    }
} 