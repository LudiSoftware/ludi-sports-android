package io.usys.report.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.RealmList
import io.usys.report.db.getUserUpdatesFromFirebase
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.model.parseFromFirebaseUser
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.URI
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

inline fun Fragment.pickImageFromGallery(crossinline block: (Uri?) -> Unit) {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            context?.contentResolver?.takePersistableUriPermission(uri, flag)
            block(uri)
        }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/gif")))
}

inline fun Fragment.ysrRequestPermission(permission:String=WRITE_EXTERNAL_STORAGE, crossinline block: (Boolean) -> Unit) {
    // Registers a photo picker activity launcher in single-select mode.
    val perms = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        block(isGranted)
    }
    perms.launch(permission)
}

inline fun Fragment.ysrRequestPermissions(permissions:Array<String>, crossinline block: (Map<String,Boolean>) -> Unit) {
    // Registers a photo picker activity launcher in single-select mode.
    val perms = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        block(it)
    }
    perms.launch(permissions)
}



fun getMasterUser(): User? {
    val realmUser = Session.getCurrentUser()
    val realmUserId = realmUser?.id
    if (!realmUserId.isNullOrEmpty()) return realmUser
    val fireUser = FirebaseAuth.getInstance().currentUser
    return parseFromFirebaseUser(fireUser)
}

inline fun safeUser(block: (User) -> Unit) {
    val user = getMasterUser()
    user?.let {
        if (it.id.isNullOrEmpty()) return@let
        block(it)
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

fun log(msg: Any?) {
    println(msg.toString())
}


@Throws(IOException::class)
fun getFileFromUri(context: Context, uri: Uri): File? {
    val destinationFilename: File? = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
    try {
        context.contentResolver.openInputStream(uri).use { ins ->
            if (ins != null) {
                createFileFromStream(
                    ins,
                    destinationFilename
                )
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
