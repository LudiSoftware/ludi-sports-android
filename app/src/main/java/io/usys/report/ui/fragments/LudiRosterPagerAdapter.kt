package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.realm.Realm
import io.realm.RealmResults
import io.usys.report.realm.*
import io.usys.report.realm.model.TryOut
import io.usys.report.ui.ludi.roster.ViewRosterListFragment
import io.usys.report.utils.log

class LudiRosterPagerAdapter(private val parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var fragmentPairs: MutableList<Pair<String, Fragment>> = mutableListOf()

    var realmInstance: Realm? = null
    var teamId: String? = null
    var tryoutId: String? = null

    private var tryoutListener: RealmResults<TryOut>? = null

    fun setupRosters(teamId: String) {
        this.teamId = teamId
        this.realmInstance = realm()
        setupRosterFragments()
    }

    /** Master Roster Setup! **/
    private fun setupRosterFragments() {
        fragmentPairs.clear()
        realmInstance?.findTeamById(teamId)?.let { team ->
            // Official Roster
            fragmentPairs.add(Pair("Official Roster", ViewRosterListFragment.newRoster(team.rosterId!!, "Official Roster", teamId!!)))
            // TryOut Roster
            team.tryoutId?.let { itToId ->
                tryoutListener = realmInstance?.observe(parentFragment.viewLifecycleOwner) { results ->
                    results.find { it.id == itToId }?.let {
                        log("Team results updated")
                        realmInstance?.findTryOutById(team.tryoutId)?.let { to ->
                            to.rosterId?.let { itToRosterId ->
                                fragmentPairs.add(Pair("TryOut Roster", ViewRosterListFragment.newRoster(itToRosterId, "TryOut", teamId!!)))
                                tryoutListener?.removeAllChangeListeners()
                                notifyDataSetChanged()
                            }
                            this.tryoutId = team.tryoutId
                        }
                    }
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