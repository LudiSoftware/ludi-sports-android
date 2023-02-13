package io.usys.report.model

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.coreFirebaseUser
import io.usys.report.firebase.fireUpdateSingleValueDBAsync
import io.usys.report.utils.*
import io.usys.report.utils.AuthTypes.Companion.BASIC_USER
import io.usys.report.utils.AuthTypes.Companion.COACH_USER
import io.usys.report.utils.AuthTypes.Companion.PARENT_USER
import io.usys.report.utils.AuthTypes.Companion.PLAYER_USER
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */



open class User : RealmObject(), Serializable {

    var id: String = "" // SETUP VIA FIREBASE TO LINK TO AUTH SYSTEM
    var dateCreated: String = getTimeStamp()
    //todo: create function for updating this.
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

fun createUser() {
    val realm = Realm.getDefaultInstance()
    if (realm.where(User::class.java) == null){
        realm.executeTransaction { itRealm ->
            itRealm.createObject(User::class.java, newUUID())
        }
    }
}

fun updateUser(newNser: User){
    executeRealm { itRealm ->
        Session.user = newNser
        itRealm.insertOrUpdate(newNser)
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