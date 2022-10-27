package io.usys.report.basicUser

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.usys.report.R
import io.usys.report.db.getSports
import io.usys.report.model.Session
import io.usys.report.model.User

/**
 * Created by ChazzCoin : December 2019.
 */

class MainBasicUserActivity : AppCompatActivity() {

    private val user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_foodtruck)

        // -> Base Loading of Data for the user.
//        Session.removeAllSports()
        getSports()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_spot_calendar,
                R.id.navigation_dashboard,
                R.id.navigation_cart,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


}
