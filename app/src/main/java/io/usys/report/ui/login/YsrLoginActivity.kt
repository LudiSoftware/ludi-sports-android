package io.usys.report.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.databinding.ActivityLoginBinding
import io.usys.report.firebase.fireSaveUserToFirebaseAsync
import io.usys.report.model.Session
import io.usys.report.model.User
import io.usys.report.model.toYsrRealmUser
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : October 2022.
 */

class YsrLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var viewElements: ActivityLoginBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = viewElements!!
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        viewElements = ActivityLoginBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewElements?.btnLogin?.setOnClickListener {
            val userInput = viewElements?.editUsername?.text
            val pwInput = viewElements?.editPassword?.text

        }
    }

    private fun handleFireUser() {
        val fireUser = auth.currentUser
        val user = fireUser.toYsrRealmUser()
        saveProfileToFirebase(user)
    }


    //SAVE PROFILE
    private fun saveProfileToFirebase(user:User?) {
        fireSaveUserToFirebaseAsync(user) {
            log("Profile Updated! $it")
            if (user != null) {
                Session.updateUser(user)
                Session.updateSession(user)
            }
            launchActivity<AuthControllerActivity>()
        }
    }

    // Create User
    private fun createNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    handleFireUser()
                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    // SignIn
    fun signInUser(email:String, password:String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    handleFireUser()
                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

}


