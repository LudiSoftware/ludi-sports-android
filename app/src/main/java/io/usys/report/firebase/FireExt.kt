package io.usys.report.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.realm.*
import io.usys.report.realm.toRealmList

fun DataSnapshot?.getString(key: String): String? {
    return this?.child(key)?.getValue(String::class.java)
}
// get Int
fun DataSnapshot?.getInt(key: String): Int? {
    return this?.child(key)?.getValue(Int::class.java)
}
// get Boolean
fun DataSnapshot?.getBoolean(key: String): Boolean? {
    return this?.child(key)?.getValue(Boolean::class.java)
}
// get Long
fun DataSnapshot?.getLong(key: String): Long? {
    return this?.child(key)?.getValue(Long::class.java)
}
// get Double
fun DataSnapshot?.getDouble(key: String): Double? {
    return this?.child(key)?.getValue(Double::class.java)
}
// get Float
fun DataSnapshot?.getFloat(key: String): Float? {
    return this?.child(key)?.getValue(Float::class.java)
}

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
fun Query.singleValueEventCallBack(callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            callbackFunction?.invoke(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            callbackFunction?.invoke(null)
        }
    })
}

inline fun Query.singleValueEvent(crossinline block: (DataSnapshot?) -> Unit) {
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
