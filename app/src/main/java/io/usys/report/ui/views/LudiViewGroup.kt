package io.usys.report.ui.views

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.usys.report.R
import io.usys.report.ui.fragments.LudiPagerAdapter
import io.usys.report.ui.ludi.chat.ChatFragment
import io.usys.report.ui.ludi.team.TeamNotesFragment
import io.usys.report.ui.ludi.team.TeamRosterFragment
import io.usys.report.utils.log

fun ludiTeamVGFragments(): MutableList<Pair<String, Fragment>> {
    return mutableListOf<Pair<String, Fragment>>().apply {
        add(Pair("Roster", TeamRosterFragment()))
        add(Pair("Notes", TeamNotesFragment()))
        add(Pair("Chat", ChatFragment()))
    }
}

class LudiViewGroup(parentFragment: Fragment, teamId: String?, rootView: LinearLayout) {

    var inflater: LayoutInflater? = null
    var rootView: LinearLayout? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager2? = null

    var teamId: String? = null
    var ludiPagerAdapter: LudiPagerAdapter? = null
    var mview: View? = null

    init {
        inflater = LayoutInflater.from(parentFragment.requireContext())
        this.rootView = rootView
        this.teamId = teamId
        mview = inflater?.inflate(R.layout.ludi_view_group, rootView, false)
        rootView.addView(mview)
        ludiPagerAdapter = LudiPagerAdapter(parentFragment)
        log("LudiViewGroup: init")
    }

    fun setupLudiTabs(fragmentPairs: MutableList<Pair<String, Fragment>>) {
        tabLayout = mview?.findViewById(R.id.ludiTabLayout)
        viewPager = mview?.findViewById(R.id.ludiViewPager)
        tabLayout?.isNestedScrollingEnabled = true
        tabLayout?.tabMode = TabLayout.MODE_FIXED
        viewPager?.isUserInputEnabled = true
        setupLudiPagerAdapter(fragmentPairs)
    }

    private fun setupLudiPagerAdapter(fragmentPairs: MutableList<Pair<String, Fragment>>) {
        ludiPagerAdapter?.addFragments(fragmentPairs)
        ludiPagerAdapter?.addRealmIdArg(teamId)
        viewPager?.adapter = ludiPagerAdapter!!
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = ludiPagerAdapter?.fragments?.get(position)?.first
        }.attach()
    }

}

