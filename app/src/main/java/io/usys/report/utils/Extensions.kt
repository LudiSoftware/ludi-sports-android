package io.usys.report.utils

import android.app.Activity
import android.content.Intent
import io.realm.RealmList
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.reflect.KProperty1

/**
 * Created by ChazzCoin : October 2022.
 */

inline fun tryCatch(block:() -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        log("Safe Extension Caught Exception: $e")
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.cast(): T? {
    tryCatch {
        return this as? T
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <R> Any.getAttribute(propertyName: String): R? {
    tryCatch {
        val property = this::class.members
            // don't cast here to <Any, R>, it would succeed silently
            .first { it.name == propertyName } as KProperty1<Any, *>
        // force a invalid cast exception if incorrect type here
        return property.get(this) as R
    }
    return null
}

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

fun Any?.isNullOrEmpty() : Boolean {
    if (this == null) return true
    when (this) {
        is String -> { if (this.isEmpty() || this.isBlank()) return true }
        is Collection<*> -> { if (this.isEmpty()) return true }
        is RealmList<*> -> { if (this.isEmpty()) return true }
    }
    return false
}

inline fun <T> T?.safe(block: (T) -> Unit) {
    this?.let {
        block(this)
    }
}

inline fun <reified TO> Activity.launchActivity() {
    startActivity(Intent(this, TO::class.java))
}

fun newUUID(): String {
    return UUID.randomUUID().toString()
}

fun log(msg: String) {
    println(msg)
}

