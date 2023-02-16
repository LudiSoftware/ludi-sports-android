package io.usys.report.ui.ysr.organization

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetOrderByEqualToAsync
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Sport
import io.usys.report.realm.loadInRealmList
import io.usys.report.utils.bind
import io.usys.report.utils.bindTextView
import io.usys.report.utils.cast
import io.usys.report.realm.toRealmList

/**
 * ORGANIZATION LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrganizationList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    var organizationList: RealmList<Organization>?
    fireGetOrderByEqualToAsync(FireTypes.ORGANIZATIONS, Organization.ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
        organizationList = this?.toRealmList()
        rv?.loadInRealmList(organizationList, context, FireTypes.ORGANIZATIONS, onClick)
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.bindTextView(R.id.cardOrgTxtTitle)
    var txtWebsite = itemView.bindTextView(R.id.cardOrgTxtOne)
    var txtLeague = itemView.bindTextView(R.id.cardOrgTxtTwo)
    var imgOrganization: ImageView = itemView.bind(R.id.cardOrgImgMainIcon)
    var includeReviewCard: FrameLayout = itemView.findViewById(R.id.includeReviewCard)

    var cardRatingBar: RatingBar = includeReviewCard.bind(R.id.cardRatingBar)
    var cardRatingBarScore: TextView = includeReviewCard.bind(R.id.cardRatingTxtScore)
    var cardRatingBarCount: TextView = includeReviewCard.bind(R.id.cardRatingTxtCount)

    fun bind(org: Organization?) {
        txtOrgName?.text = org?.base?.name
        txtWebsite?.text = org?.websiteUrl ?: "www.usysr.io"
        txtLeague?.text = org?.leagueRefs?.first()?.leagueName ?: "Alabama State League"
        cardRatingBar.rating = org?.ratingScore?.toFloat() ?: 0.0F
        cardRatingBarScore.text = org?.ratingScore
        cardRatingBarCount.text = "${org?.ratingCount} Reviews"
    }
}