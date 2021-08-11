package org.tech.repos.base.lib.resful


import org.tech.repos.base.lib.resful.annotation.*
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Author: xuan
 * Created on 2021/6/29 14:44.
 *
 * Describe:
 */
class MethodParser(
    val baseUrl: String,
    method: Method
) {
    private var domainUrl: String? = null
    private var cacheStrategy:Int = CacheStrategy.NET_ONLY
    private var jsonPost: Boolean = true
    private var streaming: Boolean = false
    private var httpMethod: Int = 0
    private lateinit var relativeUrl: String
    private var returnType: Type? = null
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var parameters: MutableMap<String, Any> = mutableMapOf()

    init {
        //parse method annotations such get,headers,post,baseurl
        parseMethodAnnotations(method)

        //parse method parameters such as path,field
//        parseMethodParameters(method, args)

        //parse method genric return type
        parseMehodReturnType(method)
    }

    /**
     * interface ApiService{
     * @Headers("auth-token:token","accountId:123456")
     * @BaseUrl("https://api.divio.org/as/")
     * @POST("/cities/{province}")
     * @GET("/cities")
     * fun listCities(@Path("province") province:Int,@Field("page") page:Int):HiCall<Object>
     * }
     *
     */
    private fun parseMehodReturnType(method: Method) {
        if (method.returnType != ResfCall::class.java) {
            throw IllegalStateException("mthod ${method.name} must be type of ResfCall.class.")
        }

        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size == 1) { "method ${method.name} can only has one generic return type." }
            returnType = actualTypeArguments[0]
        } else {
            throw IllegalStateException("mthod ${method.name} must has one generic return type.")
        }

    }

    private fun parseMethodParameters(method: Method, args: Array<Any>) {
        val parameterAnnotations = method.parameterAnnotations
        val equals = parameterAnnotations.size == args.size
        require(equals) {
            String.format(
                "argument annnotations count %s dont match expect count %s",
                parameterAnnotations.size,
                args.size
            )
        }

        for (index in args.indices) {
            val annotations = parameterAnnotations[index]
            require(annotations.size <= 1) {
                "field can only has one annnotation: index = $index"
            }
            val value = args[index]
            require(isPrimitive(value)) {
                "8 basic types are supported for now,index = $index"
            }

            val annotation = annotations[0]
            if (annotation is Field) {
                val key = annotation.value
                val value = args[index]
                parameters[key] = value
            } else if (annotation is Path) {
                val replaceName = annotation.value
                val replacement = value.toString()
                if (replaceName != null && replacement != null) {
                    val newRelativeUrl = relativeUrl.replace("{$replaceName}", replacement)
                    relativeUrl = newRelativeUrl
                }
            }else if (annotation is CacheStrategy) {
                cacheStrategy = value as Int
            } else {
                throw IllegalStateException("cannot handle parameter annnotation: " + annotation.javaClass.toString())
            }

        }
    }

    private fun isPrimitive(value: Any): Boolean {
        //String
        if (value.javaClass == String::class.java) {
            return true
        }
        
        if(value.javaClass == List::class.java || value.javaClass == ArrayList::class.java){
            return true
        }

        try {
            //int byte short long boolean char double float
            val field = value.javaClass.getField("TYPE")
            val clazz = field[null] as Class<*>
            if (clazz.isPrimitive) {
                return true
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        return false
    }

    private fun parseMethodAnnotations(method: Method) {
        val annotations = method.annotations
        for (annotation: Annotation in annotations) {
            if (annotation is GET) {
                relativeUrl = annotation.value
                httpMethod = ResfRequest.METHOD.GET
            } else if (annotation is POST) {
                relativeUrl = annotation.value
                httpMethod = ResfRequest.METHOD.POST
                jsonPost = annotation.jsonPost
            } else if (annotation is Headers) {
                val headersArray: Array<out String> = annotation.value
                for (header: String in headersArray) {
                    val colon = header.indexOf(":")
                    check(!(colon == 0 || colon == -1)) {
                        String.format(
                            "@headers value must be in the form [name:vlaue],but found [%s].",
                            header
                        )
                    }
                    val name = header.substring(0, colon)
                    val value = header.substring(colon + 1).trim()
                    headers[name] = value
                }
            } else if (annotation is BaseUrl) {
                domainUrl = annotation.value

            } else if (annotation is CacheStrategy) {
                cacheStrategy = annotation.value
            } else if(annotation is Streaming){
                streaming = annotation.value
            }else {
                throw IllegalStateException("cannot handle method annotation : " + annotation.javaClass.toString())
            }
            
        }

        require(httpMethod == ResfRequest.METHOD.GET || httpMethod == ResfRequest.METHOD.POST) {
            String.format("method %s must has one of GET,POST", method.name)
        }

        if (domainUrl == null) {
            domainUrl = baseUrl
        }
    }

    fun newRequest(method: Method, args: Array<out Any>?): ResfRequest {
        val arguments = args as Array<Any>? ?: arrayOf()
        parseMethodParameters(method,arguments)
        
        var request = ResfRequest()
        request.domainUrl = domainUrl
        request.returnType = returnType
        request.relativeUrl = relativeUrl
        request.parammeters = parameters
        request.headers = headers
        request.httpMethod = httpMethod
        request.jsonPost = jsonPost
        request.streaming = streaming
        request.cacheStrategy = cacheStrategy
        return request
    }


    companion object {
        fun parse(baseUrl: String, method: Method): MethodParser {
            return MethodParser(baseUrl, method)
        }
    }
} 