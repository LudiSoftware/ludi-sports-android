package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import io.usys.report.realm.model.Review
import io.usys.report.realm.model.ReviewTemplate

/** Review Template */

inline fun fireGetReviewTemplateAsync(templateType:String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.REVIEW_TEMPLATES).child(templateType)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

inline fun fireGetReviewTemplateQuestionsAsync(templateType:String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.REVIEW_TEMPLATES).child(templateType).child("master").child("questions")
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/**
 * REVIEWS
 */

// -> Save
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

// -> Save
fun fireAddUpdateCoachReviewDBAsync(obj: ReviewTemplate): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(FireTypes.REVIEW_TEMPLATES).child(obj.type!!).child(obj.id)
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

// -> Get
fun fireGetReviewsByReceiverIdToCallback(receiverId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(FireTypes.REVIEWS).child(receiverId)
            .fairAddListenerForSingleValueEvent(callbackFunction)
    }
}