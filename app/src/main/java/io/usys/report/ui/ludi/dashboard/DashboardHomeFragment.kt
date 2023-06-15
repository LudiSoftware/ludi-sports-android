package io.usys.report.ui.ludi.dashboard

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import io.usys.report.ui.ludi.onBackPressed
import io.usys.report.ui.views.listAdapters.teamLiveList.loadInTeamIds
import io.usys.report.ui.views.ludiActionBarTitle
import io.usys.report.ui.views.ludiActionBarResetColor
import io.usys.report.ui.views.menus.SignInOutMenuProvider
import io.usys.report.ui.views.navController.TO_TEAM_PROFILE
import io.usys.report.ui.views.navController.bundleRealmObject
import io.usys.report.ui.views.navController.bundleStringId
import io.usys.report.ui.views.navController.toFragmentWithRealmObject
import io.usys.report.ui.views.statusBar.ludiStatusBarColorWhite
import io.usys.report.utils.*
import io.usys.report.utils.ludi.DeviceScreenSizes
import io.usys.report.utils.ludi.getScreenType
import io.usys.report.utils.ludi.screenDiagonalInches
import io.usys.report.utils.ludi.screenHeight
import io.usys.report.utils.ludi.screenHeightDp
import io.usys.report.utils.ludi.screenWidth
import io.usys.report.utils.ludi.screenWidthDp
import io.usys.report.utils.views.makeInVisible


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var menuIn: SignInOutMenuProvider? = null
    private var menuOut: SignInOutMenuProvider? = null
    private var _binding: LudiDashboardFragmentBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null

    var coachListener: CoachRealmSingleEventListener? = null
    var teamIds: MutableList<String>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LudiDashboardFragmentBinding.inflate(inflater, container, false)
        rootView = binding.root

        val z = requireContext().screenWidthDp
        println("Screen Height: $z")

        onBackPressed { log("Ignoring Back Press") }
        setupOnClickListeners()
        ifNull(user) { _binding?.includeYsrListViewTeams?.root?.makeInVisible() }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).ludiStatusBarColorWhite()
        (requireActivity() as AppCompatActivity).ludiActionBarResetColor(R.color.ysrWindowBackground)
        (requireActivity() as AppCompatActivity).ludiActionBarTitle("Please Sign In!")
        realmInstance?.safeUser { itUser ->
            realmInstance?.createIdBundleSession()
            (requireActivity() as AppCompatActivity).ludiActionBarTitle("Welcome, ${itUser.name}")
            menuOut = SignInOutMenuProvider(requireActivity(), isSignIn = false)
            requireActivity().addMenuProvider(menuOut ?: return)
            // Check For Coach User
            setupCoachDisplay()
        }

        if (user == null) {
            menuIn = SignInOutMenuProvider(requireActivity())
            requireActivity().addMenuProvider(menuIn ?: return)
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
        val adapter = _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInTeamIds(teamIds, this)
    }
    override fun setupOnClickListeners() {
        itemOnClickSportList = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_org_list, bundleRealmObject(obj))
        }

        itemOnClickTeamList = { view, obj ->
            realmInstance?.updateIdBundleIds(teamId = obj.toString())
            toFragmentWithRealmObject(TO_TEAM_PROFILE, bundleStringId(obj.toString()))
        }
    }

}


