package io.usys.report.ui.login

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.R
import io.usys.report.firebase.FirePaths
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.coreFireUpdateProfileImageUri
import io.usys.report.firebase.fireUploadToStorage
import io.usys.report.firebase.fireludi.fireSaveUserToFirebaseAsync
import io.usys.report.firebase.getUserProfileImgRef
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.createCoach
import io.usys.report.realm.model.users.executeCreateUserObject
import io.usys.report.realm.model.users.getUserId
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.views.hideLudiActionBar
import io.usys.report.utils.fairGetPickImageFromGalleryIntent
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.launchActivity
import io.usys.report.utils.log
import io.usys.report.utils.toast
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : October 2022.
 */

class LudiSignUpActivity : AppCompatActivity() {

    val realmInstance = realm()

    var auth: FirebaseAuth? = null
    var name: EditText? = null
    var userName: EditText? = null
    var password: EditText? = null
    var btnSignIn: Button? = null
    var imgProfile: CircleImageView? = null
    var btnImgProfile: Button? = null
    var imageUri: Uri? = null

    var nameInput: String = ""
    var emailInput: String = ""
    var passwordInput: String = ""

    var pickImageIntent: ActivityResultLauncher<PickVisualMediaRequest>? = null

    private var userTypeSelection: String = ""
    var checkCoach: CheckBox? = null
    var isCoach: Boolean = false
    var checkParent: CheckBox? = null
    var isParent: Boolean = false
    var checkBasic: CheckBox? = null
    var isBasic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        imgProfile = findViewById(R.id.signUpImgProfile)
        btnImgProfile = findViewById(R.id.signUpBtnImgProfile)
        name = findViewById(R.id.signUpEditName)
        userName = findViewById(R.id.signUpEditUsername)
        password = findViewById(R.id.signUpEditPassword)
        btnSignIn = findViewById(R.id.signUpBtnLogin)

        setupCheckboxes()

        btnSignIn?.setOnClickListener {
            if (isUsernamePasswordValid()) {
                onSignUpAndSignInResult()
            } else {
                toast("Please enter a valid email and password")
            }
        }

        btnImgProfile?.setOnClickListener {
            toast("Coming soon!")
            launchPickImageIntent()
        }

        //Create Initial Intent for Uploading Image.
        pickImageIntent = fairGetPickImageFromGalleryIntent { itUri ->
            log(itUri)
            imageUri = itUri
            imgProfile?.loadUriIntoImgView(imageUri.toString())
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
                isCoach = true
            } else if (!checkParent!!.isChecked && !checkBasic!!.isChecked) {
                userTypeSelection = ""
            }
        }

        checkParent?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkCoach?.isChecked = false
                checkBasic?.isChecked = false
                userTypeSelection = "Parent"
                isParent = true
            } else if (!checkCoach!!.isChecked && !checkBasic!!.isChecked) {
                userTypeSelection = ""
            }
        }

        checkBasic?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkCoach?.isChecked = false
                checkParent?.isChecked = false
                userTypeSelection = "Basic"
                isBasic = true
            } else if (!checkCoach!!.isChecked && !checkParent!!.isChecked) {
                userTypeSelection = ""
            }
        }
    }


    private fun isUsernamePasswordValid(): Boolean {
        nameInput = name?.text.toString().trim()
        emailInput = userName?.text.toString().trim()
        passwordInput = password?.text.toString()

        if (nameInput.isBlank()) {
            toast("Name cannot be empty")
            return false
        }

        if (emailInput.isBlank() || passwordInput.isBlank()) {
            toast("Email or Password cannot be empty")
            return false
        }
        return true
    }

    //
    private fun onSignUpAndSignInResult() {
        auth?.createUserWithEmailAndPassword(emailInput, passwordInput)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, sign in the user
                    log("createUserWithEmail:success")
                    auth?.signInWithEmailAndPassword(emailInput, passwordInput)
                        ?.addOnCompleteListener(this) { signInTask ->
                            if (signInTask.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                log("signInWithEmail:success")
                                handleFireUserSignUp(auth?.currentUser)
                            } else {
                                // If sign in fails, display a message to the user.
                                log("signInWithEmail:failure")
                                resetUserInputs()
                            }
                        }
                } else {
                    // If sign up fails, display a message to the user.
                    log("createUserWithEmail:failure")
                    resetUserInputs()
                }
            }
    }

    private fun resetUserInputs() {
        name?.setText("")
        userName?.setText("")
        password?.setText("")
    }

    private fun handleFireUserSignUp(firebaseUser: FirebaseUser?) {
        val user = firebaseUser.fromFirebaseToRealmUserSignUp()
        if (userTypeSelection == "Coach") realm().createCoach(user, saveToFirebase = true)
        launchActivity<AuthControllerActivity>()
    }

    private fun FirebaseUser?.fromFirebaseToRealmUserSignUp() : User {
        if (this.isNullOrEmpty()) return User()
        val uid = this?.uid ?: "UNKNOWN"
        val email = this?.email ?: "UNKNOWN"
        val name = if (this?.displayName.isNullOrBlank()) nameInput else this?.displayName
        val photoUrl = this?.photoUrl ?: "UNKNOWN"
        val emailVerified = this?.isEmailVerified ?: false
        executeCreateUserObject(uid)
        val user = User()
        realmInstance.safeWrite {
            user.apply {
                this.id = uid
                this.email = email
                this.name = name
                this.photoUrl = photoUrl.toString()
                this.imgUrl = photoUrl.toString()
                this.emailVerified = emailVerified
                this.coach = isCoach
                this.parent = isParent
                this.player = false
            }
        }

//        imageUri?.fireUploadToStorage(this@LudiSignUpActivity, FirePaths.PROFILE_IMAGE_PATH_BY_ID(FireTypes.USERS))
        fireSaveUserToFirebaseAsync(user)
        realmInstance.safeWrite {
            realmInstance.insertOrUpdate(user)
        }
        return user
    }


    // Image Upload
    private fun launchPickImageIntent(){
        pickImageIntent?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}


