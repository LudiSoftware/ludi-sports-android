package io.usys.report.model

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import io.realm.RealmList
import io.usys.report.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.FireDB
import io.usys.report.firebase.addUpdateDBAsync
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Review : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var ownerId: String? = null
    var sportId: String? = null
    var score: Int = 999 // score 1 or 10
    var type: String? = null
    var details: String? = null //
    var dateCreated: String? = "" // timestamp
    var questions: RealmList<Question>? = null
}

open class Question: RealmObject() {
    @PrimaryKey
    var id: String? = null
    var question: String? = null
    var choices: RealmList<String>? = null
    var answer: String? = null
}

fun Review.addUpdateInFirebase(): Boolean {
    return addUpdateDBAsync(FireDB.REVIEWS, this.id.toString(), this)
}

private fun createReview() {
    val rev = Review()
    rev.apply {
        this.id = newUUID()
        this.score = 4
        this.details = "us soccer"
        this.questions = RealmList(
            Question().apply { this.question = "Are you satisfied?" },
            Question().apply { this.question = "Does this coach work well with kids?" },
            Question().apply { this.question = "Does this coach work well with parents?" },
            Question().apply { this.question = "Is this coach Chace Zanaty?" })
    }
    addUpdateDBAsync(FireDB.REVIEWS, rev.id!!, rev)
}

fun createReviewDialog(activity: Activity) : Dialog {

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_review_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    // On Clicks
    val submit = dialog.findViewById(R.id.btnReviewSubmit) as Button
    val cancel = dialog.findViewById(R.id.btnReviewCancel) as Button
    submit.setOnClickListener {
        dialog.dismiss()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}