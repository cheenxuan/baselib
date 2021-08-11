package org.tech.repos.base.lib.utils

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Author: xuan
 * Created on 2021/7/8 15:19.
 *
 * Describe: 消息总线
 * 另一种实现方式： 通过一堆的反射获取LiveData中的mVersion字段 来控制粘性数据的分发与否。
 */
object DataBus {


    private val eventMap = ConcurrentHashMap<String, StickyLiveData<*>>()
    fun <T> with(eventName: String): StickyLiveData<T> {
        //基于事件名称 订阅、分发消息
        //由一个LiveData只能发送 一种数据类型
        //所以不同的Event事件，需要使用不同的的LiveData实例去分发
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = StickyLiveData<T>(eventName)
            eventMap[eventName] = liveData
        }
        return liveData as StickyLiveData<T>
    }
    
    class StickyLiveData<T>(private val eventName: String) : LiveData<T>() {

        var mStickyData: T? = null
        var mVersion = 0

        fun setStickyData(stickyData: T) {
            mStickyData = stickyData
            setValue(stickyData)
            //只能在主线程发送数据
        }

        fun postStickyData(stickyData: T) {
            mStickyData = stickyData
            postValue(stickyData)
        }

        override fun setValue(value: T) {
            mVersion++
            super.setValue(value)
        }

        override fun postValue(value: T) {
            mVersion++
            super.postValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observeSticky(owner, false, observer)
        }

        fun observeSticky(owner: LifecycleOwner, sticky: Boolean, observer: Observer<in T>) {
            //允许指定注册的观察者 是否需要关心粘性事件
            //sticky=true 如果之前存在已经发送的数据，那么这个observer会受到之前的粘性事件消息
            owner.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                //监听宿主发生销毁事件，主动把livedata移除掉
                if (event == Lifecycle.Event.ON_DESTROY) {
                    eventMap.remove(eventName)
                }
            })
            super.observe(owner, StickyObserver(this, sticky, observer))

        }
    }

    class StickyObserver<T>(
        val stickyLiveData: StickyLiveData<T>,
        val sticky: Boolean,
        val observer: Observer<in T>
    ) : Observer<T> {
        //lasrVersion和liveData的version对齐的原因，就是为控制粘性事件的分发
        //默认情况下，sticky!=true只能接收到注册之后发送的消息，如果要接收粘性事件，则sticky=true
        private var lastVersion = stickyLiveData.mVersion
        override fun onChanged(t: T) {
            if (lastVersion >= stickyLiveData.mVersion) {
                //则说明 stickyLiveData没有更新的数据需要发送
                if (sticky && stickyLiveData.mStickyData != null) {
                    observer.onChanged(stickyLiveData.mStickyData)
                }
                return
            }

            lastVersion = stickyLiveData.mVersion
            observer.onChanged(t)
        }

    }
}