package io.usys.report.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.db.saveProfileToFirebaseAsync
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.model.parseFromFirebaseUser
import io.usys.report.utils.launchActivity
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class LoginActivity : AppCompatActivity() {

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            this.onSignInResult(res)
        }

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.usysr_logo) // Set logo drawable
            .setTheme(R.style.YSR) // Set theme
            .build()
        signInLauncher.launch(signInIntent)

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
        val user = parseFromFirebaseUser(firebaseUser)
        saveProfileToFirebaseUI(user)
    }

    //SAVE PROFILE
    private fun saveProfileToFirebaseUI(user:User?) {
        saveProfileToFirebaseAsync(user) {
            log("Profile Updated! $it")
            if (user != null) {
                Session.updateUser(user)
                Session.updateSession(user)
            }
            launchActivity<AuthControllerActivity>()
        }
    }

//    private fun googleTesting() {
//        val signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            .build()
//    }


}


