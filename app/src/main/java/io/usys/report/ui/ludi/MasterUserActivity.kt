package io.usys.report.ui.ludi

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        ludiStatusBarColor()
        setContentView(R.layout.activity_main)
        val actionBar = supportActionBar
        actionBar?.setLogo(R.drawable.ludi_icon_one)
        actionBar?.setDisplayUseLogoEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)

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

const val TO_DASHBOARD = R.id.navigation_dashboard
const val TO_MANAGEMENT = R.id.navigation_management
const val TO_PROFILE = R.id.navigation_profile
const val TO_TEAM_PROFILE = R.id.navigation_team_profile
const val TO_PLAYER_PROFILE = R.id.navigation_player_profile
const val TO_ORG_PROFILE = R.id.navigation_org_profile
const val TO_ROSTER_BUILDER = R.id.navigation_roster_builder_frag
const val TO_FORMATION_BUILDER = R.id.navigation_formation_builder

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
fun AppCompatActivity.ludiStatusBarColor(@ColorRes color: Int= R.color.ysrWindowBackground) {
    window.statusBarColor = ContextCompat.getColor(this, color)
}

