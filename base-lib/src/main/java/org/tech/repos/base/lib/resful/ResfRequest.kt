package org.tech.repos.base.lib.resful

import android.text.TextUtils
import androidx.annotation.IntDef
import org.tech.repos.base.lib.resful.annotation.CacheStrategy
import java.lang.IllegalStateException
import java.lang.reflect.Type
import java.net.URLEncoder

open class ResfRequest {
    

    @METHOD
    var httpMethod: Int = 0
    var headers: MutableMap<String, String>? = null
    var parammeters: MutableMap<String, Any>? = null
    var domainUrl: String? = null
    var relativeUrl: String? = null
    var returnType: Type? = null
    var jsonPost: Boolean = true
    var cacheStrategy:Int = CacheStrategy.NET_ONLY
    private var cacheStrategyKey: String=""
    var streaming:Boolean = false

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
        }
    }

    /**
     * @return 返回的是请求的完整url
     */
    fun endPointUrl(): String {
        if (relativeUrl == null) {
            throw IllegalStateException("relative url must not be null.")
        }

        if (relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl
        }else{
            return relativeUrl!!
        }

//        val indexOf = domainUrl!!.indexOf("/")
//        if(indexOf != 0) return domainUrl!!
//        return domainUrl!!.substring(0, indexOf) + relativeUrl
    }

    fun addHeader(name: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        
        headers!![name] = value
    }

    fun getCacheKey(): String {

        if (!TextUtils.isEmpty(cacheStrategyKey)) {
            return cacheStrategyKey
        }
        
        val builder = StringBuilder()
        val endUrl = endPointUrl()
        
        builder.append(endUrl)
        if (endUrl.indexOf("?") > 0 || endUrl.indexOf("&") > 0) {
            builder.append("&")
        } else {
            builder.append("?")
        }

        if (parammeters != null) {
            for ((key, value) in parammeters!!) {
                try {
                    val encodeValue = URLEncoder.encode(value.toString(), "UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                } catch (e: Exception) {
                    //ignore
                }
            }
            builder.deleteCharAt(builder.length - 1)
            cacheStrategyKey = builder.toString()
        } else {
            cacheStrategyKey = endUrl
        }
        
        return cacheStrategyKey
        
    }

}
