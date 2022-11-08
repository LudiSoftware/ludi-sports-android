package io.usys.report.firebase

import android.net.Uri
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

class FireDB {

    companion object {
        const val SPOT_MONTH_DB = "MMMyyyy"
        const val SPOT_DATE_FORMAT = "yyyy-MM-d"
        const val DATE_MONTH = "MMMM"
        const val FIRE_DATE_FORMAT = "EEE, MMM d yyyy, hh:mm:ss a"
        //ADMIN
        const val ADMIN: String = "admin"
        const val SYSTEM: String = "system"
        // -> Main Database Structure
        const val USERS: String = "users"
        const val SPORTS: String = "sports"
        const val ORGANIZATIONS: String = "organizations"
        const val COACHES: String = "coaches"
        const val REVIEWS: String = "reviews"
        const val REVIEW_TEMPLATES: String = "review_templates"
        const val SERVICES: String = "services"


        /**
         * organizations - organization(id)
         * users - user(id)
         * reviews - review(id)
         *
         */
    }
}

class FireTypes {
    companion object {
        const val ADMIN: String = "admin"
        const val USERS: String = "users"
        const val ORGANIZATIONS: String = "organizations"
        const val REVIEWS: String = "reviews"
        const val REVIEW_TEMPLATES: String = "review_templates"
        const val SPORTS: String = "sports"
        const val COACHES: String = "coaches"

        var USER_PROFILE_IMAGE_PATH_BY_ID : (String) -> String = {"$USERS/$it/profile/profile_image.jpg"}
        var ORG_PROFILE_IMAGE_PATH_BY_ID : (String) -> String = {"$ORGANIZATIONS/$it/profile/profile_image.jpg"}
//        var ADMIN_IMAGE_PATH_BY_ID : (String) -> String = {"$ADMIN/$it/profile/profile_image.jpg"}

        fun getLayout(type: String): Int {
            when (type) {
                ORGANIZATIONS -> return R.layout.card_organization
                SPORTS -> return R.layout.card_sport
                REVIEWS -> return R.layout.card_review
                USERS -> return R.layout.card_sport
                COACHES -> return R.layout.card_sport
                REVIEW_TEMPLATES -> return R.layout.card_single_question
                else -> return R.layout.card_sport
            }
        }
    }
}

/** UTILS **/

fun coreFirebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

fun coreUpdateProfileImageUri(imgUri: Uri) {
    val userChangeRequest = UserProfileChangeRequest.Builder().apply {
        photoUri = imgUri
    }.build()
    coreFirebaseUser()?.updateProfile(userChangeRequest)?.addOnCompleteListener {
        if (it.isSuccessful) {
            log("Photo has been successfully updated in User Profile.")
        }
    }
}

fun coreSendUserVerificationEmail() {
    coreFirebaseUser()?.sendEmailVerification()?.addOnCompleteListener {
        if (it.isSuccessful) {
            log("Successfully sent email verification!")
        }
    }
}

fun coreChangeUserPassword(newPassword:String) {

}

fun coreSendResetUserPasswordEmail(newPassword:String) {

}

//fun coreAuthenticateUser(email:String, password:String) {
//    val credential = EmailAuthProvider.getCredential(email, "password1234")
//    GoogleAuthProvider.getCredential(email, password)
//}

fun Any.forceGetNameOfRealmObject() : String {
    when (this) {
        is User -> {
            return FireTypes.USERS
        }
        is Sport -> {
            return FireTypes.SPORTS
        }
        is Organization -> {
            return FireTypes.ORGANIZATIONS
        }
        is Coach -> {
            return FireTypes.COACHES
        }
        else -> {
            // Create Base Generic Object
            return FireTypes.USERS
        }
    }
}

inline fun <T> T?.whenType(block: (T) -> Unit) {
    this?.let {
        when (it) {
            is User -> {
                block(it)
            }
            is Sport -> {
                block(it)
            }
            is Organization -> {
                block(it)
            }
            is Coach -> {
                block(it)
            }
        }
    }
}

fun Any?.getNameOfRealmObject(): String? {
    this?.let {
        when (it) {
            is User -> {
                return FireTypes.USERS
            }
            is Sport -> {
                return FireTypes.SPORTS
            }
            is Organization -> {
                return FireTypes.ORGANIZATIONS
            }
            is Coach -> {
                return FireTypes.COACHES
            }
            else -> {
                return null
            }
        }
    }
    return null
}

inline fun <reified T> DataSnapshot.loadIntoSession() {
    for (ds in this.children) {
        val obj: T? = ds.getValue(T::class.java)
        obj?.let {
            obj.addToSession()
        }
    }
}








