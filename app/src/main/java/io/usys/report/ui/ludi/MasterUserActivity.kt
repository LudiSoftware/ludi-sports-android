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
import io.usys.report.firebase.fireGetAndLoadSportsIntoSessionAsync


/**
 * Created by ChazzCoin : October 2022.
 */

class MasterUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ludiStatusBarColorWhite()
        setContentView(R.layout.activity_main)

//        val toolbar = LayoutInflater.from(this).inflate(R.layout.ysr_toolbar, null)
//        val tb = toolbar.findViewById<androidx.appcompat.widget.Toolbar>(R.id.ysrToolBar)
//        setSupportActionBar(tb)

//        val actionBar = supportActionBar
//        actionBar?.setLogo(R.drawable.ludi_icon_one)
//        actionBar?.setDisplayUseLogoEnabled(false)
//        actionBar?.setDisplayShowHomeEnabled(false)
//        actionBar?.setDisplayHomeAsUpEnabled(false)

        // -> Base Loading of Data for the user.
        fireGetAndLoadSportsIntoSessionAsync()

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
fun Fragment.ludiStatusBarTeamTryoutMode() {
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
fun AppCompatActivity.ludiStatusBarTeamTryoutMode() {
    window.statusBarColor = ContextCompat.getColor(this, R.color.ysrFadedRed)
}
fun AppCompatActivity.ludiStatusBarTeamInSeasonMode() {
    window.statusBarColor = ContextCompat.getColor(this, R.color.ysrFadedBlue)
}