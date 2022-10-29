package io.usys.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.realm.RealmList
import io.usys.report.db.FireTypes
import io.usys.report.db.getOrderByEqualTo
import io.usys.report.model.Organization.Companion.ORDER_BY_SPORTS
import kotlinx.android.synthetic.main.fragment_org_list.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : YsrFragment() {

    private var hasBeenLoaded = false
    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_org_list, container, false)

        setupOnClickListeners()

        if (!hasBeenLoaded) {
            getOrderByEqualTo(FireTypes.ORGANIZATIONS, ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
                organizationList = this?.toRealmList()
                rootView.recyclerList.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
            }
            hasBeenLoaded = true
        } else {
            rootView.recyclerList.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
        }
        return rootView
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
