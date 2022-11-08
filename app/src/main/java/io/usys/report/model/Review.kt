package io.usys.report.model

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.*
import io.usys.report.model.Review.Companion.ANSWER_COUNT
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
        const val ANSWER_COUNT = "reviewAnswerCount"
    }

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String? = getTimeStamp()
    var creatorId: String? = null
    var receiverId: String? = null
    var sportName: String? = null
    var score: String = "0" // score 1 or 5
    var type: String? = null
    var details: String? = null //
    var questions: RealmList<Question>? = null
    var comment: String? = ""
}

open class ReviewTemplate : RealmObject() {

    companion object {
        const val UNASSIGNED = "unassigned"
        const val RATING_SCORE = "ratingScore"
        const val RATING_COUNT = "ratingCount"
    }

    @PrimaryKey
    var id: String = "master"
    var type: String = UNASSIGNED
    var dateCreated: String? = getTimeStamp()
    var sportName: String = ""
    var topScore: String = "5" // score 1 or 5
    var lowScore: String = "0"
    var details: String = "" //
    var questions: RealmList<Question>? = null
    var commentsEnabled: Boolean = false
}

fun getQuestionScore(letter:String?) : String {
    return when (letter) {
        Question.A -> "5"
        Question.B -> "4"
        Question.C -> "3"
        Question.D -> "2"
        else -> "0"
    }
}

fun createReviewTemplate() {
    val revTemplate = ReviewTemplate()
    revTemplate.apply {
        this.sportName = "soccer"
        this.type = FireTypes.COACHES
        this.details = "us soccer based review system for coaches"
        this.questions = RealmList(
            createBaseQuestion(),
            createBaseQuestion("Does this coach work well with kids?"),
            createBaseQuestion("Does this coach work well with parents?"),
            createBaseQuestion("Is this coach Chace Zanaty?"))
    }
    addUpdateCoachReviewDBAsync(revTemplate)
}

open class Question: RealmObject() {
    companion object {
        const val A = "a"
        const val B = "b"
        const val C = "c"
        const val D = "d"
    }
    @PrimaryKey
    var id: String = newUUID()
    var question: String = ""
    var choiceA: String = ""
    var choiceB: String = ""
    var choiceC: String = ""
    var choiceD: String = ""
    var finalScore: String = ""
    var answer: String? = null
}

fun createBaseQuestion(question:String?=null) : Question {
    return Question().apply {
        this.question = if (!question.isNullOrEmpty()) question.toString() else "This is a review question about the coach?"
        this.choiceA = "Weighted option 5"
        this.choiceB = "Weighted option 4"
        this.choiceC = "Weighted option 3"
        this.choiceD = "Weighted option 2"
    }
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
fun updateCoachRatingScore(coachId:String, newRatingScore:String) {
    updateSingleValueDBAsync(FireTypes.COACHES, coachId, RATING_SCORE, newRatingScore)
}

fun updateCoachReviewCount(coachId:String, newRatingScore:String) {
    updateSingleValueDBAsync(FireTypes.COACHES, coachId, RATING_COUNT, newRatingScore)
}

fun updateCoachReviewAnswerCount(coachId:String, newRatingScore:String) {
    updateSingleValueDBAsync(FireTypes.COACHES, coachId, ANSWER_COUNT, newRatingScore)
}

fun updateOrgRatingScore(orgId:String, newRatingScore:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    updateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, RATING_SCORE, newRatingScore)
}

fun updateOrgRatingCount(orgId:String, newRatingCount:String) {
    //busa = "54d9d63d-52bb-4503-95ca-8bda462e0f9a"
    updateSingleValueDBAsync(FireTypes.ORGANIZATIONS, orgId, RATING_COUNT, newRatingCount)
}



fun Review.addUpdateInFirebase(): Boolean {
    return addUpdateDBAsync(FireDB.REVIEWS, this.id, this)
}

fun createOrgReviewDialog2(activity: Activity) : Dialog {

    val TYPE = FireTypes.ORGANIZATIONS
    val SPORT = "soccer"

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_review_coach_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    val commentEditTxt = dialog.bind<EditText>(R.id.reviewEditComment)
//    val reviewRatingBar = dialog.bind<RatingBar>(R.id.reviewRatingBar)

    // On Clicks
    val submit = dialog.findViewById(R.id.reviewBtnSubmit) as Button
    val cancel = dialog.findViewById(R.id.reviewBtnCancel) as Button

    submit.setOnClickListener {
        dialog.dismiss()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
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