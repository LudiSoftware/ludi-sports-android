package io.usys.report.model

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.RealmObject
import io.usys.report.model.AuthTypes.Companion.BASIC_USER
import io.usys.report.model.AuthTypes.Companion.COACH_USER
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : December 2019.
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
    }
}

open class User : RealmObject() {

    var id: String = "" // SETUP VIA FIREBASE TO LINK TO AUTH SYSTEM
    var dateCreated: String? = null
    var name: String? = "" //Name Given by Manager
    var auth: String = AuthTypes.BASIC_USER // "basic"
    var type: String? = ""
    var email: String? = ""
    var phone: String? = ""
    var organization: String? = ""
    var visibility: String = "closed"
    var photoUrl: String? = null
    var emailVerified: Boolean? = false

    init {
        if (dateCreated.isNullOrBlank()) {
            dateCreated = DateUtils().getCurrentDayTime()
        }
    }

    fun isCoachUser() : Boolean {
        if (this.auth == COACH_USER) return true
        return false
    }
    fun isBasicUser() : Boolean {
        if (this.auth == BASIC_USER) return true
        return false
    }

}

fun FirebaseUser?.toYsrUser() : User {
    if (this.isNullOrEmpty()) return User()
    val uid = this?.uid
    val email = this?.email
    val name = this?.displayName
    val photoUrl = this?.photoUrl
    val emailVerified = this?.isEmailVerified
    val user = User().apply {
        this.id = uid ?: "unknown"
        this.email = email
        this.name = name
        this.photoUrl = photoUrl.toString()
        this.emailVerified = emailVerified
    }
    return user
}

fun getMasterUser(): User? {
    val realmUser = Session.getCurrentUser()
    val realmUserId = realmUser?.id
    if (!realmUserId.isNullOrEmpty()) return realmUser
    val fireUser = FirebaseAuth.getInstance().currentUser
    return fireUser.toYsrUser()
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

fun userOrLogout(activity: Activity? = null) {
    if (Session.user.isNullOrEmpty()) {
        activity?.let { Session.logoutAndRestartApplication(it) }
    }
    //todo: get firebase user, if valid, set and continue
}
