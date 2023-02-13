package io.usys.report.ui.ysr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.DefaultFullDashboardBinding
import io.usys.report.firebase.fireGetCoachProfile
import io.usys.report.firebase.fireGetTeamProfile
import io.usys.report.firebase.fireGetTeamsProfiles
import io.usys.report.model.Sport
import io.usys.report.model.Team
import io.usys.report.model.addObjectToSessionList
import io.usys.report.model.safeUserId
import io.usys.report.ui.fragments.YsrFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragment
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.utils.executeRealm
import io.usys.report.utils.io
import io.usys.report.utils.log
import io.usys.report.utils.session


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var _binding: DefaultFullDashboardBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DefaultFullDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()

        session { itSess ->
            val first = itSess.teams
            log(first)
        }
        io {
            val ids = RealmList("3ec805dc-ab59-11ed-a39f-86f7c4c00ee3", "5e95cef0-ab61-11ed-914b-86f7c4c00ee3")
            val testing = fireGetTeamsProfiles(ids)
            val test1 = testing[0]
            addObjectToSessionList("teams", test1!!)
            log(test1)
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
        _binding?.includeYsrListView?.root?.setupSportList(itemOnClickSportList)
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

