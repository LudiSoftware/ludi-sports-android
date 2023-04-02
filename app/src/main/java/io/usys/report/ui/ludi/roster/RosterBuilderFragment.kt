package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    companion object {
        fun newRoster(): RosterBuilderFragment {
            val fragment = RosterBuilderFragment()
            return fragment
        }
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var teamId: String = "unknown"

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
    var rosterIds = mutableMapOf<String,String>()
    var currentRosterId: String? = null
    var tryoutId: String? = null
    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RosterBuilderFragmentBinding.inflate(inflater, container, false)
        rootView = binding.root

        arguments?.let {
            teamId = it.getString("teamId") ?: "unknown"
        }

        setupRosterIds()
        setupTeamRosterRealmListener()
        return rootView
    }

    /** Master Roster Setup! **/
    private fun setupRosterIds() {
        // Official Roster
        realmInstance?.findRosterIdByTeamId(teamId)?.let { rosterId ->
            // official roster
            rosterIds["official"] = rosterId
            currentRosterId = rosterId
        }
        // TryOut Roster
        realmInstance?.findTryOutIdByTeamId(teamId) { tryoutId ->
            realmInstance?.findTryOutById(tryoutId)?.let { to ->
                to.rosterId?.let {
                    // tryout roster
                    rosterIds["tryout"] = it
                }
                this.tryoutId = tryoutId
            }
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupCurrentRosterDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
            toPlayerProfile(teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }
        currentRosterId?.let {
            _binding?.rosterBuilderLudiRosterView?.root?.setActivity(requireActivity())
            _binding?.rosterBuilderLudiRosterView?.root?.setupRoster(it, onClickReturnViewRealmObject)
        }

    }

    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            var updateDisplay = false
            rosterIds.values.forEach { rosterId ->
                it.find { it.id == rosterId }?.let { _ ->
                    updateDisplay = true
                }
            }
            if (updateDisplay) {
                setupCurrentRosterDisplay()
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}
