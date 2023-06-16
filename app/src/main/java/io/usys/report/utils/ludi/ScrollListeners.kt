package io.usys.report.utils.ludi

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.utils.views.slideDown
import io.usys.report.utils.views.slideUp

/**
 * Created by ChazzCoin : June 2023.
 *
 * - HEADER SCROLL LISTENERS
 *      There are helpers to Hide/Show the Top Header View of a layout based on Scroll Events.
 */

class HeaderViewScrollListener(private val hideShowView: View) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        // Check if the first item is visible and user is scrolling up
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && dy < 0) {
            hideShowView.slideDown()
        }
        // Check if the first item is visible and user is scrolling down
        else if (layoutManager.findFirstCompletelyVisibleItemPosition() >= 2 && dy > 0) {
            hideShowView.slideUp()
        }
    }
}

class NestedScrollViewScrollListener(private val hideShowView: View) : NestedScrollView.OnScrollChangeListener {
    override fun onScrollChange(v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val yOne = v.scrollY == 0
        val isOne = hideShowView.visibility != View.VISIBLE
        if (yOne && isOne) {
            // We are at the top of the NestedScrollView
            hideShowView.slideDown()
        } else {
            // We are not at the top of the NestedScrollView
            hideShowView.slideUp()
        }
    }
}