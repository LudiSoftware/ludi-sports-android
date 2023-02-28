package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.firebase.fireGetTeamProfileForSession
import io.usys.report.firebase.fireGetTryOutProfileForSession
import io.usys.report.realm.findByField
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.realm.model.TryOut
import io.usys.report.ui.fragments.*
import io.usys.report.ui.tryouts.RosterFormationFragment
import io.usys.report.ui.ysr.chat.ChatDialogFragment
import io.usys.report.ui.ysr.chat.ChatFragment
import io.usys.report.utils.YsrMode
import io.usys.report.utils.hideLudiNavView
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragmentVG : YsrMiddleFragment() {

    private var _MODE = YsrMode.TRYOUTS

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!
    private var teamId: String? = null
    private var teamRef: TeamRef? = null
    private var team: Team? = null
    private var tryout: TryOut? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root
        hideLudiNavView()
        //Basic Setup
        teamRef = realmObjectArg as? TeamRef
        teamId = teamRef?.id
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
        val fragPair1 = Pair("Roster", TeamRosterFragment())
        val fragPair2 = Pair("Chat", ChatFragment())
        adapter.addFragment(fragPair1)
        adapter.addFragment(fragPair2)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.fragments[position].first
        }.attach()
    }

    private fun setupTeamRealmListener() {
        val teamListener = RealmChangeListener<RealmResults<Team>> {
            // Handle changes to the Realm data here
            log("Team listener called")
            val realmTeam = realmInstance?.findByField<Team>("id", teamId!!)
            if (realmTeam != null) {
                team = realmTeam
            }
            setupHeader()
        }
        realmInstance?.where(Team::class.java)?.findAllAsync()?.addChangeListener(teamListener)
    }

    private fun setupTryOutRealmListener() {
        val tryoutListener = RealmChangeListener<RealmResults<TryOut>> {
            // Handle changes to the Realm data here
            log("TryOut listener called")
            val realmTeam = realmInstance?.findByField<TryOut>("teamId", teamId!!)
            if (realmTeam != null) {
                tryout = realmTeam
            }

        }
        realmInstance?.where(TryOut::class.java)?.findAllAsync()?.addChangeListener(tryoutListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupHeader() {
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.text = teamRef?.name
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtOne?.text = teamRef?.headCoachName
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTwo?.text = teamRef?.ageGroup + teamRef?.year
    }

    private fun setupDisplay() {
        setupHeader()

    }

    override fun setupOnClickListeners() {
        log("Setting up onClickListeners")
    }

}
