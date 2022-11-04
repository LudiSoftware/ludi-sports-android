package io.usys.report.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import io.realm.RealmList
import kotlinx.coroutines.*
import java.io.*
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
fun getClassName(className: Any): String? {
    tryCatch {
        return className::class.simpleName
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
        is Collection<*> -> { if (this.isEmpty() || this.size <= 0) return true }
        is RealmList<*> -> { if (this.isEmpty() || this.size <= 0) return true }
    }
    return false
}

inline fun <T> T?.fairSafe(block: (T) -> Unit) {
    this?.let {
        block(this)
    }
}

fun String.toUri() : Uri? {
    val uriString = Uri.parse(this)
    return if (uriString.isNullOrEmpty()) null else uriString
}

fun newUUID(): String {
    return UUID.randomUUID().toString()
}

fun log(msg: Any?) {
    println(msg.toString())
}

fun Uri?.toFile(context: Context): File? {
    if (this.isNullOrEmpty()) return null
    return this?.let { getFileFromUri(context, it) }
}

fun Uri?.toFileInputStream(context: Context): FileInputStream? {
    if (this.isNullOrEmpty()) return null
    val file = this?.let { getFileFromUri(context, it) }
    return FileInputStream(file)
}

@Throws(IOException::class)
fun getFileFromUri(context: Context, uri: Uri): File? {
    val destinationFilename: File? = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
    try {
        context.contentResolver.openInputStream(uri).use { ins ->
            if (ins != null) {
                createFileFromStream(ins, destinationFilename)
            }
        }
    } catch (ex: Exception) {
        log("Save File")
        ex.printStackTrace()
    }
    return destinationFilename
}

fun createFileFromStream(ins: InputStream, destination: File?) {
    try {
        FileOutputStream(destination).use { os ->
            val buffer = ByteArray(4096)
            var length: Int
            while (ins.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
            os.flush()
        }
    } catch (ex: Exception) {
        log("Save File")
        ex.printStackTrace()
    }
}

private fun queryName(context: Context, uri: Uri): String {
    val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name: String = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}
