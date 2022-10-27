package io.usys.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.usys.report.model.User

/**
 * Created by ChazzCoin : December 2019.
 */

class MainAdminActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_admin)

//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            topLevelDestinationIds = setOf(
//                R.id.navigation_spot_calendar, R.id.navigation_dashboard, R.id.navigation_cart
//            )
//        )

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

    }


}
