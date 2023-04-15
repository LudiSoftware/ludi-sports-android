package io.usys.report.ui.ludi

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.LudiDashboardFragmentBinding
import io.usys.report.firebase.models.CoachRealmSingleEventListener
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.realm.local.createIdBundleSession
import io.usys.report.realm.local.updateIdBundleIds
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.ui.login.LudiLoginActivity
import io.usys.report.ui.views.listAdapters.teamLiveList.loadInTeamIds
import io.usys.report.ui.views.ludiActionBar
import io.usys.report.ui.views.resetColor
import io.usys.report.ui.views.tryoutMode
import io.usys.report.utils.*


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var menuIn: SignInMenuProvider? = null
    private var menuOut: SignOutMenuProvider? = null
    private var _binding: LudiDashboardFragmentBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null

    var coachListener: CoachRealmSingleEventListener? = null
    var teamIds: MutableList<String>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LudiDashboardFragmentBinding.inflate(inflater, container, false)
        rootView = binding.root

        ludiActionBar()?.resetColor()

        _binding?.txtWelcomeDashboard?.text = "Please Sign In!"
        setupOnClickListeners()
        realmInstance?.safeUser { itUser ->
            realmInstance?.createIdBundleSession()
            _binding?.txtWelcomeDashboard?.text = "Welcome, ${itUser.name}"
            // Check For Coach User
            setupCoachDisplay()
        }
        user?.let {
//            setupRealmCoachListener()
        } ?: kotlin.run {
            _binding?.includeYsrListViewTeams?.root?.makeInVisible()
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
            setupTeamList()
        } else {
            coachListener = CoachRealmSingleEventListener(coachCallback())
        }
    }

    private fun coachCallback() : (() -> Unit) {
        return {
            log("Coach Updated")
            setupCoachDisplay()
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
        _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInTeamIds(teamIds, this)
    }
    override fun setupOnClickListeners() {
        itemOnClickSportList = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_org_list, bundleRealmObject(obj))
        }

        itemOnClickTeamList = { view, obj ->
            val teamRef = obj as TeamRef
            val id = teamRef.id ?: "unknown"
            realmInstance?.updateIdBundleIds(teamId = id)
            toFragmentWithRealmObject(TO_TEAM_PROFILE, bundleStringId(id))
        }
    }

}

class SignInMenuProvider(private val activity: Activity) : MenuProvider {
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

class SignOutMenuProvider(private val activity: Activity) : MenuProvider {
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
