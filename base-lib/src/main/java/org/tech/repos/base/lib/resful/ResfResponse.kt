package org.tech.repos.base.lib.resful

/**
 * Author: xuan
 * Created on 2021/6/29 14:21.
 *
 * Describe: 响应报文
 */
open class ResfResponse<T> {
    companion object {
        val ERROR = "10000"
        val PARSE_ERROR = "1001"
        val SUCCESS = "00"
        val CACHE_SUCCESS = "304"
    }

    var rawData: String? = null//原始数据
    var code = "00"
    var data: T? = null//业务数据
    var errorData: Map<String, String>? = null//错误状态下的数据
    var msg: String? = null //错误信息
    var failCode: String? = null //业务错误码
    var error: Throwable? = null


    fun successful(): Boolean {
        return code == SUCCESS || code == CACHE_SUCCESS
    }

}