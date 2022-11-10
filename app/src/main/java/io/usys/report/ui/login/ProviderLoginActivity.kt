package io.usys.report.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.firebase.fireSaveUserToFirebaseAsync
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.model.toYsrRealmUser
import io.usys.report.utils.launchActivity
import io.usys.report.utils.log
import io.usys.report.utils.fairRegisterActivityResult

/**
 * Created by ChazzCoin : October 2022.
 */

class ProviderLoginActivity : AppCompatActivity() {

    // Choose authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)


        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.usysr_logo) // Set logo drawable
            .setTheme(R.style.YSR) // Set theme
            .setIsSmartLockEnabled(true)
            .build()
        fairRegisterActivityResult<FirebaseAuthUIAuthenticationResult>(signInIntent) { res ->
            this.onSignInResult(res)
        }

    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        log(response)
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val fireUser = FirebaseAuth.getInstance().currentUser
            handleFireUser(fireUser)
            // ...
        } else {
            log("Login Failed!")
            launchActivity<AuthControllerActivity>()
        }
    }

    private fun handleFireUser(firebaseUser: FirebaseUser?) {
        val user = firebaseUser.toYsrRealmUser()
        saveProfileToFirebaseUI(user)
    }

    //SAVE PROFILE
    private fun saveProfileToFirebaseUI(user:User?) {
        fireSaveUserToFirebaseAsync(user) {
            log("Profile Updated! $it")
            if (user != null) {
                Session.updateUser(user)
                Session.updateSession(user)
            }
            launchActivity<AuthControllerActivity>()
        }
    }

}


