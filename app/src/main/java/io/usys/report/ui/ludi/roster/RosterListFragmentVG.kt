package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.usys.report.R
import io.usys.report.databinding.RosterVgFragmentBinding
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.*
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.roster.config.RosterType
import io.usys.report.utils.ludi.LudiViewGroupViewModel
import io.usys.report.ui.views.viewGroup.LudiViewGroup

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterListFragmentVG : LudiStringIdsFragment() {

    companion object {
        fun newRosterVG(headerView:View?=null): RosterListFragmentVG {
            val fragment = RosterListFragmentVG()
            fragment.headerView = headerView
            return fragment
        }
    }

    private var _binding: RosterVgFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var ludiViewGroupViewModel: LudiViewGroupViewModel

    var rosterType: RosterType? = null
    var rosterIds: MutableList<String> = mutableListOf()
//    var rosterId: String? = null
    var roster: Roster? = null
    var headerView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)

        if (container == null) {
            _binding = RosterVgFragmentBinding.inflate(inflater, teamContainer, false)
        } else {
            _binding = RosterVgFragmentBinding.inflate(inflater, container, false)
        }

        rootView = binding.root

        ludiViewGroupViewModel = ViewModelProvider(requireActivity())[LudiViewGroupViewModel::class.java]

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
        val lvg = LudiViewGroup(this, _binding?.ludiRosterVGLinearLayout!!, teamId, playerId, null, type, headerView)
        ludiViewGroupViewModel.setLudiViewGroup(lvg)
        ludiViewGroupViewModel.ludiViewGroup.value?.setupRosterTabs()

        return rootView
    }


}
