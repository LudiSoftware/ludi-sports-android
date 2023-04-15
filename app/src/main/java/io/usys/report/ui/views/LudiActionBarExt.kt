package io.usys.report.ui.views

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.ui.ludi.ludiStatusBarColorWhite
import io.usys.report.ui.ludi.ludiStatusBarTeamInSeasonMode
import io.usys.report.ui.ludi.ludiStatusBarTeamTryoutMode


/** Ludi Action Bar **/
fun AppCompatActivity.ludiActionBar(): ActionBar? {
    return this.supportActionBar
}

fun Activity.ludiActionBar(): ActionBar? {
    return (this as AppCompatActivity).supportActionBar
}

fun Activity.hideLudiActionBar() {
    this.ludiActionBar()?.hide()
}

fun Activity.showLudiActionBar() {
    this.ludiActionBar()?.show()
}

fun Fragment.hideLudiActionBar() {
    this.requireActivity().hideLudiActionBar()
}
fun Fragment.showLudiActionBar() {
    this.requireActivity().showLudiActionBar()
}

fun Fragment.ludiActionBar() : ActionBar? {
    return this.requireActivity().ludiActionBar()
}

fun ActionBar.resetColor(@ColorRes color: Int= R.color.ludiWhite) {
    this.setBackgroundDrawable(ContextCompat.getDrawable(this.themedContext, color))
}
fun ActionBar.tryoutMode(@ColorRes color: Int= R.color.ludiWhite) {
    this.setBackgroundDrawable(ContextCompat.getDrawable(this.themedContext, color))
}
fun ActionBar.inSeasonMode(@ColorRes color: Int= R.color.ludiWhite) {
    this.setBackgroundDrawable(ContextCompat.getDrawable(this.themedContext, color))
}
fun AppCompatActivity.ludiActionBarTryoutMode() {
    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.ysrFadedRed))
    this.ludiActionBarTitle("Tryout Mode")
    this.ludiStatusBarTeamTryoutMode()
}
fun AppCompatActivity.ludiActionBarInSeasonMode() {
    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.ysrFadedBlue))
    this.ludiActionBarTitle("In Season Mode")
    this.ludiStatusBarTeamInSeasonMode()
}

fun AppCompatActivity.ludiActionBarHome(title: String="Home") {
    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.ysrFadedBlue))
    this.ludiActionBarTitle(title)
    this.ludiStatusBarColorWhite()
}
fun AppCompatActivity.ludiActionBarTitle(title: String) {
    supportActionBar?.title = title
}