package io.usys.report.ui.views.statusBar

import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.providers.TeamMode

/** Ludi Status Bar **/
fun Fragment.ludiStatusBarColorWhite(@ColorRes color: Int= R.color.ysrWindowBackground) {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, color)
    }
}
fun Fragment.ludiStatusBarTeamMode() {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, R.color.ysrFadedRed)
    }
}
fun Fragment.ludiStatusBarTeamInSeasonMode() {
    (requireActivity() as AppCompatActivity).let {
        it.window.statusBarColor = ContextCompat.getColor(it, R.color.ysrFadedBlue)
    }
}
fun AppCompatActivity.ludiStatusBarColorWhite(@ColorRes color: Int= R.color.ysrWindowBackground) {
    window.statusBarColor = ContextCompat.getColor(this, color)
}
fun AppCompatActivity.ludiStatusBarTeamMode(teamMode: String) {
    val modeColor = when (teamMode) {
        TeamMode.CREATION.mode -> TeamMode.CREATION.color
        TeamMode.PRE_SEASON.mode -> TeamMode.PRE_SEASON.color
        TeamMode.IN_SEASON.mode -> TeamMode.IN_SEASON.color
        TeamMode.OFF_SEASON.mode -> TeamMode.OFF_SEASON.color
        TeamMode.TRYOUT.mode -> TeamMode.TRYOUT.color
        TeamMode.PENDING_ROSTER.mode -> TeamMode.PENDING_ROSTER.color
        else -> TeamMode.CREATION.color
    }
    window.statusBarColor = ContextCompat.getColor(this, modeColor)
}
fun AppCompatActivity.ludiStatusBarTeamInSeasonMode() {
    window.statusBarColor = ContextCompat.getColor(this, R.color.ysrFadedBlue)
}