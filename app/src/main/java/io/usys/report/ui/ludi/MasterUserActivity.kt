package io.usys.report.ui.ludi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R
import io.usys.report.firebase.fireGetAndLoadSportsIntoSessionAsync
import io.usys.report.firebase.fireGetCoachProfileCustom
import io.usys.report.firebase.fireGetCoachProfileInBackground
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm


/**
 * Created by ChazzCoin : October 2022.
 */

class MasterUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.ysrWindowBackground)
        setContentView(R.layout.activity_main)
        val actionBar = supportActionBar
        actionBar?.setLogo(R.drawable.ludi_icon_one)
        actionBar?.setDisplayUseLogoEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // -> Base Loading of Data for the user.
        fireGetAndLoadSportsIntoSessionAsync()
//        val realm = realm()
//        realm.safeUser {
//            realm.fireGetCoachProfileCustom(it.id)
//        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.navigation_management,
                R.id.navigation_dashboard,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }



}

