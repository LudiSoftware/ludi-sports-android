package io.usys.report.ui.views

import android.animation.ObjectAnimator
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
import io.usys.report.ui.ludi.evaluation.CreateEvaluationFragment
import io.usys.report.ui.ludi.note.CreateNoteFragment
import io.usys.report.ui.ludi.player.DetailsListFragment
import io.usys.report.ui.ludi.player.PlayerProfileFragment
import io.usys.report.ui.ludi.team.ViewTeamNotesFragment
import io.usys.report.ui.ludi.team.ViewTeamRosterFragment
import io.usys.report.utils.log

fun ludiTeamVGFragments(): MutableList<Pair<String, Fragment>> {
    return mutableListOf<Pair<String, Fragment>>().apply {
        add(Pair("Roster", ViewTeamRosterFragment()))
        add(Pair("Notes", ViewTeamNotesFragment()))
        add(Pair("Chat", ChatFragment()))
    }
}

fun ludiNotesAndEvalsVGFragments(): MutableList<Pair<String, Fragment>> {
    return mutableListOf<Pair<String, Fragment>>().apply {
        add(Pair("Evaluation", CreateEvaluationFragment()))
        add(Pair("Notes", CreateNoteFragment()))
    }
}

fun ludiPlayerProfileFragments(): MutableList<Pair<String, Fragment>> {
    return mutableListOf<Pair<String, Fragment>>().apply {
        add(Pair("Details", DetailsListFragment()))
        add(Pair("Notes", CreateNoteFragment()))
    }
}

class LudiViewGroup(parentFragment: Fragment, rootView: LinearLayout) {

    var inflater: LayoutInflater? = null
    var rootView: LinearLayout? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager2? = null

    var teamId: String? = null
    var playerId: String? = null
    var orgId: String? = null
    var ludiPagerAdapter: LudiPagerAdapter? = null
    var mview: View? = null

    init {
        inflater = LayoutInflater.from(parentFragment.requireContext())
        this.rootView = rootView
        mview = inflater?.inflate(R.layout.ludi_view_group, rootView, false)
        rootView.addView(mview)
        ludiPagerAdapter = LudiPagerAdapter(parentFragment)
        log("LudiViewGroup: init")
    }

    constructor(parentFragment: Fragment, rootView: LinearLayout, teamId: String?, playerId:String?=null, orgId:String?=null) : this(parentFragment, rootView) {
        this.teamId = teamId
        this.playerId = playerId
        this.orgId = orgId
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
        ludiPagerAdapter?.addStringIdArgs(teamId, playerId, orgId)
        viewPager?.adapter = ludiPagerAdapter!!

        val tlm = TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = ludiPagerAdapter?.fragments?.get(position)?.first
        }

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Animate the tab at the given position
                val tabView = tabLayout?.getTabAt(position)?.view
                tabView?.let {
                    ObjectAnimator.ofFloat(it, "scaleX", 1.0f, 1.2f, 1.0f).apply {
                        duration = 500
                        start()
                    }
                    ObjectAnimator.ofFloat(it, "scaleY", 1.0f, 1.2f, 1.0f).apply {
                        duration = 500
                        start()
                    }
                }
            }
        })

        tlm.attach()

    }

}

