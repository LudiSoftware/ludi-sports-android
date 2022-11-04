package io.usys.report.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireDB
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.getBaseObjects
import io.usys.report.firebase.getOrderByEqualToAsync
import io.usys.report.model.Coach
import io.usys.report.model.Organization
import io.usys.report.model.Sport
import io.usys.report.model.addToSession
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

    fun bind(org: Organization?) {
        txtOrgName?.text = org?.name
        txtWebsite?.text = org?.websiteUrl
        txtLeague?.text = org?.leagueIds?.first() ?: "Alabama State League"
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



