package io.usys.report.firebase

import io.usys.report.model.*
import io.usys.report.utils.isNullOrEmpty

/**
 * Created by ChazzCoin : October 2022.
 */


/** ADD/UPDATE **/
// unverified
fun <T> T.fireAddUpdateInFirebase(id: String, callbackFunction: ((Boolean, String) -> Unit)?) {
    val collection = this.fireGetNameOfRealmObject() ?: return
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .fairAddOnSuccessCallback(callbackFunction)
    }
}

// Verified
fun fireAddUpdateCoachReviewDBAsync(obj: ReviewTemplate): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(FireTypes.REVIEW_TEMPLATES).child(obj.type).child(obj.id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}

// Verified
fun fireAddUpdateDBAsync(collection: String, id: String, obj: Any): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}

// Verified
fun fireUpdateSingleValueDBAsync(collection: String, id: String, single_attribute: String, single_value: String): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection)
            .child(id)
            .child(single_attribute)
            .setValue(single_value)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}

fun Review.fireAddUpdateReviewDBAsync(block: ((Any) -> Unit)? = null): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(FireTypes.REVIEWS).child(this.receiverId!!).child(this.id)
            .setValue(this).fairAddOnCompleteListener { itReturn ->
                block?.let { block(itReturn) }
            }
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}
fun fireSaveUserToFirebaseAsync(user:User?) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
    }
}
inline fun fireSaveUserToFirebaseAsync(user:User?, crossinline block: (Any?) -> Unit) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
            .fairAddOnCompleteListener { ds ->
                block(ds)
            }
    }
}




