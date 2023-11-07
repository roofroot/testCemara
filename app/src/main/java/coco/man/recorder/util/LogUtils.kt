package coco.man.recorder.util

import android.util.Log
import coco.man.recorder.BuildConfig

object LogUtils {
    var className //类名
            : String? = null
    var methodName //方法名
            : String? = null
    var lineNumber //行数
            = 0
    val isDebuggable: Boolean
        get() = BuildConfig.is_debug

    private fun createLog(log: String): String {
        val buffer = StringBuffer()
        buffer.append(methodName)
        buffer.append("(").append(className).append(":").append(lineNumber).append(")")
        buffer.append(log)
        return buffer.toString()
    }

    private fun getMethodNames(sElements: Array<StackTraceElement>) {
        className = sElements[1].fileName
        methodName = sElements[1].methodName
        lineNumber = sElements[1].lineNumber
    }

    fun e(message: String) {
        if (!isDebuggable) return

        // Throwable instance must be created before any methods
        getMethodNames(Throwable().stackTrace)
        Log.e(className, createLog(message))
    }

    fun i(message: String) {
        if (!isDebuggable) return
        getMethodNames(Throwable().stackTrace)
        Log.i(className, createLog(message))
    }

    fun d(message: String) {
        if (!isDebuggable) return
        getMethodNames(Throwable().stackTrace)
        Log.d(className, createLog(message))
    }

    fun v(message: String) {
        if (!isDebuggable) return
        getMethodNames(Throwable().stackTrace)
        Log.v(className, createLog(message))
    }

    fun w(message: String) {
        if (!isDebuggable) return
        getMethodNames(Throwable().stackTrace)
        Log.w(className, createLog(message))
    }

    fun wtf(message: String) {
        if (!isDebuggable) return
        getMethodNames(Throwable().stackTrace)
        Log.wtf(className, createLog(message))
    }
}