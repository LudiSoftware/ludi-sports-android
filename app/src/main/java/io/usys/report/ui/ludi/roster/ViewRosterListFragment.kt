package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.databinding.TeamRosterFragmentBinding
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.roster.config.RosterController
import io.usys.report.ui.views.listAdapters.HeaderViewScrollListener
import io.usys.report.ui.views.listAdapters.rosterLiveList.RosterListLiveAdapter
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ViewRosterListFragment : LudiStringIdsFragment() {

    companion object {
        private const val ARG_ROSTER_TYPE = "roster_type"
        private const val ARG_ROSTER_ID = "roster_id"
        private const val ARG_ROSTER_TITLE = "roster_title"
        private const val ARG_ROSTER_TEAM_ID = "roster_team_id"

        fun newRoster(rosterId: String, title:String, teamId:String, headerView:View?=null): ViewRosterListFragment {
            val fragment = ViewRosterListFragment()
            fragment.headerView = headerView
            val args = Bundle()
            args.putString(ARG_ROSTER_ID, rosterId)
            args.putString(ARG_ROSTER_TITLE, title)
            args.putString(ARG_ROSTER_TEAM_ID, teamId)
            fragment.arguments = args
            return fragment
        }
    }

    var headerView: View? = null
    private var _binding: TeamRosterFragmentBinding? = null
    private val binding get() = _binding!!

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
//    var rosterId: String? = null


    /**
     * What Other Modifications or Tools would we need for this page?
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        if (container != null) {
            _binding = TeamRosterFragmentBinding.inflate(inflater, container, false)
        } else {
            _binding = TeamRosterFragmentBinding.inflate(inflater, teamContainer, false)
        }

        rootView = binding.root

        arguments?.let {
            rosterType = it.getString(ARG_ROSTER_TYPE) ?: "Official"
            rosterId = it.getString(ARG_ROSTER_ID) ?: "unknown"
            teamId = it.getString(ARG_ROSTER_TEAM_ID) ?: "unknown"
            title = it.getString(ARG_ROSTER_TITLE) ?: "No Roster Found!"
            log("Roster type: $rosterType")
        }

        setupDisplay()
        return rootView
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        teamId?.let { teamId ->
            val config = RosterController(teamId)
            config.parentFragment = this
            config.recyclerView = _binding?.includeTeamRosterLudiListViewTeams?.root
            config.switchRosterTo(title)
            val adapter = RosterListLiveAdapter(config)
            headerView?.let { HeaderViewScrollListener(it) }?.let {
                _binding?.includeTeamRosterLudiListViewTeams?.root?.addOnScrollListener(it)
            }
        }
    }

}

