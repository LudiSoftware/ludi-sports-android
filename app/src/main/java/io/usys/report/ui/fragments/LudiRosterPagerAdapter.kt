package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.realm.Realm
import io.usys.report.realm.*
import io.usys.report.ui.ludi.roster.ViewRosterFragment


class LudiRosterPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var realmInstance: Realm? = null
    var fragments: MutableList<Fragment> = mutableListOf()
    var teamId: String? = null
    var tryoutId: String? = null

    fun setupRosters(teamId: String) {
        this.teamId = teamId
        this.realmInstance = realm()
        setupRosterFragments()
    }

    private fun setupRosterFragments() {

        val ids = mutableMapOf<String,String>()
        realmInstance?.findTryOutIdByTeamId(teamId) { tryoutId ->
            realmInstance?.findTryOutById(tryoutId)?.let { to ->
                to.rosterId?.let {
                    ids["TryOut"] = it
                }
                this.tryoutId = tryoutId
            }
        }
        realmInstance?.findRosterIdByTeamId(teamId)?.let { rosterId ->
            ids["Official"] = rosterId
        }

        ids.forEach { (title, id) ->
            fragments.add(ViewRosterFragment.newRoster(id, title, teamId!!))
        }
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}

