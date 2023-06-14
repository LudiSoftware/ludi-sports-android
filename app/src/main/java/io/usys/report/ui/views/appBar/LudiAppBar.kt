package io.usys.report.ui.views.appBar

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import io.usys.report.ui.views.navController.TO_DASHBOARD
import io.usys.report.ui.views.navController.TO_MANAGEMENT
import io.usys.report.ui.views.navController.TO_PROFILE


fun createBasicAppBarNavigation(): AppBarConfiguration {
    return AppBarConfiguration(topLevelDestinationIds = setOf(TO_MANAGEMENT, TO_DASHBOARD, TO_PROFILE))
}


fun AppCompatActivity.setupAppBarNavigation(navController: NavController, appBarConfiguration: AppBarConfiguration?= null) {
    if (appBarConfiguration == null) {
        this.setupActionBarWithNavController(navController, createBasicAppBarNavigation())
    } else this.setupActionBarWithNavController(navController, appBarConfiguration)
}