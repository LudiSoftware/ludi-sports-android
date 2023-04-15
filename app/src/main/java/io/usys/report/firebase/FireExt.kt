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


/** Observe a firebase value event */
fun Query.observeValueEvent(block: (DataSnapshot?, ValueEventListener) -> Unit): ValueEventListener {
    val listener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot, this)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            block(null, this)
        }
    }
    this.addValueEventListener(listener)
    return listener
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
