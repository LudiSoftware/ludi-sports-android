package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.realm.Realm
import io.usys.report.realm.*
import io.usys.report.ui.ludi.roster.ViewRosterFragment
import io.usys.report.ui.ludi.team.subscribeToTryoutUpdates

class LudiRosterPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var fragmentPairs: MutableList<Pair<String, Fragment>> = mutableListOf()

    var realmInstance: Realm? = null
    var teamId: String? = null
    var tryoutId: String? = null

    fun getFragmentAt(position: Int): Fragment {
        return fragmentPairs[position].second
    }

    fun setupRosters(teamId: String) {
        this.teamId = teamId
        this.realmInstance = realm()
        setupRosterFragments()
    }

    /** Master Roster Setup! **/
    private fun setupRosterFragments() {
        realmInstance?.findTeamById(teamId)?.let { team ->
            // Official Roster
            fragmentPairs.add(Pair("Official Roster", ViewRosterFragment.newRoster(team.rosterId!!, "Official Roster", teamId!!)))
            // TryOut Roster
            team.tryoutId?.let {
                realmInstance?.findTryOutById(team.tryoutId)?.let { to ->
                    to.rosterId?.let {
                        fragmentPairs.add(Pair("TryOut Roster", ViewRosterFragment.newRoster(it, "TryOut", teamId!!)))
                    }
                    this.tryoutId = team.tryoutId
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return fragmentPairs.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentPairs[position].second
    }

}

