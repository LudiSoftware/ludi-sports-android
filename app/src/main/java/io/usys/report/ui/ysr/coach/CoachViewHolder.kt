package io.usys.report.ui.ysr.coach

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetOrderByEqualToAsync
import io.usys.report.model.Coach
import io.usys.report.model.Organization
import io.usys.report.utils.loadInRealmList
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * COACH LIST VIEW CONTROLS
 */

fun RecyclerView?.setupCoachList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Coaches by Organization.id
    val rv = this
    var coachesList: RealmList<Coach>?
    fireGetOrderByEqualToAsync(FireTypes.COACHES, Coach.ORDER_BY_ORGANIZATION, realmObjectArg?.cast<Organization>()?.id ?: return) {
        coachesList = this?.toRealmList()
        rv?.loadInRealmList(coachesList, context, FireTypes.COACHES, onClick)
    }
}

class CoachViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var cardCoachTxtName = itemView.bindTextView(R.id.cardCoachTxtName)
    var cardCoachImgProfile = itemView.bind<ImageView>(R.id.cardCoachImgProfile)
    var cardCoachTxtOrg = itemView.bindTextView(R.id.cardCoachTxtOrg)
    var cardCoachTxtEmail = itemView.bindTextView(R.id.cardCoachTxtEmail)

    fun bind(coach: Coach?) {
        coach?.let {
            cardCoachTxtName?.text = it.ownerName
            cardCoachTxtEmail?.text = it.details
            cardCoachTxtOrg?.text = it.organizationName
            itemView.context.loadUriIntoImgView(it.imgUrl.toUri() ?: return, cardCoachImgProfile)
        }
    }
}