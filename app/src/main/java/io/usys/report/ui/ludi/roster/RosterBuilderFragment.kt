package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.LudiRosterViewBinding
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Roster"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: LudiRosterViewBinding? = null
    private val binding get() = _binding!!

    var rosterId: String? = null
    var roster: Roster? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = LudiRosterViewBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        rosterId = realmInstance?.findRosterIdByTeamId(teamId)
        roster = realmInstance?.findRosterById(rosterId)

        if (roster == null && rosterId != null) {
            fireGetRosterInBackground(rosterId!!)
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
            toPlayerProfile(teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }
//        _binding?.includeTeamRosterLudiListViewTeams?.root?.setTitle("Roster")
//        rosterId?.let {
//            _binding?.includeTeamRosterLudiListViewTeams?.root?.setupPlayerListOfficialRoster(
//                it, onClickReturnViewRealmObject)
//        }
    }


    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                it.find { it.id == rosterId }?.let { roster ->
                    setupDisplay()
                }
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}
