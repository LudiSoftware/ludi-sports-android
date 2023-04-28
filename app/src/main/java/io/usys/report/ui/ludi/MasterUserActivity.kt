package io.usys.report.ui.ludi

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R
import io.usys.report.firebase.fireludi.fireGetAndLoadSportsIntoSessionAsync
import io.usys.report.providers.TeamMode
import io.usys.report.providers.UserProvider
import io.usys.report.providers.notifications.createNotificationChannel


/**
 * Created by ChazzCoin : October 2022.
 */

class MasterUserActivity : AppCompatActivity() {

    var userProvider: UserProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ludiStatusBarColorWhite()
        setContentView(R.layout.activity_main)
        createNotificationChannel(this)

        // -> Base Loading of Data for the user.
        fireGetAndLoadSportsIntoSessionAsync()
        userProvider = UserProvider()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = ludiNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = createBasicAppBarNavigation()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}

inline fun FragmentActivity.onBackPressed(crossinline onBackPressed: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    })
}

inline fun Fragment.onBackPressed(crossinline onBackPressed: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    })
}

inline fun Fragment.onEnterKeyPressed(editText: EditText, crossinline onEnterPressed: () -> Unit) {
    editText.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
            onEnterPressed()
            true
        } else {
            false
        }
    }
}


const val TO_DASHBOARD = R.id.navigation_dashboard
const val TO_MANAGEMENT = R.id.navigation_management
const val TO_PROFILE = R.id.navigation_profile
const val TO_TEAM_PROFILE = R.id.navigation_team_profile
const val TO_PLAYER_PROFILE = R.id.navigation_player_profile
const val TO_ORG_PROFILE = R.id.navigation_org_profile
const val TO_ROSTER_BUILDER = R.id.navigation_roster_builder_frag
const val TO_FORMATION_BUILDER = R.id.navigation_formation_builder
const val TO_CREATE_NOTE = R.id.navigation_dual_notes


/** Ludi Nav Controller **/
fun AppCompatActivity.ludiNavController() : NavController {
    return findNavController(R.id.nav_host_fragment)
}

fun AppCompatActivity.setupAppBarNavigation(navController: NavController, appBarConfiguration: AppBarConfiguration?= null) {
    if (appBarConfiguration == null) {
        this.setupActionBarWithNavController(navController, createBasicAppBarNavigation())
    } else this.setupActionBarWithNavController(navController, appBarConfiguration)
}
fun createBasicAppBarNavigation(): AppBarConfiguration {
    return AppBarConfiguration(topLevelDestinationIds = setOf(TO_MANAGEMENT, TO_DASHBOARD, TO_PROFILE))
}

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