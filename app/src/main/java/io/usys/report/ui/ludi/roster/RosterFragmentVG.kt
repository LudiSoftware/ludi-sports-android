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

        if (container == null) {
            _binding = FragmentRosterVgBinding.inflate(inflater, teamContainer, false)
        } else {
            _binding = FragmentRosterVgBinding.inflate(inflater, container, false)
        }

        rootView = binding.root

        /** Get/Add Roster Id's **/
        teamId?.let {
            realmInstance?.findTeamById(it)?.let { team ->
                // Team Roster
                team.rosterId?.let { rosterId ->
                    rosterIds.add(rosterId)
                }
                // Roster Type + TryOut Roster
                realmInstance?.findTryOutById(team.tryoutId!!)?.let { tryout ->
                    rosterIds.add(tryout.rosterId!!)
                    rosterType = RosterType.TRYOUT
                }
            }
        }

        // Loop Roster Ids, if they don't exist, go get them.
        rosterIds.forEach { currentRosterId ->
            realmInstance?.ifObjectDoesNotExist<Roster>(currentRosterId) {
                fireGetRosterInBackground(it)
            }
        }

        // ViewPager/Tab Setup
        _binding?.ludiRosterVGLinearLayout.addLudiRosterViewGroup(this, teamId!!)
        return rootView
    }

}
