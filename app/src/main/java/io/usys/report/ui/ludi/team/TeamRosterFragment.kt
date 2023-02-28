package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.TeamRosterFragmentBinding
import io.usys.report.realm.findByField
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamRosterFragment : LudiStringIdFragment() {

    private var _MODE = YsrMode.TRYOUTS
    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: TeamRosterFragmentBinding? = null
    private val binding get() = _binding!!
    private var teamId: String? = null
    private var roster: Roster? = null
    private var rosterId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.teamViewPager)
        _binding = TeamRosterFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        rosterId = realmIdArg

        setupTeamRosterRealmListener()
        setupOnClickListeners()

        return rootView
    }

    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            val realmRoster = realmInstance?.findByField<Roster>("id", rosterId!!)
            if (realmRoster != null) {
                roster = realmRoster
            }
            setupDisplay()
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
        }
        _binding?.includeTeamRosterLudiListViewTeams?.root?.setTitle("Roster")
        _binding?.includeTeamRosterLudiListViewTeams?.root?.setupPlayerList(rosterId!!, onClickReturnViewRealmObject)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
//            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }
    }

}