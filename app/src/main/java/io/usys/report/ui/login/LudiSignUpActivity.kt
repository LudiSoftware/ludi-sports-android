package io.usys.report.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.firebase.fireSyncUserWithDatabase
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.fromFirebaseToRealmUser
import io.usys.report.utils.launchActivity
import io.usys.report.utils.log
import io.usys.report.utils.fairRegisterActivityResult
import io.usys.report.utils.hideLudiActionBar

/**
 * Created by ChazzCoin : October 2022.
 */

class LudiSignUpActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var userName: EditText? = null
    var password: EditText? = null
    var btnSignIn: Button? = null

    private var userTypeSelection: String = ""
    var checkCoach: CheckBox? = null
    var checkParent: CheckBox? = null
    var checkBasic: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        userName = findViewById<EditText>(R.id.signUpEditUsername)
        password = findViewById<EditText>(R.id.signUpEditPassword)
        btnSignIn = findViewById<Button>(R.id.signUpBtnLogin)

        setupCheckboxes()


        btnSignIn?.setOnClickListener {
            if (isUsernamePasswordValid()) {
                checkIfEmailExists()
            }
        }
        supportActionBar?.title = ""
        hideLudiActionBar()
    }

    private fun setupCheckboxes() {
        checkCoach = findViewById(R.id.signUpCheckCoach)
        checkParent = findViewById(R.id.signUpCheckParent)
        checkBasic = findViewById(R.id.signUpCheckBasic)

        checkCoach?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkParent?.isChecked = false
                checkBasic?.isChecked = false
                userTypeSelection = "Coach"
            } else if (!checkParent!!.isChecked && !checkBasic!!.isChecked) {
                userTypeSelection = ""
            }
        }

        checkParent?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkCoach?.isChecked = false
                checkBasic?.isChecked = false
                userTypeSelection = "Parent"
            } else if (!checkCoach!!.isChecked && !checkBasic!!.isChecked) {
                userTypeSelection = ""
            }
        }

        checkBasic?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkCoach?.isChecked = false
                checkParent?.isChecked = false
                userTypeSelection = "Basic"
            } else if (!checkCoach!!.isChecked && !checkParent!!.isChecked) {
                userTypeSelection = ""
            }
        }
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
                        onSignUpAndSignInResult()
                    } else {
                        launchActivity<ProviderLoginActivity>()
                    }
                } else {
                    log("Error checking email existence")
                }
            }
    }

    //
    private fun onSignUpAndSignInResult() {
        val email = userName?.text.toString()
        val passw = password?.text.toString()

        auth?.createUserWithEmailAndPassword(email, passw)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, sign in the user
                    log("createUserWithEmail:success")
                    auth?.signInWithEmailAndPassword(email, passw)
                        ?.addOnCompleteListener(this) { signInTask ->
                            if (signInTask.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                log("signInWithEmail:success")
                                handleFireUserLogin(auth?.currentUser)
                            } else {
                                // If sign in fails, display a message to the user.
                                log("signInWithEmail:failure")
                                userName?.setText("")
                                password?.setText("")
                            }
                        }
                } else {
                    // If sign up fails, display a message to the user.
                    log("createUserWithEmail:failure")
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


