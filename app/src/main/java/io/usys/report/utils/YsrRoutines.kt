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

/** -> TRIED AND TRUE! <- */
fun ioScope(): CoroutineScope {
    return CoroutineScope(Dispatchers.IO + SupervisorJob())
}

//fun mainScope(): CoroutineScope {
//    return CoroutineScope(Dispatchers.Main + SupervisorJob())
//}