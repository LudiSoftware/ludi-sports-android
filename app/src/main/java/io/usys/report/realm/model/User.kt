package io.usys.report.realm.model

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.coreFirebaseUserUid
import io.usys.report.firebase.fireSaveUserToFirebaseAsync
import io.usys.report.realm.executeRealm
import io.usys.report.realm.realm
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
    var username: String = UNASSIGNED
    var auth: String = BASIC_USER // "basic"
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
    var coachUser: Coach? = null
    var playerUser: Player? = null
    // Base -> YsrRealmObject
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var name: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean = false
    var status: String? = null
    var mode: String? = null
    var imgUrl: String? = null
    var sport: String? = null

    fun isParentUser() : Boolean {
        return this.parent
    }
    fun isPlayerUser() : Boolean {
        return this.player
    }
    fun isCoachUser() : Boolean {
        return this.coach
    }

    fun makeCoachAndSave(coach: Coach?) {
        executeRealm {
            this.apply {
                this.coachUser = coach
            }
        }
        this.saveToRealm()
    }

    fun isIdentical(userTwo: User): Boolean {
        if (this == userTwo) return true
        return false
    }

    fun saveToFirebase(): User {
        fireSaveUserToFirebaseAsync(this)
        return this
    }

    fun saveToRealm(): User {
        executeRealm {
            this.saveToRealm()
        }
        return this
    }

}

fun FirebaseUser?.fromFirebaseToRealmUser() : User {
    if (this.isNullOrEmpty()) return User()
    val uid = this?.uid ?: "UNKNOWN"
    val email = this?.email ?: "UNKNOWN"
    val name = this?.displayName ?: "UNKNOWN"
    val photoUrl = this?.photoUrl ?: "UNKNOWN"
    val emailVerified = this?.isEmailVerified ?: false
    createUserObject(uid)
    val user = User()
    user.apply {
        this.id = uid
        this.email = email
        this.name = name
        this.photoUrl = photoUrl.toString()
        this.emailVerified = emailVerified
    }
    return user
}

inline fun safeUser(block: (User) -> Unit) {
    val user = realmUser()
    user?.let {
        if (it.id.isNullOrEmpty()) return@let
        block(it)
    }
}
fun realmUser(): User? {
    val uid = coreFirebaseUserUid() ?: return null
    return getRealmUserById(uid)
}
fun getRealmUserById(id:String) : User? {
    var user: User? = null
    try {
        executeRealm { user = queryForUser(id) }
        return user
    } catch (e: Exception) { e.printStackTrace() }
    return user
}

fun queryForUser(userId:String): User? {
    return realm().where(User::class.java).equalTo("id", userId).findFirst()
}
fun createUserObject(userId:String) {
    executeRealm { itRealm ->
        itRealm.createObject(User::class.java, userId)
    }
}

inline fun safeUserId(crossinline block: (String) -> Unit) {
    realmUser()?.id?.let { itId ->
        block(itId)
    }
}

fun getUserId(): String? {
    return realmUser()?.id
}

inline fun userOrLogout(activity: Activity? = null, block: (User) -> Unit) {
    val user = realmUser()
    user?.let {
        block(it)
    } ?: run {
        activity?.let {
            Session.logoutAndRestartApplication(it)
        }
    }
    //todo: get firebase user, if valid, set and continue
}


