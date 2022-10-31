package io.usys.report.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration
import io.usys.report.BuildConfig
import io.usys.report.R
import io.usys.report.db.getUserUpdatesFromFirebase
import io.usys.report.model.*
import io.usys.report.ui.login.LoginActivity
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.launchActivity


/**
 * Created by ChazzCoin : October 2022.
 * release key pw: M4BOfzfVr4ymXwI
 */

class AuthControllerActivity : AppCompatActivity()  {

    companion object {
        var USER_ID = ""
        var USER_AUTH = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //Initialize
        initializeDependencies()
        //Setup
        verifyUserLogin()
    }

    private fun initializeDependencies() {
        //Init Firebase Instance
        FirebaseApp.initializeApp(this)
        //Init Calendar
        AndroidThreeTen.init(this)
        //Init Realm DB
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(BuildConfig.APPLICATION_ID + ".realm2")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }

    private fun verifyUserLogin() {
        val u = Session.getCurrentUser()
        if (!u.isNullOrEmpty()) {
            u?.let { itUser ->
                USER_AUTH = itUser.auth
                itUser.id?.let { itId ->
                    getUserUpdatesFromFirebase(itId) {
                        navigateUser(it)
                    }
                }
            }
        } else {
            Session.createObjects()
            launchActivity<LoginActivity>()
        }
    }

    override fun onRestart() {
        super.onRestart()
        verifyUserLogin()
    }

    private fun navigateUser(user: User?){
        if (user.isNullOrEmpty()) launchActivity<MainBasicUserActivity>()
        when (user?.auth) {
            AuthTypes.BASIC_USER -> {
                launchActivity<MainBasicUserActivity>()
            }
            AuthTypes.COACH_USER -> {
                launchActivity<MainBasicUserActivity>()
            }
            else -> {
                Toast.makeText(this, "Pending User Approval", Toast.LENGTH_LONG).show()
                //TODO: CREATE TEMP PAGE FOR WAITING USERS
                launchActivity<MainPendingActivity>()
            }
        }
    }

}

