package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.realm.Realm
import io.usys.report.realm.*
import io.usys.report.ui.ludi.roster.ViewRosterFragment


class LudiRosterPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var fragmentPairs: MutableList<Pair<String, Fragment>> = mutableListOf()

    var realmInstance: Realm? = null
    var teamId: String? = null
    var tryoutId: String? = null

    fun setupRosters(teamId: String) {
        this.teamId = teamId
        this.realmInstance = realm()
        setupRosterFragments()
    }

    private fun setupRosterFragments() {
        // Official Roster
        realmInstance?.findRosterIdByTeamId(teamId)?.let { rosterId ->
            fragmentPairs.add(Pair("Official Roster", ViewRosterFragment.newRoster(rosterId, "Official Roster", teamId!!)))
        }
        // TryOut Roster
        realmInstance?.findTryOutIdByTeamId(teamId) { tryoutId ->
            realmInstance?.findTryOutById(tryoutId)?.let { to ->
                to.rosterId?.let {
                    fragmentPairs.add(Pair("TryOut Roster", ViewRosterFragment.newRoster(it, "TryOut", teamId!!)))
                }
                this.tryoutId = tryoutId
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

