package io.usys.report.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import io.realm.Realm
import io.realm.RealmConfiguration
import io.usys.report.BuildConfig
import io.usys.report.R
import io.usys.report.firebase.fireGetUserUpdatesFromFirebaseAsync
import io.usys.report.model.*
import io.usys.report.ui.login.ProviderLoginActivity
import io.usys.report.ui.ysr.MasterUserActivity
import io.usys.report.utils.AuthTypes
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.launchActivity
import io.usys.report.utils.session


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
    }

    override fun onStart() {
        super.onStart()
        //Setup
        verifyUserLogin()
    }

    private fun initializeDependencies() {
        //Init Firebase Instance
        FirebaseApp.initializeApp(this)
        //Init Calendar
//        AndroidThreeTen.init(this)
        //Init Realm DB
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(BuildConfig.APPLICATION_ID + ".realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
//        Session.deleteAllRealmObjects()
    }

    private fun verifyUserLogin() {
        safeUser {
            fireGetUserUpdatesFromFirebaseAsync(it.id) {
                navigateUser(it)
            }
            return
        }
        Session.createObjects()
        launchActivity<ProviderLoginActivity>()
    }

    override fun onRestart() {
        super.onRestart()
        verifyUserLogin()
    }

    private fun navigateUser(user: User?){
        if (user.isNullOrEmpty()) launchActivity<MasterUserActivity>()
        when (user?.auth) {
            AuthTypes.BASIC_USER -> {
                launchActivity<MasterUserActivity>()
            }
            AuthTypes.COACH_USER -> {
                launchActivity<MasterUserActivity>()
            }
            else -> {
                Toast.makeText(this, "Pending User Approval", Toast.LENGTH_LONG).show()
                //TODO: CREATE TEMP PAGE FOR WAITING USERS
                launchActivity<MainPendingActivity>()
            }
        }
    }

}

