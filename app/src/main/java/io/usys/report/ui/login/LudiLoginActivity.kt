package io.usys.report.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.firebase.fireludi.fireSyncUserWithDatabase
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.fromFirebaseToRealmUserLogin
import io.usys.report.ui.views.hideLudiActionBar
import io.usys.report.utils.*
import io.usys.report.utils.androidx.launchActivity
import io.usys.report.utils.views.makeGone
import io.usys.report.utils.views.makeRed
import io.usys.report.utils.views.makeVisible
import io.usys.report.utils.views.onEnterKeyPressed

/**
 * Created by ChazzCoin : October 2022.
 */

class LudiLoginActivity : AppCompatActivity() {

    var emailInput: String = ""
    var passwordInput: String = ""

    var auth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    var userName: EditText? = null
    var password: EditText? = null
    var btnSignIn: Button? = null
    var btnSignUp: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        progressBar = findViewById(R.id.loginProgressBar)
        hideProgressBar()
        userName = findViewById(R.id.editUsername)
        password = findViewById(R.id.editPassword)
        btnSignIn = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnLoginSignUp)

        btnSignUp?.setOnClickListener {
            launchActivity<LudiSignUpActivity>()
        }

        btnSignIn?.setOnClickListener {
            doLogin()
        }

        password?.onEnterKeyPressed {
            doLogin()
        }

        supportActionBar?.title = ""
        hideLudiActionBar()
    }

    private fun showProgressBar() {
        progressBar?.makeVisible()
    }
    private fun hideProgressBar() {
        progressBar?.makeGone()
    }

    private fun doLogin() {
        showProgressBar()
        if (isUsernamePasswordValid()) {
            checkIfEmailExists()
        } else {
            hideProgressBar()
        }
    }

    private fun isUsernamePasswordValid(): Boolean {
        emailInput = userName?.text.toString().trim()
        passwordInput = password?.text.toString().trim()

        if (emailInput == "demo" && passwordInput == "demo") {
            emailInput = resources?.getString(R.string.demo_e).toString()
            passwordInput = resources?.getString(R.string.demo_p).toString()
        }

        if (emailInput.isBlank()) {
            userName?.makeRed()
            log("Email or Password cannot be empty")
            return false
        }
        if (passwordInput.isBlank()) {
            password?.makeRed()
            log("Email or Password cannot be empty")
            return false
        }
        return true
    }

    private fun checkIfEmailExists() {
        if (emailInput.isBlank()) return
        auth?.fetchSignInMethodsForEmail(emailInput)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        onSignInResult()
                    } else {
                        hideProgressBar()
                        launchActivity<ProviderLoginActivity>()
                    }
                } else {
                    log("Error checking email existence")
                }
            }
    }

    private fun onSignInResult() {
        auth?.signInWithEmailAndPassword(emailInput, passwordInput)
            ?.addOnCompleteListener(this) { task ->
                hideProgressBar()
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
        val user = firebaseUser.fromFirebaseToRealmUserLogin()
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


