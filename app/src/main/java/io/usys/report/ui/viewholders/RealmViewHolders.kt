package io.usys.report.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.db.FireDB
import io.usys.report.db.FireTypes
import io.usys.report.db.loadIntoRealm
import io.usys.report.model.Organization
import io.usys.report.model.Sport
import io.usys.report.utils.*


class RouterViewHolder(itemView: View, var type:String) : RecyclerView.ViewHolder(itemView) {

    fun bind(obj: RealmObject) {
        when (type) {
            FireDB.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireDB.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
        }
    }
}
class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
    var itemLinearLayout = itemView.getLinearLayout(R.id.itemLinearLayout)
    var txtItemSpotName = itemView.getTextView(R.id.txtItemSpotName)
    var txtItemDate = itemView.getTextView(R.id.txtItemDate)
    var txtItemEstPeople = itemView.getTextView(R.id.txtItemEstPeople)
    var txtItemCost = itemView.getTextView(R.id.txtItemCost)

    fun bind(sport: Sport?) {
        sport?.let {
            txtItemSpotName.text = it.name
            txtItemDate.text = it.id
            txtItemEstPeople.text = "1"
            txtItemCost.text = "1"

            itemLinearLayout.setOnClickListener {
                println("!!!!! ORGANIZATION ${it.id} PRESSED!!!!!!!!!!!!")
            }
        }
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
    var itemLinearLayout = itemView.getLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.getTextView(R.id.itemLocationName)
    var txtAddressOne = itemView.getTextView(R.id.txtAddressOne)
    var txtAddressTwo = itemView.getTextView(R.id.txtAddressTwo)
    var txtCityStateZip = itemView.getTextView(R.id.txtCityStateZip)
    var txtPeople = itemView.getTextView(R.id.txtPeople)
    var btnAddEditLocationManage = itemView.getImageButton(R.id.btnAddEditLocationManage)
    var btnMinusLocationManage = itemView.getImageButton(R.id.btnMinusLocationManage)

    fun bind(org: Organization?) {
        txtOrgName.text = org?.name
        txtAddressOne.text = org?.addressOne
        txtAddressTwo.text = org?.addressTwo
        txtCityStateZip.text = org?.city
        txtPeople.text = org?.sport

        itemLinearLayout.setOnClickListener {
            println("!!!!! ORGANIZATION CELL PRESSED!!!!!!!!!!!!")
        }
    }
}





