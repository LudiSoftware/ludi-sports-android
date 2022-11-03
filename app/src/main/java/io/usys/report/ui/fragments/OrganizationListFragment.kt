package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.getOrderByEqualToAsync
import io.usys.report.model.Organization.Companion.ORDER_BY_SPORTS
import io.usys.report.ui.loadInRealmList

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : YsrMiddleFragment() {

    private var _binding: FragmentOrgListBinding? = null
    private val binding get() = _binding!!

    private var hasBeenLoaded = false
    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        // Load Organizations by Sport
        if (!hasBeenLoaded) {
            getOrderByEqualToAsync(FireTypes.ORGANIZATIONS, ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
                organizationList = this?.toRealmList()
                _binding?.recyclerList?.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
            }
            hasBeenLoaded = true
        } else {
            _binding?.recyclerList?.loadInRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS, itemOnClick)
        }
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
