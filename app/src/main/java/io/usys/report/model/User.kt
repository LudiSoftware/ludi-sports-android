package io.usys.report.model

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.coreFirebaseUserUid
import io.usys.report.firebase.fireSaveUserToFirebaseAsync
import io.usys.report.firebase.fireUpdateSingleValueDBAsync
import io.usys.report.utils.*
import io.usys.report.utils.AuthTypes.Companion.BASIC_USER
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */



open class User : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = "" // SETUP VIA FIREBASE TO LINK TO AUTH SYSTEM
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var username: String = UNASSIGNED
    var name: String = UNASSIGNED //Name Given by Manager
    var auth: String = BASIC_USER // "basic"
    var type: String = UNASSIGNED
    var email: String = UNASSIGNED
    var phone: String = UNASSIGNED
    var organization: String = UNASSIGNED
    var organizationId: String = UNASSIGNED
    var visibility: String = "closed"
    var photoUrl: String = UNASSIGNED
    var emailVerified: Boolean = false
    var parent: Boolean = false
    var player: Boolean = false
    var coach: Boolean = false

    fun isParentUser() : Boolean {
        return this.parent
    }
    fun isPlayerUser() : Boolean {
        return this.player
    }
    fun isCoachUser() : Boolean {
        return this.coach
    }

    fun isIdentical(userTwo:User): Boolean {
        if (this == userTwo) return true
        return false
    }

    fun saveToFirebase(): User {
        fireSaveUserToFirebaseAsync(this)
        return this
    }

    fun saveToRealm(): User {
        executeRealm {
            it.insertOrUpdate(this)
        }
        return this
    }

    fun updateUserFields(updatedUser: User) {
        this.username = updatedUser.username
        this.name = updatedUser.name
        this.auth = updatedUser.auth
        this.type = updatedUser.type
        this.email = updatedUser.email
        this.phone = updatedUser.phone
        this.organization = updatedUser.organization
        this.organizationId = updatedUser.organizationId
        this.visibility = updatedUser.visibility
        this.photoUrl = updatedUser.photoUrl
        this.emailVerified = updatedUser.emailVerified
        this.parent = updatedUser.parent
        this.player = updatedUser.player
        this.coach = updatedUser.coach
        this.dateUpdated = getTimeStamp()
    }
}

fun FirebaseUser?.fromFirebaseToRealmUser() : User {
    if (this.isNullOrEmpty()) return User()
    val uid = this?.uid ?: "UNKNOWN"
    val email = this?.email ?: "UNKNOWN"
    val name = this?.displayName ?: "UNKNOWN"
    val photoUrl = this?.photoUrl ?: "UNKNOWN"
    val emailVerified = this?.isEmailVerified ?: false
    Session.createUserObject(uid)
    val user = User()
    user.apply {
        this.id = uid
        this.email = email
        this.name = name
        this.photoUrl = photoUrl.toString()
        this.emailVerified = emailVerified
    }.saveToRealm()
    return user
}

inline fun User.applyToRealm(crossinline block: (User) -> Unit) {
    executeInsertOrUpdateRealm {
        this.apply {
            block(this)
        }
    }
}

fun getMasterUser(): User? {
    val uid = coreFirebaseUserUid() ?: return null
    return Session.getCreateUserById(uid)
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
    val user = getMasterUser()
    user?.let {
        block(it)
    } ?: run {
        activity?.let {
            Session.logoutAndRestartApplication(it)
        }
    }
    //todo: get firebase user, if valid, set and continue
}


