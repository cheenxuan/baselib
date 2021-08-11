package org.tech.repos.base.lib.resful

/**
 * Author: xuan
 * Created on 2021/6/29 14:19.
 *
 * Describe:callback回调
 */
interface ResfCallback<T> {
    fun onSuccess(response: ResfResponse<T>){
        
    }
    fun onFailed(throwable: Throwable){
        
    }
    
    
}