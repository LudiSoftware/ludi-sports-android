package io.usys.report.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import io.realm.Realm
import io.realm.RealmConfiguration
import io.usys.report.BuildConfig
import io.usys.report.R
import io.usys.report.realm.model.Session
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm
import io.usys.report.ui.login.ProviderLoginActivity
import io.usys.report.ui.ysr.MasterUserActivity
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.launchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Created by ChazzCoin : October 2022.
 * release key pw: M4BOfzfVr4ymXwI
 */

class AuthControllerActivity : AppCompatActivity()  {


    companion object {
        var HAS_INITIALIZED = false
        private val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val realmThread = CoroutineScope(singleThreadExecutor.asCoroutineDispatcher())
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
        if (HAS_INITIALIZED) return
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
        Session.createObjects()
        HAS_INITIALIZED = true
    }

    // https://firebasestorage.googleapis.com/v0/b/usysr-a16ef.appspot.com/o/users%2FtnmjTR7r1HPwIaBb2oXrDrwXT842%2Fprofile%2Fprofile_image.jpg?alt=media&token=c5715968-19f0-4dee-b65a-3c722cafbfc9
    private fun verifyUserLogin() {
        realm().safeUser { itSafeUser ->
            navigateUser(itSafeUser)
            return
        }
        launchActivity<ProviderLoginActivity>()
    }

    override fun onRestart() {
        super.onRestart()
        verifyUserLogin()
    }

    private fun navigateUser(user: User?){
        if (user.isNullOrEmpty()) launchActivity<MasterUserActivity>()
        launchActivity<MasterUserActivity>()
    }

}

