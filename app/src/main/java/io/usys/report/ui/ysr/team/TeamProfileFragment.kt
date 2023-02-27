package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
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
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragment : YsrMiddleFragment() {

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

        viewPager = _binding?.teamViewPager!!
        tabLayout = _binding?.teamTabLayout!!
        val adapter = LudiPagerAdapter(this)
        val fragPair1 = Pair("Roster", RosterFormationFragment())
        val fragPair2 = Pair("Formations", RosterFormationFragment())
        adapter.addFragment(fragPair1)
        adapter.addFragment(fragPair2)
        viewPager.adapter = adapter
//        childFragmentManager.beginTransaction().replace(R.id.teamFragmentContainerView, this).commit()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.fragments[position].first
        }.attach()

        //Basic Setup
        teamRef = realmObjectArg as? TeamRef
        teamId = teamRef?.id
        setupTeamRealmListener()
        setupTryOutRealmListener()
        setupOnClickListeners()
        teamId?.let {
            fireGetTeamProfileForSession(it)
        }
        if (_MODE == YsrMode.TRYOUTS) {
            fireGetTryOutProfileForSession(teamId!!)
        }

//        setupDisplay()
        return rootView
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
//            _binding?.btnTeamTabTryOuts?.makeVisible()
//            _binding?.btnTeamTabTryOuts?.setOnClickListener {
//                toFragmentWithRealmObject(R.id.navigation_tryout_frag, bundleRealmObject(teamRef!!))
//            }
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
//        _binding?.includeYsrListScheduleLayout?.makeGone()
//        _binding?.includeYsrListViewRosterLayout?.makeGone()
//
//        _binding?.btnTeamTabHome?.setOnClickListener {
//            _binding?.includeYsrListScheduleLayout?.makeVisible()
//            _binding?.includeYsrListViewRosterLayout?.makeGone()
//        }
    }

    fun setupCoachesChat() {
//        _binding?.btnTeamTabChat?.setOnClickListener {
//            val chatDialogFragment = ChatDialogFragment.newChatInstance(teamRef!!.id!!)
//            chatDialogFragment.show(childFragmentManager, "chat_dialog")
//        }
    }


    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
//            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }
    }

}
