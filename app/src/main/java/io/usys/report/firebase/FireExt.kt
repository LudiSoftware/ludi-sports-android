package io.usys.report.firebase

import android.content.Context
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.realm.RealmList
import io.usys.report.model.safeUserId
import io.usys.report.utils.log
import io.usys.report.utils.toFileInputStream
import io.usys.report.utils.toRealmList
import java.lang.Exception

fun firebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

fun masterFirebaseLogoutAsync(context: Context): Task<Void> {
    return AuthUI.getInstance().signOut(context)
}

fun firebaseStorage(): StorageReference {
    return Firebase.storage.reference
}

fun fireStorageRefByPath(path:String): StorageReference {
    return Firebase.storage.reference.child(path)
}

fun Uri?.uploadToFirebaseStorage(context: Context, storagePath: String) {
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

// Verified
inline fun firebaseDatabase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}

inline fun StorageReference.getDownloadUrlAsync(crossinline block:(Uri) -> Unit) {
    this.downloadUrl.addOnCompleteListener { itUri ->
        block(itUri.result)
    }
}

fun <TResult> Task<TResult>.fairAddOnCompleteListener(block: (TResult) -> Unit): Task<TResult> {
    this.addOnCompleteListener {
        block(it.result)
    }
    return this
}

fun <TResult> Task<TResult>.fairAddFailureListener(block: (Exception) -> Unit): Task<TResult> {
    this.addOnFailureListener {
        block(it)
    }
    return this
}

// Verified
fun Query.fairAddListenerForSingleValueEvent(callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            callbackFunction?.invoke(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            callbackFunction?.invoke(null)
        }
    })
}

@JvmName("fairAddYsrListenerForSingleValueEvent1")
fun Query.fairAddListenerForSingleValueEvent(block: (DataSnapshot?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}

inline fun <reified T> Query.fairAddParsedListenerForSingleValueEvent(crossinline block: (RealmList<T>?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot.toRealmList())
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}

fun <TResult> Task<TResult>.fairAddOnSuccessCallback(callbackFunction: ((Boolean, String) -> Unit)?) {
    this.addOnSuccessListener {
        callbackFunction?.invoke(true, "success")
    }.addOnCompleteListener {
        callbackFunction?.invoke(true, "complete")
    }.addOnFailureListener {
        callbackFunction?.invoke(false, "failure")
    }
}
