package io.usys.report.ui.ysr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.DefaultFullDashboardBinding
import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetCoachProfile
import io.usys.report.firebase.fireGetTeamProfile
import io.usys.report.realm.findByField
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm
import io.usys.report.realm.session
import io.usys.report.ui.fragments.YsrFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragment
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.utils.log


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var _binding: DefaultFullDashboardBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null
    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    var teamRefList: RealmList<TeamRef>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DefaultFullDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        realmInstance = realm()
        realmInstance?.safeUser { itUser ->
            _binding?.txtWelcomeDashboard?.text = "Welcome, ${itUser.name}"
            // Check For Coach User
            val coach = realmInstance?.findByField<Coach>("id", itUser.id)
            if (coach != null) {
                log("Coach is not null")
                val teams = coach.teams
                teams?.let {
                    teamRefList = it
                }
            }
        }

        val coachListener = RealmChangeListener<RealmResults<Coach>> {
            // Handle changes to the Realm data here
            log("Coach listener called")
            val coach = realmInstance?.findByField<Coach>("id", userId!!)
            if (coach != null) {
                val teams = coach.teams
                teams?.let {
                    teamRefList = it
                }
            }
            setupTeamList()
        }
        realmInstance?.where(Coach::class.java)?.findAllAsync()?.addChangeListener(coachListener)
        setupOnClickListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
//        setupTeamList()
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSportsList() {
        _binding?.includeYsrListViewSports?.root?.setupSportList(itemOnClickSportList)
    }

    private fun setupTeamList() {
        _binding?.includeYsrListViewTeams?.root?.setTitle("Teams")
        _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInRealmList(teamRefList, FireTypes.TEAMS, itemOnClickTeamList, true)
    }

    override fun setupOnClickListeners() {
        itemOnClickSportList = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }
        itemOnClickTeamList = { _, obj ->
            toFragment(R.id.navigation_team_profile, bundleRealmObject(obj))
        }
//        itemOnClickServiceList = { _, obj ->
//            toFragment(R.id.navigation_service_details, bundleRealmObject(obj))
//        }

    }

}

