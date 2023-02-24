package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Team
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.ui.fragments.bundleRealmObject
import io.usys.report.ui.fragments.toFragment
import io.usys.report.ui.ysr.chat.ChatDialogFragment
import io.usys.report.ui.ysr.player.popPlayerProfileDialog
import io.usys.report.utils.YsrMode
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragment : YsrMiddleFragment() {

    private var _MODE = YsrMode.TRYOUTS

    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!
    private var team: Team? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root
        team = realmObjectArg as? Team

        setupOnClickListeners()
        setupDisplay()

        return rootView
    }

    private fun setupDisplay() {
        setupHeader()

        _binding?.includeYsrListScheduleLayout?.makeGone()
        _binding?.includeYsrListViewRosterLayout?.makeGone()
        _binding?.btnTeamTabTryOuts?.setOnClickListener {
            toFragment(R.id.navigation_tryout_frag, bundleRealmObject(team!!))
//            _binding?.includeYsrListViewRosterLayout?.makeVisible()
//            _binding?.includeYsrListScheduleLayout?.makeGone()
            _binding?.includeYsrListViewRoster?.root?.setupPlayerList(team!!.id, itemOnClick)
        }
        _binding?.btnTeamTabChat?.setOnClickListener {
            val chatDialogFragment = ChatDialogFragment.newChatInstance(team!!.id)
            chatDialogFragment.show(childFragmentManager, "chat_dialog")
        }
        _binding?.btnTeamTabHome?.setOnClickListener {
            _binding?.includeYsrListScheduleLayout?.makeVisible()
            _binding?.includeYsrListViewRosterLayout?.makeGone()
//            _binding?.includeYsrListSchedule?.root?.loadInRealmObject(dayEvent, "DayEvent")
        }

//        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Services"
    }

    private fun setupHeader() {
        //TODO: Setup header
//        _binding?.includeOrgProfileCard?.cardTxtTitle?.text = "U17 Lady Angels (Bad Girls)"
//        _binding?.includeOrgProfileCard?.cardTxtOne?.text = "Region 3 Premier League"
//        _binding?.includeOrgProfileCard?.cardTxtTwo?.text = ""
    }
    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }

    }

}
