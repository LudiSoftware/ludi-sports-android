package io.usys.report.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.usys.report.R
import io.usys.report.ui.fragments.LudiPagerAdapter
import io.usys.report.utils.*

class LudiViewGroup(context: Context) : ViewGroup(context) {

    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var lvg = this
    private var parentFragment: Fragment? = null
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    var ludiePagerAdapter: LudiPagerAdapter? = null

    override fun onViewAdded(child: View?) {
        tabLayout = this.bind(R.id.ludiTabLayout)
        viewPager = this.bind(R.id.ludiViewPager)
        tabLayout.isNestedScrollingEnabled = true
        tabLayout.tabMode = TabLayout.MODE_FIXED
        viewPager.isUserInputEnabled = false
    }

    fun setParentFragment(fragment: Fragment) {
        parentFragment = fragment
        ludiePagerAdapter = LudiPagerAdapter(parentFragment!!)
    }

    fun setFragments(fragmentPairs: MutableList<Pair<String, Fragment>>) {
        // Create an adapter to manage the fragments
        ludiePagerAdapter?.addFragments(fragmentPairs)

        // Set the adapter on the ViewPager
        viewPager.adapter = ludiePagerAdapter!!

        // Link the TabLayout to the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Set the text of the tab to the fragment's tag
            tab.text = ludiePagerAdapter!!.fragments[position].first
        }.attach()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Set the layout of child views here
        tabLayout.layout(l, t, r, t + LayoutParams.MATCH_PARENT)
        viewPager.layout(l, t + LayoutParams.MATCH_PARENT, r, b)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Measure the size of child views here
        // and set the measured dimensions with setMeasuredDimension()
        // based on the widthMeasureSpec and heightMeasureSpec

        // Measure the TabLayout
        tabLayout.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED)

        // Measure the ViewPager
        val viewPagerHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            heightMeasureSpec - tabLayout.measuredHeight,
            MeasureSpec.EXACTLY
        )
        viewPager.measure(widthMeasureSpec, viewPagerHeightMeasureSpec)

        // Set the measured dimensions
        setMeasuredDimension(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }


}