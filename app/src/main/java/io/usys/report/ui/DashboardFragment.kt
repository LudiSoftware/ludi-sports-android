package io.usys.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.usys.report.db.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setupOnClickListeners()
        session {
            sportList.clear()
            sportList = it.sports!!
            rootView.recyclerSportList.loadInRealmList(sportList, requireContext(), FireDB.SPORTS, itemOnClick)
        }

        createCoach()
        return rootView
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

        //-> Global On Click Listeners
        rootView.btnLogout.setOnClickListener {
            //FOR TESTING/ADMIN WORK ONLY
            if (Session.isLogged){
                createAskUserLogoutDialog(requireActivity()).show()
            }
        }
    }

}