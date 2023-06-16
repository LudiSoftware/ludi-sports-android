package io.usys.report.ui.ludi.dashboard

import android.content.ContextWrapper
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.LudiDashboardFragmentNewBinding
import io.usys.report.firebase.models.CoachRealmSingleEventListener
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.realm.local.createIdBundleSession
import io.usys.report.realm.local.updateIdBundleIds
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.onBackPressed
import io.usys.report.ui.views.listAdapters.teamLiveList.TeamListLiveAdapter
import io.usys.report.ui.views.listAdapters.teamLiveList.teamLiveAdapter
import io.usys.report.ui.views.ludiActionBarTitle
import io.usys.report.ui.views.ludiActionBarResetColor
import io.usys.report.ui.views.menus.SignInOutMenuProvider
import io.usys.report.ui.views.navController.TO_TEAM_PROFILE
import io.usys.report.ui.views.navController.bundleRealmObject
import io.usys.report.ui.views.navController.bundleStringId
import io.usys.report.ui.views.navController.toFragmentWithRealmObject
import io.usys.report.ui.views.recyclerViews.LudiRCVs
import io.usys.report.ui.views.recyclerViews.addLudiRecyclerView
import io.usys.report.ui.views.recyclerViews.emptyLudiRCVs
import io.usys.report.ui.views.statusBar.ludiStatusBarColorWhite
import io.usys.report.utils.*
import io.usys.report.utils.ludi.NestedScrollViewScrollListener
import io.usys.report.utils.views.getColorCompat
import org.jetbrains.anko.backgroundColor


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardHomeFragment : YsrFragment() {

    private var menuIn: SignInOutMenuProvider? = null
    private var menuOut: SignInOutMenuProvider? = null
    var _binding: LudiDashboardFragmentNewBinding? = null
    private val binding get() = _binding!!

    var ludis: LudiRCVs = emptyLudiRCVs()
    var itemOnClickSportList: ((View, Sport) -> Unit)? = null
    var itemOnClickTeamList: ((View, RealmObject) -> Unit)? = null

    var coachListener: CoachRealmSingleEventListener? = null
    var teamIds: MutableList<String>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LudiDashboardFragmentNewBinding.inflate(inflater, container, false)
        rootView = binding.root

        setupOnClickListeners()

        _binding?.includeLudiCardView?.root?.nestedScrollView?.setOnScrollChangeListener(
            NestedScrollViewScrollListener(_binding?.includeHeader?.root!!)
        )

//        _binding?.root?.background = requireContext().getDrawableCompat(R.drawable.test_background)
        _binding?.root?.backgroundColor = requireContext().getColorCompat(R.color.darkWhite)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        onBackPressed { log("Ignoring Back Press") }
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
        for ((k,v) in ludis) {
            (v?.adapter as? TeamListLiveAdapter)?.destroy()
        }
        ludis.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupSportsList() {
        ludis["sports"]?.removeFromParentLayout()
        ludis["sports"] = _binding?.includeLudiCardView?.root?.linearLayoutView?.addLudiRecyclerView {
            it?.txtTitle?.text = "Sports"
            it?.recyclerView?.isNestedScrollingEnabled = false
            it?.setupSportList(itemOnClickSportList)
        }
    }
    private fun setupTeamList() {
        ludis["teams"]?.removeFromParentLayout()
        ludis["teams"] = _binding?.includeLudiCardView?.root?.linearLayoutView?.addLudiRecyclerView {
            it?.txtTitle?.text = "My Teams"
            it?.recyclerView?.isNestedScrollingEnabled = false
            it?.adapter = it?.recyclerView?.teamLiveAdapter(teamIds, this)
        }
        ludis["teams2"]?.removeFromParentLayout()
        ludis["teams2"] = _binding?.includeLudiCardView?.root?.linearLayoutView?.addLudiRecyclerView {
            it?.txtTitle?.text = "My Organizations"
            it?.recyclerView?.isNestedScrollingEnabled = false
            it?.adapter = it?.recyclerView?.teamLiveAdapter(teamIds, this)
        }
        ludis["teams3"]?.removeFromParentLayout()
        ludis["teams3"] = _binding?.includeLudiCardView?.root?.linearLayoutView?.addLudiRecyclerView {
            it?.txtTitle?.text = "Upcoming Events"
            it?.recyclerView?.isNestedScrollingEnabled = false
            it?.adapter = it?.recyclerView?.teamLiveAdapter(teamIds, this)
        }
        ludis["teams4"]?.removeFromParentLayout()
        ludis["teams4"] = _binding?.includeLudiCardView?.root?.linearLayoutView?.addLudiRecyclerView {
            it?.txtTitle?.text = "My Services"
            it?.recyclerView?.isNestedScrollingEnabled = false
            it?.adapter = it?.recyclerView?.teamLiveAdapter(teamIds, this)
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


fun View.getLifecycleOwner(): LifecycleOwner? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is LifecycleOwner) {
            return context
        }
        context = context.baseContext
    }
    return null
}