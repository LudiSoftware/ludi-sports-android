package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.firebase.fireGetTeamProfileForSession
import io.usys.report.firebase.fireGetTryOutProfileForSession
import io.usys.report.realm.findFirstByField
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.realm.model.TryOut
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.chat.ChatFragment
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragmentVG : YsrMiddleFragment() {

    private var _MODE = YsrMode.TRYOUTS
    private var menu: TeamMenuProvider? = null

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!
    private var teamId: String? = null
    private var teamRef: TeamRef? = null
    private var team: Team? = null
    private var tryout: TryOut? = null
    private var tryoutRoster: Roster? = null
    private var officialRoster: Roster? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root

        hideLudiNavView()
        showLudiActionBar()
        //Basic Setup
        teamRef = realmObjectArg as? TeamRef
        teamId = teamRef?.id ?: "unknown"
        menu = TeamMenuProvider(this, teamId!!)
        requireActivity().addMenuProvider(menu!!)
        //Setup Functions
        setupTeamViewPager()
        setupTeamRealmListener()
        setupTryOutRealmListener()
        setupOnClickListeners()
        teamId?.let {
            fireGetTeamProfileForSession(it)
        }
        if (_MODE == YsrMode.TRYOUTS) {
            fireGetTryOutProfileForSession(teamId!!)
        }
        return rootView
    }

    private fun setupTeamViewPager() {
        viewPager = _binding?.teamViewPager!!
        tabLayout = _binding?.teamTabLayout!!
        tabLayout.isNestedScrollingEnabled = true
        tabLayout.tabMode = TabLayout.MODE_FIXED
        viewPager.isUserInputEnabled = false
        val adapter = LudiPagerAdapter(this)
        adapter.addRealmIdArg(teamId)
        val fragPairRoster = Pair("Roster", TeamRosterFragment())
        val fragPairNotes = Pair("Notes", TeamNotesFragment())
//        val fragPairEvaluation = Pair("Evaluations", TeamEvaluationsFragment())
        val fragPairChat = Pair("Chat", ChatFragment())
        adapter.addFragment(fragPairRoster)
        adapter.addFragment(fragPairNotes)
//        adapter.addFragment(fragPairEvaluation)
        adapter.addFragment(fragPairChat)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.fragments[position].first
        }.attach()
    }

    private fun setupTeamRealmListener() {
        val teamListener = RealmChangeListener<RealmResults<Team>> {
            // Handle changes to the Realm data here
            log("Team listener called")
            val realmTeam = realmInstance?.findFirstByField<Team>("id", teamId!!)
            if (realmTeam != null) {
                team = realmTeam
//                team?.rosterId?.let {
//                    setupRosterRealmListener(it)
//                    fireGetRosterForSession(it)
//                }
            }
            setupHeader()
        }
        realmInstance?.where(Team::class.java)?.findAllAsync()?.addChangeListener(teamListener)
    }

//    private fun setupRosterRealmListener(rosterId:String) {
//        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
//            // Handle changes to the Realm data here
//            log("Roster listener called")
//            val realmRoster = realmInstance?.findByField<Roster>("id", rosterId)
//            if (realmRoster != null) {
//                if (!realmRoster.isLocked && team?.mode == YsrMode.TRYOUTS) {
//                    tryoutRoster = realmRoster
//                } else {
//                    officialRoster = realmRoster
//                }
//            }
//
//        }
//        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
//    }

    private fun setupTryOutRealmListener() {
        val tryoutListener = RealmChangeListener<RealmResults<TryOut>> {
            // Handle changes to the Realm data here
            log("TryOut listener called")
            val realmTeam = realmInstance?.findFirstByField<TryOut>("teamId", teamId!!)
            if (realmTeam != null) {
                tryout = realmTeam
            }

        }
        realmInstance?.where(TryOut::class.java)?.findAllAsync()?.addChangeListener(tryoutListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
        menu?.let {
            requireActivity().removeMenuProvider(it)
        }
    }

    private fun setupHeader() {
        tryCatch {
            requireActivity().ludiActionBar()?.title = teamRef?.name
        }
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.text = teamRef?.name
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtOne?.text = teamRef?.headCoachName
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTwo?.text = teamRef?.ageGroup + teamRef?.year
        teamRef?.imgUrl?.let {
            _binding?.includeTeamProfileCard?.cardTeamMediumImgMainIcon?.loadUriIntoImgView(it)
        }
    }

    override fun setupOnClickListeners() {
        log("Setting up onClickListeners")
    }

}

class TeamMenuProvider(val fragment:Fragment, val teamId: String) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_team_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_formation -> {
                fragment.toFragmentWithId(R.id.navigation_tryout_frag, teamId)
                return true
            }
            else -> {}
        }
        return true
    }

}