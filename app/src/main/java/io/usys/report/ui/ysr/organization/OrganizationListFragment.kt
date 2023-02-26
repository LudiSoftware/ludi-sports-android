package io.usys.report.ui.ysr.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragmentWithRealmObject

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : YsrMiddleFragment() {

    private var _binding: FragmentOrgListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
//        _binding?.includeLayoutTest?.lockToTopOfScrollView()
        _binding?.recyclerList?.setupOrganizationList(requireContext(), realmObjectArg, itemOnClick)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_org_profile, bundleRealmObject(obj))
        }
    }

}
