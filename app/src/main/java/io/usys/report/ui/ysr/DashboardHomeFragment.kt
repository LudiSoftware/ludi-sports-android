package io.usys.report.ui.ysr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.DefaultFullDashboardBinding
import io.usys.report.firebase.fireGetTeamProfile
import io.usys.report.realm.executeRealm
import io.usys.report.realm.model.Sport
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.executeGetCoachByCoachId
import io.usys.report.realm.model.getCoachByCoachId
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.updateFieldsAndSave
import io.usys.report.ui.fragments.YsrFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragment
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.utils.cast
import io.usys.report.utils.log
import io.usys.report.utils.tryCatch


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

        safeUser { itUser ->
            fireGetTeamProfile("a8d341b2-affa-11ed-a62e-86f7c4c00ee3") { itTeam ->
                val teamObj = itTeam?.cast<Team>()
//                val tempTeam = Team()
//                tempTeam.updateFieldsAndSave(teamObj)
//                teamObj?.saveToRealm()
                val managed = teamObj?.isManaged
                val valid = teamObj?.isValid
                val name = teamObj?.name
                val roster = teamObj?.roster
                val coaches = teamObj?.coachRefs
                log(coaches)

            }

            if (itUser.coach) {
                val i = executeGetCoachByCoachId(itUser.id)
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

