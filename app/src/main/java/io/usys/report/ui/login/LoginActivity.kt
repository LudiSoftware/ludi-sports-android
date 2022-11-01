package io.usys.report.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.model.parseFromFirebaseUser
import io.usys.report.utils.FireHelper
import io.usys.report.utils.isNullOrEmpty

/**
 * Created by ChazzCoin : October 2022.
 */

class LoginActivity : AppCompatActivity() {

    private var COUNT = 0
    private val RC_SIGN_IN: Int = 999

    private lateinit var database: DatabaseReference
    private lateinit var mUser : User

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        database = FirebaseDatabase.getInstance().reference

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
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

    //SAVE PROFILE
    fun saveProfileToFirebase(user:User?) {
        if (user.isNullOrEmpty()) return
        user?.let {
            database.child(FireHelper.USERS).child(it.id).setValue(mUser)
                .addOnSuccessListener {
//                TODO("HANDLE SUCCESS")
                    Session.updateUser(mUser)
                    Session.updateSession(mUser)
                    startActivity(Intent(this@LoginActivity, AuthControllerActivity::class.java))
                }.addOnCompleteListener {
//                TODO("HANDLE COMPLETE")
                }.addOnFailureListener {
//                TODO("HANDLE FAILURE")
//                showLoginFailed()
                }
        }

    }

    private fun showLoginFailed() {
        Toast.makeText(applicationContext, "Error Signing In", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //TODO: SETUP USER MODEL
                response?.let {
                    val fireUser = FirebaseAuth.getInstance().currentUser
                    mUser = parseFromFirebaseUser(fireUser = fireUser)
                    saveProfileToFirebase(mUser)
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


}


