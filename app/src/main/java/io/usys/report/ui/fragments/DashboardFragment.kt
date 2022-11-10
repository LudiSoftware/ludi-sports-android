package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.FragmentDashboardBinding
import io.usys.report.model.Session
import io.usys.report.model.createService
import io.usys.report.model.createSport
import io.usys.report.ui.setupServiceList
import io.usys.report.ui.setupSportList
import io.usys.report.utils.log
import io.usys.report.utils.session


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()

//        createCoach()
//        createService()
//        createSport()
        session {
            val temp = it.services
            log(temp)
        }
        return binding.root
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
        _binding?.dashboardRecyclerServiceList?.setupServiceList(requireContext(), itemOnClickServiceList)
        _binding?.recyclerSportList?.setupSportList(requireContext(), itemOnClick)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

        itemOnClickServiceList = { _, obj ->
            toFragment(R.id.navigation_service_details, bundleRealmObject(obj))
        }

    }

}

