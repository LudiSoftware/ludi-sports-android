package io.usys.report.firebase

import android.content.Context
import android.net.Uri
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.*
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */



class FireTypes {
    companion object {
        // DATE FORMATS
        const val SPOT_MONTH_DB = "MMMyyyy"
        const val SPOT_DATE_FORMAT = "yyyy-MM-d"
        const val DATE_MONTH = "MMMM"
        const val FIRE_DATE_FORMAT = "EEE, MMM d yyyy, hh:mm:ss a"
        // DATABASE ROUTES
        const val ADMIN: String = "admin"
        const val USERS: String = "users"
        const val ORGANIZATIONS: String = "organizations"
        const val REVIEWS: String = "reviews"
        const val REVIEW_TEMPLATES: String = "review_templates"
        const val SPORTS: String = "sports"
        const val COACHES: String = "coaches"
        const val SERVICES: String = "services"
    }
}

class FirePaths {
    companion object {
        // FIRESTORE FILE NAMES
        const val PROFILE_FILE_NAME = "profile_image.jpg"
        const val ICON_FILE_NAME = "icon.jpg"
        // FIRESTORE ROUTES
        const val PROFILE = "profile"
        // Profile Images
        var PROFILE_IMAGE_PATH_BY_ID : (String, String) -> String = { FireType,id -> "$FireType/$id/$PROFILE/$PROFILE_FILE_NAME"}
        // Icon Images
        var ICON_IMAGE_PATH_BY_ID : (String, String) -> String = { FireType,id -> "$FireType/$id/$PROFILE/$ICON_FILE_NAME"}
    }
}

class FireLayouts {
    companion object {
        fun getLayout(type: String): Int {
            return when (type) {
                FireTypes.ORGANIZATIONS -> R.layout.card_organization
                FireTypes.SPORTS -> R.layout.card_sport
                FireTypes.REVIEWS -> R.layout.card_review
                FireTypes.USERS -> R.layout.card_sport
                FireTypes.COACHES -> R.layout.card_sport
                FireTypes.REVIEW_TEMPLATES -> R.layout.card_single_question
                else -> R.layout.card_sport
            }
        }
    }
}

/** UTILS **/

fun coreFirebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

fun coreFireLogoutAsync(context: Context): Task<Void> {
    return AuthUI.getInstance().signOut(context)
}

fun coreFireUpdateProfileImageUri(imgUri: Uri) {
    val userChangeRequest = UserProfileChangeRequest.Builder().apply {
        photoUri = imgUri
    }.build()
    coreFirebaseUser()?.updateProfile(userChangeRequest)?.addOnCompleteListener {
        if (it.isSuccessful) {
            log("Photo has been successfully updated in User Profile.")
        }
    }
}

fun coreFireSendUserVerificationEmail() {
    coreFirebaseUser()?.sendEmailVerification()?.addOnCompleteListener {
        if (it.isSuccessful) {
            log("Successfully sent email verification!")
        }
    }
}

fun coreFireChangeUserPassword(newPassword:String) {
    //todo
}

fun coreFireSendResetUserPasswordEmail(newPassword:String) {
    //todo
}

//fun coreAuthenticateUser(email:String, password:String) {
//    val credential = EmailAuthProvider.getCredential(email, "password1234")
//    GoogleAuthProvider.getCredential(email, password)
//}

fun Any?.fireGetNameOfRealmObject(): String? {
    this?.let {
        when (it) {
            is User -> return FireTypes.USERS
            is Sport -> return FireTypes.SPORTS
            is Organization -> return FireTypes.ORGANIZATIONS
            is Coach -> return FireTypes.COACHES
            is Service -> FireTypes.SERVICES
            is Review -> FireTypes.REVIEWS
            is ReviewTemplate -> FireTypes.REVIEW_TEMPLATES
            else -> return null
        }
    }
    return null
}

fun Any.fireForceGetNameOfRealmObject() : String {
    return when (this) {
        is User -> FireTypes.USERS
        is Sport -> FireTypes.SPORTS
        is Organization -> FireTypes.ORGANIZATIONS
        is Coach -> FireTypes.COACHES
        is Service -> FireTypes.SERVICES
        is Review -> FireTypes.REVIEWS
        is ReviewTemplate -> FireTypes.REVIEW_TEMPLATES
        else -> FireTypes.USERS
    }
}

inline fun <T> T?.fireWhenType(block: (T) -> Unit) {
    this?.let {
        when (it) {
            is User -> block(it)
            is Sport -> block(it)
            is Organization -> block(it)
            is Coach -> block(it)
        }
    }
}



inline fun <reified T> DataSnapshot.loadIntoSession() {
    for (ds in this.children) {
        val obj: T? = ds.getValue(T::class.java)
        obj?.let {
            obj.addToSession()
        }
    }
}








