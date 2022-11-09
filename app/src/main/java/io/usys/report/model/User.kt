package io.usys.report.model

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.RealmObject
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.coreFirebaseUser
import io.usys.report.firebase.fireUpdateSingleValueDBAsync
import io.usys.report.model.AuthTypes.Companion.BASIC_USER
import io.usys.report.model.AuthTypes.Companion.COACH_USER
import io.usys.report.model.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : October 2022.
 */

open class AuthTypes {
    companion object {
        // Auth Types
        var MASTER = "master"
        var ADMIN = "admin"
        var ORG_ADMIN_USER = "org_admin"
        var COACH_USER = "coach"
        var PLAYER_USER = "player"
        var PARENT_USER = "parent"
        var BASIC_USER = "basic" // Default
        var WAITING = "waiting"

        var UNASSIGNED = "unassigned"
    }
}

open class User : RealmObject() {

    var id: String = "" // SETUP VIA FIREBASE TO LINK TO AUTH SYSTEM
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var username: String = UNASSIGNED
    var name: String = "" //Name Given by Manager
    var auth: String = BASIC_USER // "basic"
    var type: String = ""
    var email: String = ""
    var phone: String = ""
    var organization: String = ""
    var visibility: String = "closed"
    var photoUrl: String = ""
    var emailVerified: Boolean = false

    fun isCoachUser() : Boolean {
        if (this.auth == COACH_USER) return true
        return false
    }
    fun isBasicUser() : Boolean {
        if (this.auth == BASIC_USER) return true
        return false
    }

}

fun FirebaseUser?.toYsrRealmUser() : User {
    if (this.isNullOrEmpty()) return User()
    val uid = this?.uid
    val email = this?.email
    val name = this?.displayName
    val photoUrl = this?.photoUrl
    val emailVerified = this?.isEmailVerified
    val user = User().apply {
        this.id = uid ?: "unknown"
        this.email = email ?: UNASSIGNED
        this.name = name ?: UNASSIGNED
        this.photoUrl = photoUrl.toString()
        this.emailVerified = emailVerified ?: false
    }
    return user
}

fun ysrUpdateRealmUser() {
    val fireUser = coreFirebaseUser()
    if (fireUser.isNullOrEmpty()) return
    val uid = fireUser?.uid
    val email = fireUser?.email
    val name = fireUser?.displayName
    val photoUrl = fireUser?.photoUrl
    val emailVerified = fireUser?.isEmailVerified
    Session.getCurrentUser()?.let { itUser ->
        executeRealm {
            itUser.id = uid ?: "unknown"
            itUser.email = email ?: UNASSIGNED
            itUser.name = name ?: UNASSIGNED
            itUser.photoUrl = photoUrl.toString()
            itUser.emailVerified = emailVerified ?: false
            it.insertOrUpdate(itUser)
        }
    }

}

fun getMasterUser(): User? {
    val realmUser = Session.getCurrentUser()
    val realmUserId = realmUser?.id
    if (!realmUserId.isNullOrEmpty()) return realmUser
    val fireUser = FirebaseAuth.getInstance().currentUser
    return fireUser.toYsrRealmUser()
}

inline fun safeUser(block: (User) -> Unit) {
    val user = getMasterUser()
    user?.let {
        if (it.id.isNullOrEmpty()) return@let
        block(it)
    }
}

inline fun safeUserId(crossinline block: (String) -> Unit) {
    getMasterUser()?.id?.let { itId ->
        block(itId)
    }
}

fun getUserId(): String? {
    return getMasterUser()?.id
}

inline fun userOrLogout(activity: Activity? = null, block: (User) -> Unit) {
    val user = Session.getCurrentUser()
    user?.let {
        block(it)
    } ?: run {
        activity?.let {
            Session.logoutAndRestartApplication(it)
        }
    }
    //todo: get firebase user, if valid, set and continue
}

fun User.fireUpdateUserProfileSingleValue(singleAttribute:String, singleValue:String) {
    fireUpdateSingleValueDBAsync(FireTypes.USERS, this.id, singleAttribute, singleValue)
}

fun fireUpdateUserProfileSingleValue(userId:String, singleAttribute:String, singleValue:String) {
    fireUpdateSingleValueDBAsync(FireTypes.USERS, userId, singleAttribute, singleValue)
}