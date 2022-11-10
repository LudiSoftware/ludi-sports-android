package io.usys.report.ui.ysr.review.organization

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetReviewsByReceiverIdToCallback
import io.usys.report.model.Review
import io.usys.report.ui.ysr.loadInRealmList
import io.usys.report.utils.bindTextView
import io.usys.report.utils.toRealmList

/**
 * REVIEW LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrgReviewCommentList(context: Context, receiverId:String, itemOnClick: ((View, RealmObject) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    val callBack : ((DataSnapshot?) -> Unit) = { ds ->
        val reviewList = ds?.toRealmList<Review>()
        rv?.loadInRealmList(reviewList, context, FireTypes.REVIEWS, itemOnClick)
    }
    fireGetReviewsByReceiverIdToCallback(receiverId, callBack)
}

class OrgReviewCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var cardReviewTxtDateCreated = itemView.bindTextView(R.id.cardReviewTxtDateCreated)
    private var cardReviewTxtComment = itemView.bindTextView(R.id.cardReviewTxtComment)

    fun bind(review: Review?) {
        review?.let {
            cardReviewTxtDateCreated?.text = it.dateCreated
            cardReviewTxtComment?.text = it.comment
        }
    }
}