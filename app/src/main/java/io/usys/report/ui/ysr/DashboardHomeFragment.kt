package io.usys.report.ui.ysr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.DefaultFullDashboardBinding
import io.usys.report.model.Session
import io.usys.report.model.Sport
import io.usys.report.model.getCoachByOwnerId
import io.usys.report.model.safeUser
import io.usys.report.ui.fragments.MyBottomSheetDrawerFragment
import io.usys.report.ui.fragments.YsrFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragment
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.utils.log
import io.usys.report.utils.session


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var _binding: DefaultFullDashboardBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null
    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DefaultFullDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()

        safeUser {
            if (it.isCoachUser()) {
                //todo: SAVE COACH OBJECT EXACTLY LIKE A USER OBJECT...
                val i = getCoachByOwnerId(it.id)
                log(i)
            }
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
        _binding?.includeYsrListViewSports?.root?.setupSportList(itemOnClickSportList)
        _binding?.includeYsrListViewTeams?.root?.setupTeamListFromSession(itemOnClickTeamList)
    }

    override fun setupOnClickListeners() {
        itemOnClickSportList = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }
//        itemOnClickServiceList = { _, obj ->
//            toFragment(R.id.navigation_service_details, bundleRealmObject(obj))
//        }

    }

}

