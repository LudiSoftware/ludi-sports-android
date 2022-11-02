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
import io.usys.report.db.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.ui.loadInRealmList

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : YsrFragment() {

    private var _binding: FragmentOrgListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var coachesList: RealmList<Coach>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
        rootView = binding.root

        setupOnClickListeners()

        (realmObjectArg as? Organization)?.id?.let {
            getOrderByEqualToAsync(FireDB.COACHES, ORDER_BY_ORGANIZATION, it) {
                coachesList = this?.toRealmList()
                _binding?.recyclerList?.loadInRealmList(coachesList, requireContext(), FireDB.COACHES, itemOnClick)
            }
        }

        return rootView
    }

    //todo: navigate to org profile and coaching list...
    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_profile, obj)
        }
    }




}
