package io.usys.report.ui.ludi.dashboard

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
import io.usys.report.ui.views.recyclerViews.LudiRecyclerCardView
import io.usys.report.ui.views.statusBar.ludiStatusBarColorWhite
import io.usys.report.utils.*
import io.usys.report.utils.ludi.screenWidthDp
import io.usys.report.utils.views.getDrawableCompat
import io.usys.report.utils.views.makeInVisible


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var menuIn: SignInOutMenuProvider? = null
    private var menuOut: SignInOutMenuProvider? = null
    var _binding: LudiDashboardFragmentBinding? = null
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

        _binding?.root?.background = requireContext().getDrawableCompat(R.drawable.test_background)
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

    inline fun addView(block : (LudiRecyclerCardView) -> Unit) {
        val newView = LayoutInflater.from(requireContext()).inflate(R.layout.ludi_recycler_card_view, _binding?.linearLayoutTop, false) as LudiRecyclerCardView
        // For LinearLayout, just need to set the width and height
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        newView.id = View.generateViewId()  // generate a new id for the view
        newView.layoutParams = layoutParams
//        newView.txtTitle?.text = "Uh ohhhhhhhhhhh"
//        newView.recyclerView?.loadInTeamIds(teamIds, this)
        // add the view to the layout
        _binding?.linearLayoutTop?.addView(newView)
        block(newView)
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
//        _binding?.includeYsrListViewTeams?.root?.txtTitle?.text = "My Teams"
//        val adapter = _binding?.includeYsrListViewTeams?.root?.recyclerView?.loadInTeamIds(teamIds, this)
        addView {
            it.txtTitle?.text = "My Teams"
            it.recyclerView?.loadInTeamIds(teamIds, this)
        }
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


    // Function to convert dp to pixels
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()


}


