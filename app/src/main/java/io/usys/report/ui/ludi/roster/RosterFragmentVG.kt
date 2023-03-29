package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.R
import io.usys.report.databinding.FragmentRosterVgBinding
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.RosterType
import io.usys.report.ui.fragments.*
import io.usys.report.ui.views.addLudiRosterViewGroup
import io.usys.report.ui.views.addLudiViewGroup
import io.usys.report.ui.views.ludiRosterFragments
import io.usys.report.utils.isNotNBE
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFragmentVG : LudiStringIdsFragment() {

    private var _binding: FragmentRosterVgBinding? = null
    private val binding get() = _binding!!

    var rosterType: RosterType? = null
    var rosterIds: MutableList<String> = mutableListOf()
    var rosterId: String? = null
    var roster: Roster? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = FragmentRosterVgBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        teamId?.let {
            realmInstance?.findTeamById(it)?.let { team ->
                // Team Roster
                team.rosterId?.let { rosterId ->
                    rosterIds.add(rosterId)
                }
                // Roster Type + TryOut Roster
                rosterType = if (team.tryoutId.isNotNBE()) {
                    realmInstance?.findTryOutById(team.tryoutId!!)?.let { tryout ->
                        rosterIds.add(tryout.rosterId!!)
                    }
                    RosterType.TRYOUT
                } else {
                    RosterType.TEAM
                }
            }
        }

        _binding?.ludiRosterVGLinearLayout.addLudiRosterViewGroup(this, teamId!!)

        rosterIds.forEach { currentRosterId ->
            realmInstance?.ifObjectDoesNotExist<Roster>(currentRosterId) {
                fireGetRosterInBackground(it)
            }
        }

        return rootView
    }

    private fun setupDisplay() {
        log("setupDisplay:")
    }


}
