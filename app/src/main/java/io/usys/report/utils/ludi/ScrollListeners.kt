package io.usys.report.utils.ludi

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
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




/** Smooth Scrolling Slider **/
fun NestedScrollView.addLudiScrollSliderListener(view: View): LudiScrollSlideListener {
    val listener = LudiScrollSlideListener(view)
    this.setOnScrollChangeListener(listener)
    return listener
}
inline fun LudiScrollSlideListener.create(block: LudiScrollSlideListener.() -> Unit): LudiScrollSlideListener {
    block()
    return this
}
class LudiScrollSlideListener(private val view: View) : NestedScrollView.OnScrollChangeListener {

    var initialTopMargin = 0
    var parentViewId = R.id.includeLudiCardView
    var marginMax = -400
    var marginMin = 24
    private val constraintLayout: ConstraintLayout = view.parent as ConstraintLayout
    private val constraintSet: ConstraintSet = ConstraintSet().apply { clone(constraintLayout) }
    init {
        initialTopMargin = (view.layoutParams as ConstraintLayout.LayoutParams).topMargin
        parentViewId = view.context.getResourceId(view.getResourceName()!!)
    }
    override fun onScrollChange(v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val maxScroll = v.getChildAt(0).height - v.height
        val translationFactor = scrollY.toFloat() / maxScroll
        val newMargin = (initialTopMargin - translationFactor * view.height).toInt()
        val clampedNewMargin = newMargin.coerceIn(marginMax, marginMin)
        constraintSet.setMargin(parentViewId, ConstraintSet.TOP, clampedNewMargin)
        constraintSet.applyTo(constraintLayout)
    }
}

fun View.getResourceName(): String? {
    return try {
        resources.getResourceEntryName(id)
    } catch (e: Resources.NotFoundException) {
        null
    }
}

fun Context.getResourceId(resourceName: String): Int {
    return resources.getIdentifier(resourceName, "id", packageName)
}


// Original
class LudiViewScrollSlider(private val cardView: View, private val initialTopMargin: Int) : NestedScrollView.OnScrollChangeListener {

    var marginMax = -400
    var marginMin = 24
    private val constraintLayout: ConstraintLayout = cardView.parent as ConstraintLayout
    private val constraintSet: ConstraintSet = ConstraintSet().apply {
        clone(constraintLayout)
    }

    override fun onScrollChange(v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val maxScroll = v.getChildAt(0).height - v.height // The maximum scroll position of the NestedScrollView
        // Calculate the translation factor
        val translationFactor = scrollY.toFloat() / maxScroll
        // Compute the new margin and ensure it stays between -150 and 16
        val newMargin = (initialTopMargin - translationFactor * cardView.height).toInt()
        val clampedNewMargin = newMargin.coerceIn(marginMax, marginMin)
        // Apply the new margin
        constraintSet.setMargin(R.id.includeLudiCardView, ConstraintSet.TOP, clampedNewMargin)
        constraintSet.applyTo(constraintLayout)
    }
}