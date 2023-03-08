package io.usys.report.ui.ludi

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
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
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
        setupOnClickListeners()
        realmInstance?.safeUser { itUser ->
            _binding?.txtWelcomeDashboard?.text = "Welcome, ${itUser.name}"
            // Check For Coach User
            setupCoachDisplay()
        }

//        NewEvaluationDialog().show(childFragmentManager, "NewEvaluationDialog")
        setupRealmCoachListener()
        return binding.root
    }

    private fun setupRealmCoachListener() {
        val coachListener = RealmChangeListener<RealmResults<Coach>> {
            // Handle changes to the Realm data here
            log("Coach listener called")
            setupCoachDisplay()
        }
        realmInstance?.where(Coach::class.java)?.findAllAsync()?.addChangeListener(coachListener)
    }

    private fun setupCoachDisplay() {
        val coach = realmInstance?.findCoachBySafeId()
        if (coach != null) {
            val teams = coach.teams
            teams?.let {
                teamRefList = it
                setupTeamList()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
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
        _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInCustomAttributes(teamRefList, FireTypes.TEAMS, itemOnClickTeamList, "small")
    }

    override fun setupOnClickListeners() {
        itemOnClickSportList = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_org_list, bundleRealmObject(obj))
        }
        itemOnClickTeamList = { view, obj ->
            val teamRef = obj as TeamRef
            val id = teamRef.id ?: "unknown"
            toFragmentWithRealmObject(R.id.navigation_team_profile, bundleStringId(id))
        }
//        itemOnClickServiceList = { _, obj ->
//            toFragment(R.id.navigation_service_details, bundleRealmObject(obj))
//        }

    }

}

