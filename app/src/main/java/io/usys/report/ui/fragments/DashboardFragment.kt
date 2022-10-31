package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.databinding.FragmentDashboardBinding
import io.usys.report.model.*
import io.usys.report.utils.*
import io.usys.report.db.*
import io.usys.report.ui.loadInRealmList

/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        return rootView
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSportsList() {

        session { itSession ->
            if (!itSession.sports.isNullOrEmpty()) {
                _binding?.recyclerSportList?.loadInRealmList(itSession.sports, requireContext(), FireDB.SPORTS, itemOnClick)
            } else {
                getBaseObjects<Sport>(FireTypes.SPORTS) {
                    executeRealm {
                        sportList.clear()
                        sportList = this ?: RealmList()
                        sportList.addToSession()
                    }
                    _binding?.recyclerSportList?.loadInRealmList(sportList, requireContext(), FireDB.SPORTS, itemOnClick)
                }
            }
        }

    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

    }

}