package io.usys.report.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class LudiPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var type: String? = null
    var teamId: String? = null
    var playerId: String? = null
    var orgId: String? = null
    var rosterId: String? = null
    var realmIdArg: String? = null
    var fragments: MutableList<Pair<String, Fragment>> = mutableListOf()
    //
    var headerView: View? = null
    fun addStringIdArgs(teamId: String?=null, playerId: String?=null, orgId: String?=null, type: String?=null) {
        this.teamId = teamId
        this.playerId = playerId
        this.orgId = orgId
        this.type = type
    }

    fun addHeaderView(headerView: View) {
        this.headerView = headerView
    }

    fun getFragmentAt(position: Int): Fragment {
        return fragments[position].second
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
            arguments = bundleStringIds(teamId, playerId, orgId, rosterId, type)
        }
    }

}

