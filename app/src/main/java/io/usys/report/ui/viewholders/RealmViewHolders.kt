package io.usys.report.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireDB
import io.usys.report.model.Coach
import io.usys.report.model.Organization
import io.usys.report.model.Sport
import io.usys.report.utils.*


class RouterViewHolder(itemView: View, var type:String) : RecyclerView.ViewHolder(itemView) {

    fun bind(obj: RealmObject) {
        when (type) {
            FireDB.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
            FireDB.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireDB.COACHES -> return CoachViewHolder(itemView).bind(obj as? Coach)
        }
    }
}
class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
//    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtItemSpotName = itemView.bindTextView(R.id.cardSportTxtSportName)
//    var txtItemDate = itemView.bindTextView(R.id.txtItemDate)
//    var txtItemEstPeople = itemView.bindTextView(R.id.txtItemEstPeople)
//    var txtItemCost = itemView.bindTextView(R.id.txtItemCost)

    fun bind(sport: Sport?) {
        sport?.let {
            txtItemSpotName?.text = it.name
//            txtItemDate.text = it.id
//            txtItemEstPeople.text = "1"
//            txtItemCost.text = "1"
        }
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
//    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.bindTextView(R.id.txtCardOrgName)
    var txtAddressOne = itemView.bindTextView(R.id.txtAddressOne)
    var txtAddressTwo = itemView.bindTextView(R.id.txtAddressTwo)
    var txtCityStateZip = itemView.bindTextView(R.id.txtCityStateZip)
    var txtPeople = itemView.bindTextView(R.id.txtCardOrgSport)
//    var btnAddEditLocationManage = itemView.bindImageButton(R.id.btnAddEditLocationManage)
//    var btnMinusLocationManage = itemView.bindImageButton(R.id.btnMinusLocationManage)

    fun bind(org: Organization?) {
        txtOrgName?.text = org?.name
        txtAddressOne?.text = org?.addressOne
        txtAddressTwo?.text = org?.addressTwo
        txtCityStateZip?.text = org?.city
        txtPeople?.text = org?.sport
    }
}

class CoachViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
//    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtItemSpotName = itemView.bindTextView(R.id.cardSportTxtSportName)
//    var txtItemDate = itemView.bindTextView(R.id.txtItemDate)
//    var txtItemEstPeople = itemView.bindTextView(R.id.txtItemEstPeople)
//    var txtItemCost = itemView.bindTextView(R.id.txtItemCost)

    fun bind(coach: Coach?) {
        coach?.let {
            txtItemSpotName?.text = it.name
//            txtItemDate.text = it.id
//            txtItemEstPeople.text = "1"
//            txtItemCost.text = "1"
        }
    }
}



