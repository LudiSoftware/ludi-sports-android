package io.usys.report.ui.ludi.organization

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetOrderByEqualToAsync
import io.usys.report.firebase.toLudiObjects
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Sport
import io.usys.report.realm.realm
import io.usys.report.realm.toRealmList
import io.usys.report.ui.views.listAdapters.realmList.loadInCustomAttributes
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmList
import io.usys.report.utils.*
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.bindTextView

/**
 * ORGANIZATION LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrganizationList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    var organizationList: RealmList<Organization>?

    val orgs = realm().where(Organization::class.java).findAll().toRealmList()
    if (orgs.isEmpty()) {
        fireGetOrderByEqualToAsync(FireTypes.ORGANIZATIONS, Organization.ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
            organizationList = this?.toLudiObjects<Organization>()
            rv?.loadInCustomAttributes(organizationList, FireTypes.ORGANIZATIONS, onClick)
        }
    } else {
        var orgIds = orgs.map { it.id }
        rv?.loadInRealmList(orgs, FireTypes.ORGANIZATIONS, onClick)
    }

}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.bindTextView(R.id.cardOrgTxtTitle)
    var txtWebsite = itemView.bindTextView(R.id.cardOrgTxtOne)
    var txtLeague = itemView.bindTextView(R.id.cardOrgTxtTwo)
    var imgOrganization: ImageView? = itemView.bind(R.id.cardOrgImgMainIcon)
    var includeReviewCard: FrameLayout? = itemView.findViewById(R.id.includeReviewCard)

    var cardRatingBar: RatingBar? = includeReviewCard?.bind(R.id.cardRatingBar)
    var cardRatingBarScore: TextView? = includeReviewCard?.bind(R.id.cardRatingTxtScore)
    var cardRatingBarCount: TextView? = includeReviewCard?.bind(R.id.cardRatingTxtCount)

    fun bind(org: Organization?) {
        tryCatch {
            txtOrgName?.text = org?.name
            txtWebsite?.text = org?.websiteUrl ?: "www.usysr.io"
            txtLeague?.text = org?.officeHours ?: "8AM - 5PM"
            cardRatingBar?.rating = org?.ratingScore?.toFloat() ?: 0.0F
            cardRatingBarScore?.text = org?.ratingScore.toString()
            cardRatingBarCount?.text = "${org?.ratingCount} Reviews"
        }
    }
}