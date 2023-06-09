package io.usys.report.ui.ludi.review.organization

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmModel
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetReviewsByReceiverIdToCallback
import io.usys.report.realm.model.Review
import io.usys.report.ui.views.listAdapters.realmList.loadInCustomAttributes
import io.usys.report.utils.views.bindTextView
import io.usys.report.realm.toRealmList

/**
 * REVIEW LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrgReviewCommentList(context: Context, receiverId:String, itemOnClick: ((View, RealmModel) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    val callBack : ((DataSnapshot?) -> Unit) = { ds ->
        val reviewList = ds?.toRealmList<Review>()
        rv?.loadInCustomAttributes(reviewList, FireTypes.REVIEWS, itemOnClick)
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