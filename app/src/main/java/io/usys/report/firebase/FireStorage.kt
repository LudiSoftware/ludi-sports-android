package io.usys.report.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import io.realm.Realm
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.log
import io.usys.report.utils.toFileInputStream


fun firebaseStorage(): StorageReference {
    return Firebase.storage.reference
}

fun fireStorageRefByPath(path:String): StorageReference {
    return Firebase.storage.reference.child(path)
}

fun Uri?.fireUploadToStorage(context: Context, storagePath: String) {
    // Get File from URI
    val stream = this.toFileInputStream(context) ?: return
    // Get Path to Storage Reference
    val imageRefPath = firebaseStorage().child(storagePath)
    val uploadTask = imageRefPath.putStream(stream)
    uploadTask.addOnFailureListener {
        log("Failed to upload photo!")
    }.addOnSuccessListener { taskSnapshot ->
        log("Success uploading photo!! [ ${taskSnapshot.metadata} ]")
    }
}

inline fun Uri?.fireUploadToStorage(context: Context, storagePath: String, crossinline returnBlock: (UploadTask.TaskSnapshot) -> Unit) {
    // Get File from URI
    val stream = this.toFileInputStream(context) ?: return
    // Get Path to Storage Reference
    val imageRefPath = firebaseStorage().child(storagePath)
    val uploadTask = imageRefPath.putStream(stream)
    uploadTask.addOnFailureListener {
        log("Failed to upload photo!")
    }.addOnSuccessListener { taskSnapshot ->
        log("Success uploading photo!! [ ${taskSnapshot.metadata} ]")
        returnBlock(taskSnapshot)
    }
}

fun Uri?.fireUploadProfileImg(context: Context, id:String) {
    // Get File from URI
    val stream = this.toFileInputStream(context) ?: return
    // Get Path to Storage Reference
    val storagePath = FirePaths.PROFILE_IMAGE_PATH_BY_ID(id)
    val imageRefPath = firebaseStorage().child(storagePath)
    val uploadTask = imageRefPath.putStream(stream)
    uploadTask.addOnFailureListener {
        log("Failed to upload photo!")
    }.addOnSuccessListener { taskSnapshot ->
        log("Success uploading photo!! [ ${taskSnapshot.metadata} ]")
    }
}

inline fun Realm.getUserProfileImgRef(crossinline block: (Uri) -> Unit) {
    this.safeUserId {
        val storagePath = FirePaths.PROFILE_IMAGE_PATH_BY_ID(it)
        firebaseStorage().child(storagePath).getDownloadUrlAsync { uri ->
            log("Got Download URL: $uri")
            block(uri)
        }
    }
}

inline fun StorageReference.getDownloadUrlAsync(crossinline block:(Uri) -> Unit) {
    this.downloadUrl.addOnCompleteListener { itUri ->
        block(itUri.result)
    }
}
