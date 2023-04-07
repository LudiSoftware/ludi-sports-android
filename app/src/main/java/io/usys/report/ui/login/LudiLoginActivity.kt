package io.usys.report.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.firebase.fireSyncUserWithDatabase
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.fromFirebaseToRealmUser
import io.usys.report.utils.launchActivity
import io.usys.report.utils.log
import io.usys.report.utils.hideLudiActionBar

/**
 * Created by ChazzCoin : October 2022.
 */

class LudiLoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var userName: EditText? = null
    var password: EditText? = null
    var btnSignIn: Button? = null
    var btnSignUp: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        userName = findViewById<EditText>(R.id.editUsername)
        password = findViewById<EditText>(R.id.editPassword)
        btnSignIn = findViewById<Button>(R.id.btnLogin)
        btnSignUp = findViewById<Button>(R.id.btnLoginSignUp)


        btnSignUp?.setOnClickListener {
            launchActivity<LudiSignUpActivity>()
        }

        btnSignIn?.setOnClickListener {
            if (isUsernamePasswordValid()) {
                checkIfEmailExists()
            }
        }
        supportActionBar?.title = ""
        hideLudiActionBar()
    }

    private fun isUsernamePasswordValid(): Boolean {
        val email = userName?.text.toString()
        val passw = password?.text.toString()

        if (email.isBlank() || passw.isBlank()) {
            log("Email or Password cannot be empty")
            return false
        }
        return true
    }

    private fun checkIfEmailExists() {
        val email = userName?.text.toString()
        auth?.fetchSignInMethodsForEmail(email)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        onSignInResult()
                    } else {
                        launchActivity<ProviderLoginActivity>()
                    }
                } else {
                    log("Error checking email existence")
                }
            }
    }

    //
    private fun onSignInResult() {
        val email = userName?.text.toString()
        val passw = password?.text.toString()
        auth?.signInWithEmailAndPassword(email, passw)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    log("signInWithEmail:success")
                    handleFireUserLogin(auth?.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    log("signInWithEmail:failure")
                    //clear edittexts
                    userName?.setText("")
                    password?.setText("")
                }
            }
    }

    private fun handleFireUserLogin(firebaseUser: FirebaseUser?) {
        val user = firebaseUser.fromFirebaseToRealmUser()
        saveProfileToFirebaseUI(user)
    }

    //SAVE PROFILE
    private fun saveProfileToFirebaseUI(user: User?) {
        if (user == null) launchActivity<AuthControllerActivity>()
        fireSyncUserWithDatabase(user!!) {
            launchActivity<AuthControllerActivity>()
        }
    }

}


