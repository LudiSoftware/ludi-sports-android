package io.usys.report.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class LudiPagerAdapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var fragments: MutableList<Pair<String, Fragment>> = mutableListOf()

    fun addFragment(fragmentName: String, fragment: Fragment) {
        fragments.add(Pair(fragmentName, fragment))
        this.notifyDataSetChanged()
    }

    fun addFragment(fragmentPair: Pair<String, Fragment>) {
        fragments.add(fragmentPair)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].second
    }
}