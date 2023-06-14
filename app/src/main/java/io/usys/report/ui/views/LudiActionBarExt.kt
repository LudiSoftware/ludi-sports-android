package io.usys.report.ui.views

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.providers.TeamMode
import io.usys.report.ui.views.statusBar.ludiStatusBarColorWhite
import io.usys.report.ui.views.statusBar.ludiStatusBarTeamInSeasonMode
import io.usys.report.ui.views.statusBar.ludiStatusBarTeamMode


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

fun AppCompatActivity.ludiActionBarResetColor(@ColorRes color: Int= R.color.ludiWhite) {
    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, color))
}

fun AppCompatActivity.ludiActionBarTeamMode(teamMode:TeamMode, title: String?=null) {
    supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, teamMode.color))
    this.ludiActionBarTitle(title ?: teamMode.title)
    this.ludiStatusBarTeamMode(teamMode.mode)
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