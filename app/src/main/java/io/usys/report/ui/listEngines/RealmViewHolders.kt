package io.usys.report.ui

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.*
import io.usys.report.model.*
import io.usys.report.utils.*

/**
 * This Class will 'route' the RecyclerView to the correct ViewHolder based on its 'type'.
 */
class RouterViewHolder(itemView: View, var type:String) : RecyclerView.ViewHolder(itemView) {

    fun bind(obj: RealmObject) {
        when (type) {
            FireDB.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
            FireDB.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireDB.COACHES -> return CoachViewHolder(itemView).bind(obj as? Coach)
            FireDB.REVIEWS -> return ReviewViewHolder(itemView).bind(obj as? Review)
        }
    }
}
/**
 * SPORT LIST VIEW CONTROLS
 */

fun RecyclerView?.setupSportList(context: Context, onClick: ((View, RealmObject) -> Unit)?) {
    // Load Organizations by Sport
    val rv = this
    var sportList: RealmList<Sport>? = RealmList()
    getBaseObjects<Sport>(FireTypes.SPORTS) {
        executeRealm {
            sportList = this ?: RealmList()
            sportList.addToSession()
        }
        rv?.loadInRealmList(sportList, context, FireTypes.SPORTS, onClick)
    }
}

class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemSportName = itemView.bindTextView(R.id.cardSportTxtSportName)

    fun bind(sport: Sport?) {
        sport?.let { txtItemSportName?.text = it.name }
    }
}

/**
 * ORGANIZATION LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrganizationList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    var organizationList: RealmList<Organization>?
    getOrderByEqualToAsync(FireTypes.ORGANIZATIONS, Organization.ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
        organizationList = this?.toRealmList()
        rv?.loadInRealmList(organizationList, context, FireTypes.ORGANIZATIONS, onClick)
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.bindTextView(R.id.cardOrgTxtName)
    var txtWebsite = itemView.bindTextView(R.id.cardOrgTxtWebsite)
    var txtLeague = itemView.bindTextView(R.id.cardOrgTxtLeague)
    var imgOrganization: ImageView = itemView.bind(R.id.cardOrgImgOrg)
    var includeReviewCard: FrameLayout = itemView.findViewById(R.id.includeReviewCard)

    var cardRatingBar: RatingBar = includeReviewCard.bind(R.id.cardRatingBar)
    var cardRatingBarScore: TextView = includeReviewCard.bind(R.id.cardRatingTxtScore)
    var cardRatingBarCount: TextView = includeReviewCard.bind(R.id.cardRatingTxtCount)

    fun bind(org: Organization?) {
        txtOrgName?.text = org?.name
        txtWebsite?.text = org?.websiteUrl ?: "www.usysr.io"
        txtLeague?.text = org?.leagueIds?.first() ?: "Alabama State League"
        cardRatingBar.rating = org?.ratingScore?.toFloat() ?: 0.0F
        cardRatingBarScore.text = org?.ratingScore
        cardRatingBarCount.text = "${org?.ratingCount} Reviews"
    }
}

/**
 * COACH LIST VIEW CONTROLS
 */

fun RecyclerView?.setupCoachList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Coaches by Organization.id
    val rv = this
    var coachesList: RealmList<Coach>?
    getOrderByEqualToAsync(FireTypes.COACHES, Coach.ORDER_BY_ORGANIZATION, realmObjectArg?.cast<Organization>()?.id ?: return) {
        coachesList = this?.toRealmList()
        rv?.loadInRealmList(coachesList, context, FireTypes.COACHES, onClick)
    }
}

class CoachViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtItemSpotName = itemView.bindTextView(R.id.cardSportTxtSportName)

    fun bind(coach: Coach?) {
        coach?.let {
            txtItemSpotName?.text = it.name
        }
    }
}

/**
 * REVIEW LIST VIEW CONTROLS
 */

fun RecyclerView?.setupReviewList(context: Context, receiverId:String, itemOnClick: ((View, RealmObject) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    val callBack : ((DataSnapshot?) -> Unit) = { ds ->
        val reviewList = ds?.toRealmList<Review>()
        rv?.loadInRealmList(reviewList, context, FireTypes.REVIEWS, itemOnClick)
    }
    getReviewsByReceiverIdToCallback(receiverId, callBack)
}

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var cardReviewTxtDateCreated = itemView.bindTextView(R.id.cardReviewTxtDateCreated)
    private var cardReviewTxtComment = itemView.bindTextView(R.id.cardReviewTxtComment)

    fun bind(review: Review?) {
        review?.let {
            cardReviewTxtDateCreated?.text = it.dateCreated
            cardReviewTxtComment?.text = it.comment
        }
    }
}



