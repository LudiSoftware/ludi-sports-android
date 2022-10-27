package io.usys.report.coachUser.manage

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.model.Organization
import io.usys.report.model.Session
import io.usys.report.utils.inflate
import io.realm.RealmList
import kotlinx.android.extensions.LayoutContainer


class LocManageViewAdapter(mContext: Context, val locManageFragment: LocManageFragment)
    : RecyclerView.Adapter<LocManageViewAdapter.InnerLocationViewHolder>() {

    var organizationList : RealmList<Organization>? = Session.session?.organizations
    var arrayOfOrganizations : ArrayList<Organization> = ArrayList()
    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerLocationViewHolder {
        reloadLocations()
        return InnerLocationViewHolder(parent.inflate(R.layout.item_list_organization), locManageFragment = locManageFragment)
    }

    override fun onBindViewHolder(viewHolder: InnerLocationViewHolder, position: Int) {

        arrayOfOrganizations.let {
            it[position].let { it1 ->
                viewHolder.bind(it1)
            }
        }

    }

    override fun getItemCount(): Int {
        if (arrayOfOrganizations.isEmpty()){
            reloadLocations()
        }
        arrayOfOrganizations.let {
            return it.size
        }
    }

    private fun reloadLocations(){
        this.organizationList = Session.session?.organizations
        arrayOfOrganizations.clear()
        organizationList?.iterator()?.forEach { itLocation ->
            arrayOfOrganizations.add(itLocation)
        }
    }

    inner class InnerLocationViewHolder(override val containerView: View,
                                        val locManageFragment: LocManageFragment
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer  {

        fun bind(organization: Organization) {
//            containerView.itemLocationName.text = organization.name
//            containerView.txtAddressOne.text = organization.addressOne
//            containerView.txtAddressTwo.text = organization.addressTwo
//            containerView.txtCityStateZip.text = "${organization.city}, ${organization.state}, ${organization.zip}"
//            containerView.txtPeople.text = organization.estPeople
//
//            containerView.btnAddEditLocationManage.setOnClickListener {
//                locManageFragment.toggleButtons()
//                if (locManageFragment.MODE == locManageFragment._EDIT) {
//                    locManageFragment.fillAllFields(organization)
//                } else {
//                    locManageFragment.clearAllFields()
//                }
//            }
//            containerView.btnMinusLocationManage.setOnClickListener {
//                organization.createDeleteLocationDialog(locManageFragment).show()
//            }
        }

    }
}