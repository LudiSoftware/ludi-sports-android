package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class LudiPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var teamId: String? = null
    var playerId: String? = null
    var orgId: String? = null
    var realmIdArg: String? = null
    var fragments: MutableList<Pair<String, Fragment>> = mutableListOf()

    fun addRealmIdArg(realmIdArg: String?) {
        this.realmIdArg = realmIdArg
    }

    fun addStringIdArgs(teamId: String?=null, playerId: String?=null, orgId: String?=null) {
        this.teamId = teamId
        this.playerId = playerId
        this.orgId = orgId
    }

    fun addFragment(fragmentName: String, fragment: Fragment) {
        fragments.add(Pair(fragmentName, fragment))
        this.notifyDataSetChanged()
    }

    fun addFragment(fragmentPair: Pair<String, Fragment>) {
        fragments.add(fragmentPair)
        this.notifyDataSetChanged()
    }

    fun addFragments(fragmentPairs: MutableList<Pair<String, Fragment>>) {
        fragmentPairs.forEach {
            fragments.add(it)
        }
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].second.apply {
            arguments = bundleStringIds(teamId, playerId, orgId)
        }
    }

}

