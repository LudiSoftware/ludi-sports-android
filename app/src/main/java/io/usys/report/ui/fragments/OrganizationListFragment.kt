package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.realm.RealmList
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.db.FireTypes
import io.usys.report.db.getOrderByEqualTo
import io.usys.report.model.Organization.Companion.ORDER_BY_SPORTS
import io.usys.report.ui.loadInRealmList

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : YsrFragment() {

    private var _binding: FragmentOrgListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var hasBeenLoaded = false
    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
        rootView = binding.root

        setupOnClickListeners()

        if (!hasBeenLoaded) {
            getOrderByEqualTo(FireTypes.ORGANIZATIONS, ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
                organizationList = this?.toRealmList()
                _binding?.recyclerList?.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
            }
            hasBeenLoaded = true
        } else {
            _binding?.recyclerList?.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
        }
        return rootView
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
