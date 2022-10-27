package io.usys.report

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration
import io.usys.report.basicUser.MainBasicUserActivity
import io.usys.report.model.*
import io.usys.report.ui.login.LoginActivity
import io.usys.report.utils.FireHelper
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.showFailedToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


/**
 * Created by ChazzCoin : October 2022.
 * release key pw: M4BOfzfVr4ymXwI
 */

class AuthController : AppCompatActivity()  {

    companion object {
        var USER_ID = ""
        var USER_AUTH = ""
    }

    private val mInstance: AuthController? = null
    private lateinit var database: DatabaseReference
    val main = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var mUser : User? = null
    lateinit var id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //Init Firebase Instance
        FirebaseApp.initializeApp(this)
        //Init Calendar
        AndroidThreeTen.init(this)
        //Init Realm DB
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(BuildConfig.APPLICATION_ID + ".realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
//        Realm.deleteRealm(realmConfiguration)
//        Realm.setDefaultConfiguration(realmConfiguration)
        //Finally . . .
//        Session.deleteUser()
//        Session.deleteAll()
        doSetup()
//        startActivity(Intent(this@AuthController, LoginActivity::class.java))
    }

    private fun doSetup() {
        val u = Session.user?.id
        if (Session.session != null && Session.user != null && !u.isNullOrEmpty()){
            mUser = Session.user
            mUser?.let { itUser ->
                id = itUser.id.toString()
                USER_ID = itUser.id.toString()
                USER_AUTH = itUser.auth
                getProfileUpdatesFirebase()
            }
        } else {
            //Create User Object (checks if null)
            Session.createObjects()
            startActivity(Intent(this@AuthController, LoginActivity::class.java))
        }
    }

    @Synchronized
    fun getInstance(): AuthController? {
        return mInstance
    }

    override fun onRestart() {
        super.onRestart()
        doSetup()
    }

    private fun getProfileUpdatesFirebase() {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.USERS).child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val temp: User? = dataSnapshot.getValue(User::class.java) as User?
                    temp?.let { it ->
                        if (it.id == id){
                            USER_AUTH = it.auth.toString()
                            USER_ID = it.id.toString()
                            Session.updateUser(it)
                            navigateUser(it)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    showFailedToast(this@AuthController)
                }
            })
    }

    private fun navigateUser(user: User){
        when (user.auth) {
            AuthTypes.BASIC_USER -> {
                startActivity(Intent(this@AuthController, MainBasicUserActivity::class.java))
            }
            AuthTypes.COACH_USER -> {
                startActivity(Intent(this@AuthController, MainBasicUserActivity::class.java))
            }
            else -> {
                Toast.makeText(this, "Pending User Approval", Toast.LENGTH_LONG).show()
                //TODO: CREATE TEMP PAGE FOR WAITING USERS
                startActivity(Intent(this@AuthController, MainPendingActivity::class.java))
            }
        }
    }

}
