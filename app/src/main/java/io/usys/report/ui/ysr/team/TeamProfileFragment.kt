package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.realm.model.DayEvent
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.YsrMiddleFragment
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
        _binding?.includeYsrListViewRoster?.root?.setupPlayerList(team!!.id, itemOnClick)

        _binding?.includeYsrListScheduleLayout?.makeGone()
        _binding?.includeYsrListViewRosterLayout?.makeGone()

        _binding?.btnTeamTabChat?.setOnClickListener {
            _binding?.includeYsrListViewRosterLayout?.makeVisible()
            val chatDialogFragment = ChatDialogFragment.newInstance(team!!.id)
            chatDialogFragment.show(childFragmentManager, "chat_dialog")
        }
        _binding?.btnTeamTabHome?.setOnClickListener {
            val dayEvent: DayEvent = DayEvent().apply {
                type = "Practice"
                startTime = "6:00pm"
                endTime = "8:00pm"
                name = "Rathmell Sports Complex"
            }
            _binding?.includeYsrListScheduleLayout?.makeVisible()
            _binding?.includeYsrListSchedule?.root?.loadInRealmObject(dayEvent, "DayEvent")
        }

//        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Services"
    }

    private fun setupHeader() {
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
