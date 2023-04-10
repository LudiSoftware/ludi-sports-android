package io.usys.report.utils

import kotlinx.coroutines.*

inline fun main(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
        block(this)
    }
}

inline fun ioLaunch(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
        block(this)
    }
}

fun io() : CoroutineScope {
    return CoroutineScope(Dispatchers.IO + SupervisorJob())
}

/** -> TRIED AND TRUE! <- */
inline fun io(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
        block(this)
    }
}


suspend inline fun retryLoading(maxRetries: Int = 3, retryCondition: () -> Boolean, retryBlock: () -> Unit) {
    var retryCount = 0
    while (retryCount < maxRetries && retryCondition()) {
        retryBlock()
        retryCount++
        delay(5000L) // Wait for 5 seconds before the next retry
    }
}