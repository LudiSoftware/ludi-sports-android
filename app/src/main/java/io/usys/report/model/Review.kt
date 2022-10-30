package io.usys.report.model

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.room.PrimaryKey
import io.realm.RealmList
import io.usys.report.R
import io.realm.RealmObject
import io.usys.report.db.FireDB
import io.usys.report.db.addUpdateDB
import io.usys.report.utils.*
import java.util.*

/**
 * Created by ChazzCoin : Feb 2021.
 */
open class Review : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var ownerId: String? = null
    var sportId: String? = null
    var score: Int = 999 // score 1 or 10
    var type: String? = null
    var details: String? = null //
    var questions: RealmList<Question>? = null
}

open class Question: RealmObject() {
    @PrimaryKey
    var id: String? = null
    var question: String? = null
    var choices: RealmList<String>? = null
    var answer: String? = null

    init {
        if (this.id.isNullOrBlank()) {
            this.id = newUUID()
        }
    }
}

fun Review.addUpdateInFirebase(): Boolean {
    return addUpdateDB(FireDB.REVIEWS, this.id.toString(), this)
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
    addUpdateDB(FireDB.REVIEWS, rev.id!!, rev)
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