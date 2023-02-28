package io.usys.report.ui.ludi.review.engine

import com.google.firebase.database.DataSnapshot
import io.usys.report.firebase.*
import io.usys.report.realm.model.Review

// Verified
inline fun getOrgRatingAsync(objId: String, isScore:Boolean=false, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.ORGANIZATIONS).child(objId).child(if (isScore) Review.RATING_SCORE else Review.RATING_COUNT)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}
fun updateCoachRatingScore(coachId:String, newRatingScore:String) {
    fireUpdateSingleValueDBAsync(FireTypes.COACHES, coachId, Review.RATING_SCORE, newRatingScore)
}

fun updateCoachReviewCount(coachId:String, newRatingScore:String) {
    fireUpdateSingleValueDBAsync(FireTypes.COACHES, coachId, Review.RATING_COUNT, newRatingScore)
}

fun updateCoachReviewAnswerCount(coachId:String, newRatingScore:String) {
    fireUpdateSingleValueDBAsync(FireTypes.COACHES, coachId, Review.ANSWER_COUNT, newRatingScore)
}

fun updateOrgRatingScore(orgId:String, newRatingScore:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    fireUpdateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, Review.RATING_SCORE, newRatingScore)
}

fun updateOrgRatingCount(orgId:String, newRatingCount:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    fireUpdateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, Review.RATING_COUNT, newRatingCount)
}

fun Review.fireAddUpdateToDB(): Boolean {
    return fireAddUpdateDBAsync(FireTypes.REVIEWS, this.id, this)
}