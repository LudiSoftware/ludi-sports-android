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
import io.usys.report.AuthController
import io.usys.report.R
import io.usys.report.model.AuthTypes
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.utils.FireHelper

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
    //Ask for auth first -> if null add waiting
    private fun getUserAuthFromFirebase(currentUser: User) {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.USERS).child(currentUser.id.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
//                    mUser = User(currentUser.uid,currentUser.displayName,currentUser.email)
                    user?.let { itUser ->
                        saveProfileToFirebase()
                    }?: kotlin.run {
                        //No User Move On
                        mUser.auth = AuthTypes.BASIC_USER
                        saveProfileToFirebase()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
//                    TODO("not implemented")
                }
            })
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
    fun saveProfileToFirebase() {
        database.child(FireHelper.USERS).child(mUser.id.toString()).setValue(mUser)
            .addOnSuccessListener {
//                TODO("HANDLE SUCCESS")
                Session.updateUser(mUser)
                Session.updateSession(mUser)
                startActivity(Intent(this@LoginActivity, AuthController::class.java))
            }.addOnCompleteListener {
//                TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
//                TODO("HANDLE FAILURE")
//                showLoginFailed()
            }
    }

    private fun showLoginFailed() {
        Toast.makeText(applicationContext, "Error Signing In", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //TODO: SETUP USER MODEL
                response?.let {
                    val fireUser = FirebaseAuth.getInstance().currentUser
                    val uid = fireUser?.uid ?: "unknown"
                    val email = fireUser?.email
                    val name = fireUser?.displayName
                    val user = User()
                    user.id = uid
                    user.email = email
                    user.name = name
                    mUser = user
                    saveProfileToFirebase()
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


