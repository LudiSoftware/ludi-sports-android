package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.TeamRosterFragmentBinding
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.RosterType
import io.usys.report.ui.fragments.*
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ViewRosterFragment : LudiStringIdsFragment() {

    companion object {
        private const val ARG_ROSTER_TYPE = "roster_type"
        private const val ARG_ROSTER_ID = "roster_id"
        private const val ARG_ROSTER_TITLE = "roster_title"
        private const val ARG_ROSTER_TEAM_ID = "roster_team_id"

        fun newRoster(rosterId: String, title:String, teamId:String): ViewRosterFragment {
            val fragment = ViewRosterFragment()
            val args = Bundle()
            args.putString(ARG_ROSTER_ID, rosterId)
            args.putString(ARG_ROSTER_TITLE, title)
            args.putString(ARG_ROSTER_TEAM_ID, teamId)
            fragment.arguments = args
            return fragment
        }
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: TeamRosterFragmentBinding? = null
    private val binding get() = _binding!!

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
    var rosterId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = TeamRosterFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        arguments?.let {
            rosterType = it.getString(ARG_ROSTER_TYPE) ?: "Official"
            rosterId = it.getString(ARG_ROSTER_ID) ?: "unknown"
            title = it.getString(ARG_ROSTER_TITLE) ?: "No Roster Found!"
            log("Roster type: $rosterType")
        }

        setupDisplay()
        setupTeamRosterRealmListener()
        return rootView
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
            toFragmentWithIds(R.id.navigation_player_profile, teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }
        rosterId?.let {
            _binding?.includeTeamRosterLudiListViewTeams?.root?.setupRoster(it, title, onClickReturnViewRealmObject)
        }
    }

    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                it.findById(rosterId)?.let { roster ->
                    setupDisplay()
                }
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}

