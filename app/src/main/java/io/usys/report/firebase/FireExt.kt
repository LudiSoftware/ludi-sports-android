package io.usys.report.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.realm.*
import io.usys.report.realm.toRealmList


// Verified
inline fun firebaseDatabase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}

inline fun firebaseDatabase(collection:String, block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference.child(collection))
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
