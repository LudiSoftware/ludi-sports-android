package io.usys.report.ui.ysr.review.organization

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateReviewDBAsync
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Review
import io.usys.report.realm.model.safeUserId
import io.usys.report.ui.ysr.review.engine.calculateAverageRatingScore
import io.usys.report.ui.ysr.review.engine.updateOrgRatingCount
import io.usys.report.ui.ysr.review.engine.updateOrgRatingScore
import io.usys.report.utils.bind

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
        val newOverallAvgScore = calculateAverageRatingScore(currentRatingScore, newRatingScore)
        updateOrgRatingScore(receiverId, newOverallAvgScore)
        // New Count
        val newOverallCount = (currentRatingCount.toInt() + 1)
        updateOrgRatingCount(receiverId, newOverallCount.toString())
        // New Review
        safeUserId { itUserId ->
            Review().apply {
                this.creatorId = itUserId
                this.receiverId = receiverId
                this.comment = commentEditTxt.text.toString()
                this.score = newOverallAvgScore
                this.sportName = SPORT
                this.type = TYPE
            }.fireAddUpdateReviewDBAsync()
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