package io.usys.report.utils.views

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import io.usys.report.R

fun View.fadeInOut(otherView: View, duration: Long) {
    animate().alpha(0f).setDuration(duration).withEndAction {
        visibility = View.GONE
    }
    otherView.alpha = 0f
    otherView.visibility = View.VISIBLE
    otherView.animate().alpha(1f).setDuration(duration).start()
}

//fun View.lockToTopOfScrollView() {
//    val parentScrollView = findParentOfType<NestedScrollView>()
//    val topAppBar = getTopAppBar()
//    val topInset = getTopInset()
//
//    parentScrollView?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
//        val topOffset = parentScrollView.y + y
//        val lockToTop = topAppBar != null && topOffset < topAppBar.y + topAppBar.height + topInset
//        if (scrollY > topOffset && !lockToTop) {
//            y = scrollY - topOffset
//        } else {
//            y = if (lockToTop) topAppBar.y + topAppBar.height + topInset - y else 0f
//        }
//    }
//}

inline fun <reified T : View> View.findParentOfType(): T? {
    var parent = parent
    while (parent != null) {
        if (parent is T) {
            return parent
        }
        parent = parent.parent
    }
    return null
}

//fun View.getTopAppBar(): View? {
//    val activity = context as? AppCompatActivity ?: return null
//    return activity.findViewById<View>(R.id.appBarLayout) ?: activity.findViewById<View>(R.id.app)
//}

fun View.getTopInset(): Int {
    val windowInsets = rootWindowInsets ?: return 0
    return windowInsets.systemWindowInsetTop
}