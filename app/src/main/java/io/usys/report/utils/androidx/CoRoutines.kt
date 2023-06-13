package io.usys.report.utils.androidx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

fun CoroutineScope.launchWithDelay(
    delayMillis: Long = 0L,
    block: suspend CoroutineScope.() -> Unit
): Job = launch {
    delay(delayMillis)
    block()
}
fun <T> Flow<T>.retry(
    maxRetries: Int,
    delayMillis: Long,
    predicate: suspend (Throwable) -> Boolean = { true }
): Flow<T> = flow {
    var retryCount = 0
    var lastError: Throwable? = null
    collect { value ->
        emit(value)
    }
    retryLoop@ while (retryCount < maxRetries) {
        try {
            delay(delayMillis)
            collect { value ->
                emit(value)
            }
            break@retryLoop
        } catch (e: Throwable) {
            lastError = e
            if (!predicate(e)) {
                break@retryLoop
            }
        }
        retryCount++
    }
    lastError?.let { throw it }
}

fun <T> Flow<T>.timeout(timeoutMillis: Long): Flow<T> = flow {
    withTimeoutOrNull(timeoutMillis) {
        collect { value ->
            emit(value)
        }
    }
}

fun <T> Flow<T>.distinctUntilChanged(): Flow<T> = flow {
    var previousValue: T? = null
    collect { value ->
        if (value != previousValue) {
            emit(value)
            previousValue = value
        }
    }
}
fun <T> Flow<T>.buffer(capacity: Int): Flow<T> = channelFlow {
    val buffer = Channel<T>(capacity = capacity)
    val collector = launch {
        collect { value ->
            buffer.send(value)
        }
        buffer.close()
    }
    val consumer = launch {
        for (value in buffer) {
            send(value)
        }
    }
    collector.join()
    consumer.join()
}
