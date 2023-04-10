package io.usys.report.ui.ludi

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.DefaultFullDashboardBinding
import io.usys.report.firebase.models.CoachRealmSingleEventListener
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.ui.login.LudiLoginActivity
import io.usys.report.ui.ludi.team.TeamProvider
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.ui.views.listAdapters.loadInRealmIds
import io.usys.report.utils.*


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var menuIn: SignInMenuProvider? = null
    private var menuOut: SignOutMenuProvider? = null
    private var _binding: DefaultFullDashboardBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null
    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    var teamListener: RealmChangeListener<RealmResults<Team>>? = null
    var coachListener: CoachRealmSingleEventListener? = null
    var teamIds: MutableList<String>? = mutableListOf()
    var teamRefList: RealmList<TeamRef>? = RealmList()
    var teamProvider: TeamProvider? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DefaultFullDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        _binding?.txtWelcomeDashboard?.text = "Please Sign In!"
        setupOnClickListeners()
        realmInstance?.safeUser { itUser ->
            _binding?.txtWelcomeDashboard?.text = "Welcome, ${itUser.name}"
            // Check For Coach User
            setupCoachDisplay()
        }
//        NewEvaluationDialog().show(childFragmentManager, "NewEvaluationDialog")
        user?.let {
//            setupRealmCoachListener()
        } ?: kotlin.run {
            _binding?.includeYsrListViewTeams?.root?.makeGone()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // todo: if user is logged in...
        if (user == null) {
            menuIn = SignInMenuProvider(requireActivity())
            requireActivity().addMenuProvider(menuIn ?: return)
        } else {
            menuOut = SignOutMenuProvider(requireActivity())
            requireActivity().addMenuProvider(menuOut ?: return)
        }
    }
    override fun onPause() {
        super.onPause()
        requireActivity().removeMenuProvider(menuIn ?: menuOut ?: return)
    }

    private fun setupCoachDisplay() {
        val coach = realmInstance?.findCoachBySafeId()
        if (coach != null) {
            teamIds = coach.teams?.toMutableList()
            if (teamIds.isNullOrEmpty()) return
//            teamProvider = TeamProvider(teamId = teamIds?.first() ?: return)
//            setupTeamRealmListener()
            setupTeamList()
        } else {
            coachListener = CoachRealmSingleEventListener(coachCallback())
        }
    }

    private fun coachCallback() : (() -> Unit) {
        return {
            log("Coach Updated")
//            coachListener?.unregisterListener()
            setupCoachDisplay()
        }
    }

    private fun setupTeamRealmListener() {
        teamListener = RealmChangeListener {
            // Handle changes to the Realm data here
            log("Team listener called")
//            setupTeamList()
        }
        teamListener?.let {
            realmInstance?.where(Team::class.java)?.findAllAsync()?.addChangeListener(it)
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
        _binding?.includeYsrListViewTeams?.root?.txtTitle?.text = "My Teams"
        _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInRealmIds(teamIds, this)
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

    }

}

class SignInMenuProvider(val activity: Activity) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.sign_in_menu, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_signin -> {
                activity.launchActivity<LudiLoginActivity>()
                return true
            }else -> {}
        }
        return true
    }
}

class SignOutMenuProvider(val activity: Activity) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.sign_out_menu, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_signout -> {
                activity.popupYesNo("Sign Out.", "Are you sure you want to sign out?") {
                    Session.logoutAndRestartApplication(activity)
                }
                return true
            }else -> {}
        }
        return true
    }
}
