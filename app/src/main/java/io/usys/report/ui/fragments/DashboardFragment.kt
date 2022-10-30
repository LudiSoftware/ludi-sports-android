package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.usys.report.db.*
import io.usys.report.ui.loadInRealmList
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_org_list.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setupOnClickListeners()

        return rootView
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
    }

    private fun setupSportsList() {

        session { itSession ->
            if (!itSession.sports.isNullOrEmpty()) {
                rootView.recyclerSportList.loadInRealmList(itSession.sports, requireContext(), FireDB.SPORTS, itemOnClick)
            } else {
                getBaseObjects<Sport>(FireTypes.SPORTS) {
                    executeRealm {
                        sportList.clear()
                        sportList = this ?: RealmList()
                        sportList.addToSession()
                    }
                    rootView.recyclerSportList.loadInRealmList(sportList, requireContext(), FireDB.SPORTS, itemOnClick)
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