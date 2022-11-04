package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.model.*
import io.realm.RealmList
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.ui.setupOrganizationList

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : YsrMiddleFragment() {

    private var _binding: FragmentOrgListBinding? = null
    private val binding get() = _binding!!

//    private var hasBeenLoaded = false
//    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        _binding?.recyclerList?.setupOrganizationList(requireContext(), realmObjectArg, itemOnClick)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
