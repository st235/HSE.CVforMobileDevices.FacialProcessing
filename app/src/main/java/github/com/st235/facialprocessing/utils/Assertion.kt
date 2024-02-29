package github.com.st235.facialprocessing.utils

import android.os.Build
import android.os.Looper

object Assertion {

    class AssertionException(message: String): RuntimeException(message)

    fun assertOnMainThread() {
        val isMainThread = isMainThread()
        if (!isMainThread) {
            throw AssertionException("Code was expected to run on the main thread, though run on" +
                    " ${Thread.currentThread().name}.")
        }
    }

    fun assertOnWorkerThread() {
        val isMainThread = isMainThread()
        if (isMainThread) {
            throw AssertionException("Code was expected to run on a worker thread, though run on main.")
        }
    }

    private fun isMainThread(): Boolean {
        val mainLooper = Looper.getMainLooper()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLooper.isCurrentThread
        } else {
            val currentThread = Thread.currentThread()
            mainLooper.thread == currentThread
        }
    }
}