package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.firebase.fireGetTeamProfileForSession
import io.usys.report.realm.findByField
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragmentWithRealmObject
import io.usys.report.ui.ysr.chat.ChatDialogFragment
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragment : YsrMiddleFragment() {

    private var _MODE = YsrMode.TRYOUTS

    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!
    private var teamId: String? = null
    private var teamRef: TeamRef? = null
    private var team: Team? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root
        //Basic Setup
        teamRef = realmObjectArg as? TeamRef
        teamId = teamRef?.id
        setupTeamRealmListener()
        setupOnClickListeners()
        teamId?.let {
            fireGetTeamProfileForSession(it)
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
        _binding?.includeYsrListScheduleLayout?.makeGone()
        _binding?.includeYsrListViewRosterLayout?.makeGone()
        _binding?.btnTeamTabTryOuts?.setOnClickListener {
            toFragmentWithRealmObject(R.id.navigation_tryout_frag, bundleRealmObject(teamRef!!))
        }
        _binding?.btnTeamTabHome?.setOnClickListener {
            _binding?.includeYsrListScheduleLayout?.makeVisible()
            _binding?.includeYsrListViewRosterLayout?.makeGone()
        }
    }

    fun setupCoachesChat() {
        _binding?.btnTeamTabChat?.setOnClickListener {
            val chatDialogFragment = ChatDialogFragment.newChatInstance(teamRef!!.id!!)
            chatDialogFragment.show(childFragmentManager, "chat_dialog")
        }
    }


    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
//            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }
    }

}
