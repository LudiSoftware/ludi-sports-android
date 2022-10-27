package io.usys.report.utils

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.realm.RealmList
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

/**
 * Created by ChazzCoin : December 2019.
 */

inline fun tryCatch(block:() -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        log("Safe Extension Caught Exception: $e")
    }
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

fun newUUID(): String {
    return UUID.randomUUID().toString()
}

fun log(msg: String) {
    println(msg)
}

/**
 * Make sure to add any new fragments to Navigation XML!
 */
fun Fragment.toFragment(fragId: Int, bundle: Bundle = bundleOf()) {
    this.findNavController().navigate(fragId, bundle)
}

fun Fragment.getArg(argName: String): String? {
    return arguments?.getString(argName)
}