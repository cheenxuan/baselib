package org.tech.repos.base.lib.executor

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.IntRange
import org.tech.repos.base.lib.log.VLog
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock


/**
 * Author: xuan
 * Created on 2021/6/11 15:10.
 *
 * Describe:
 * 支持按任务的优先级去执行，
 * 支持线程池暂停、恢复(批量文件下载、上传) 
 * 异步结果主动回调主线程
 * todo 线程池能力监控，耗时任务检测，定时，延迟
 */
object GlobalExecutor {
    private val TAG = "GlobalExecutor"
    private var isPause: Boolean = false
    private var globalExecutor: ThreadPoolExecutor
    private var lock: ReentrantLock
    private var pauseCondition: Condition
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    init {
        lock = ReentrantLock()
        pauseCondition = lock.newCondition()

        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()
        
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS
        val seq = AtomicLong()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = "global-executor-${seq.getAndIncrement()}"
            return@ThreadFactory thread
        }

        globalExecutor = object : ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            unit,
            blockingQueue as BlockingQueue<Runnable>,
            threadFactory
        ) {
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if (isPause) {
                    lock.lock()
                    try {
                        pauseCondition.await()
                    } finally {
                        lock.unlock()
                    }
                }

            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                super.afterExecute(r, t)
                //监控线程池耗时任务，线程创建数量，正在运行的数量
                Log.e(TAG, "afterExecute: 已执行完的任务的优先级是： ${(r as PriorityRunnable).priority}")
            }
        }

    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        globalExecutor.execute(PriorityRunnable(priority, runnable))
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0,runnable: Callable<*>) {
        globalExecutor.execute(PriorityRunnable(priority, runnable))
    }


    abstract class Callable<T> : Runnable {
        override fun run() {
            mainHandler.post { onPrepare() }

            val t: T = onBackground()

            mainHandler.post { onCompleted(t) }
        }

        open fun onPrepare() {
            //任务执行前
        }

        abstract fun onBackground(): T
        abstract fun onCompleted(t: T)

    }

    class PriorityRunnable(val priority: Int, val runnable: Runnable) : Runnable,
        Comparable<PriorityRunnable> {

        override fun compareTo(other: PriorityRunnable): Int {
            return if (this.priority < other.priority) 1 else if (this.priority > other.priority) -1 else 0
        }

        override fun run() {
            runnable.run()
        }

    }
    
    @Synchronized
    fun shutDownNow(){
        globalExecutor.shutdownNow()
    }

    @Synchronized
    fun pause() {
        isPause = true
        VLog.d("pause: GlobalExecutor is pause")
    }

    @Synchronized
    fun resume() {
        isPause = false
        lock.lock()
        try {
            pauseCondition.signalAll()
        } finally {
            lock.unlock()
        }
        VLog.d("resume: GlobalExecutor is resume")
    }
} 