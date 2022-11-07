package io.usys.report.model

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.usys.report.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.*
import io.usys.report.model.Review.Companion.RATING_COUNT
import io.usys.report.model.Review.Companion.RATING_SCORE
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Review : RealmObject() {

    companion object {
        const val RATING_SCORE = "ratingScore"
        const val RATING_COUNT = "ratingCount"
    }

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String? = getTimeStamp()
    var creatorId: String? = null
    var receiverId: String? = null
    var sportName: String? = null
    var score: String = "0" // score 1 or 10
    var type: String? = null
    var details: String? = null //
    var questions: RealmList<Question>? = null
    var comment: String? = ""
}

// Verified
inline fun getOrgRatingAsync(objId: String, isScore:Boolean=false, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.ORGANIZATIONS).child(objId).child(if (isScore) RATING_SCORE else RATING_COUNT)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}


fun updateOrgRatingScore(orgId:String, newRatingScore:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    updateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, RATING_SCORE, newRatingScore)
}

fun updateOrgRatingCount(orgId:String, newRatingCount:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    updateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, RATING_COUNT, newRatingCount)
}

open class Question: RealmObject() {
    @PrimaryKey
    var id: String = newUUID()
    var question: String? = null
    var choices: RealmList<String>? = null
    var answer: String? = null
}

fun Review.addUpdateInFirebase(): Boolean {
    return addUpdateDBAsync(FireDB.REVIEWS, this.id, this)
}

private fun createReview() {
    val rev = Review()
    rev.apply {
        this.id = newUUID()
        this.score = "4"
        this.details = "us soccer"
        this.questions = RealmList(
            Question().apply { this.question = "Are you satisfied?" },
            Question().apply { this.question = "Does this coach work well with kids?" },
            Question().apply { this.question = "Does this coach work well with parents?" },
            Question().apply { this.question = "Is this coach Chace Zanaty?" })
    }
    addUpdateDBAsync(FireDB.REVIEWS, rev.id, rev)
}

fun createOrgReviewDialog(activity: Activity, org: Organization) : Dialog {

    val TYPE = FireTypes.ORGANIZATIONS
    val SPORT = "soccer"

    val receiverId = org.id
    val currentRatingScore = org.ratingScore
    val currentRatingCount = org.ratingCount

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_review_org_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val commentEditTxt = dialog.bind<EditText>(R.id.reviewEditComment)
    val reviewRatingBar = dialog.bind<RatingBar>(R.id.reviewRatingBar)

    // On Clicks
    val submit = dialog.findViewById(R.id.reviewBtnSubmit) as Button
    val cancel = dialog.findViewById(R.id.reviewBtnCancel) as Button

    // Inner Function for updating overall score and returning it to add Review Obj.
    fun addUpdateNewRating() {
        val newRatingScore: Float = reviewRatingBar.rating
        // New Score
        val newOverallAvgScore = calculateAverageRatingScore(currentRatingScore, newRatingScore).toFloat().roundTo(1)
        updateOrgRatingScore(receiverId, newOverallAvgScore.toString())
        // New Count
        val newOverallCount = (currentRatingCount.toInt() + 1)
        updateOrgRatingCount(receiverId, newOverallCount.toString())
        // New Review
        safeUserId { itUserId ->
            Review().apply {
                this.creatorId = itUserId
                this.receiverId = receiverId
                this.comment = commentEditTxt.text.toString()
                this.score = newOverallAvgScore.toString()
                this.sportName = SPORT
                this.type = TYPE
            }.addUpdateReviewDBAsync()
        }
    }

    submit.setOnClickListener {
        addUpdateNewRating()
        dialog.dismiss()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun calculateAverageRatingScore(overallScore:String?, singleScore:Float) : String {
    // Math for Rating
    val orgScore: Float = overallScore?.toFloat() ?: 0.0F
    val sumScore = orgScore + singleScore
    return (sumScore / 2).toString()
}