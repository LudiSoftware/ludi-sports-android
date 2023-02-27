package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.usys.report.ui.tryouts.RosterFormationFragment


class LudiPagerAdapter2(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    var fragments: MutableList<Fragment> = mutableListOf()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}