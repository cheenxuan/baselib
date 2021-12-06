package org.tech.repos.base.lib.resful

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * Author: xuan
 * Created on 2021/6/29 14:37.
 *
 * Describe:
 */
open class Restful constructor(val baseUrl: String, val callFactory: ResfCall.Facetory) {
    private var interceptors: MutableList<ResfInterceptor> = mutableListOf()
    private var methodService: ConcurrentHashMap<Method, MethodParser> = ConcurrentHashMap()
    private var scheduler: Scheduler

    init {
        scheduler = Scheduler(callFactory, interceptors)
    }

    fun addInterceptor(interceptor: ResfInterceptor) {
        interceptors.add(interceptor)
    }

    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service), object : InvocationHandler {
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
                    var methodParser = methodService[method]
                    if (methodParser == null) {
                        methodParser = MethodParser.parse(baseUrl, method)
                        if (!methodParser.getstreaming()) methodService[method] = methodParser
                    }
                    val request = methodParser.newRequest(method, args)
                    //callFactory.newCall(request)
                    return scheduler.newCall(request)
                }
            }
        ) as T
    }
}