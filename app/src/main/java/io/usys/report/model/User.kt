package io.usys.report.model

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.usys.report.ui.AuthControllerActivity
import io.realm.Realm
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

fun parseFromFirebaseUser(fireUser: FirebaseUser?) : User {
    if (fireUser.isNullOrEmpty()) return User()
    val uid = fireUser?.uid
    val email = fireUser?.email
    val name = fireUser?.displayName
    val photoUrl = fireUser?.photoUrl
    val emailVerified = fireUser?.isEmailVerified
    val user = User().apply {
        this.id = uid ?: "unknown"
        this.email = email
        this.name = name
        this.photoUrl = photoUrl.toString()
        this.emailVerified = emailVerified
    }
    return user
}

fun updateUser(newUser: User) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { r : Realm ->
        // Get a turtle to update.
        val user = r.where(User::class.java).findFirst()
        // Update properties on the instance.
        // This change is saved to the realm.
        user.apply {
            this?.auth = newUser.auth
            this?.name = newUser.name
            this?.email = newUser.email
            this?.phone = newUser.phone
        }
    }
}

//GET CURRENT CART
fun getUser(): User? {
    val realm = Realm.getDefaultInstance()
    var user : User? = User()
    realm?.let {
        user = it.where(User::class.java).findFirst()
    }
    return user
}

//fun getProfileUpdatesFirebase(mContext: Context, uid:String) {
//    val database = FirebaseDatabase.getInstance().reference
//    database.child(FireHelper.PROFILES).child(FireHelper.USERS).child(uid)
//        .addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val temp: User? = dataSnapshot.getValue(User::class.java)
//                temp?.let {
//                    if (it.id == uid){
//                        AuthControllerActivity.USER_AUTH = it.auth.toString()
//                        AuthControllerActivity.USER_ID = it.id!!
////                        it.getFoodtrucksFromFirebase(mContext)
//                        Session.updateUser(it)
//                    }
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                showFailedToast(mContext)
//            }
//        })
//}

fun User.addUpdateToFirebase(mContext: Context) {
    val database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES)
        .child(FireHelper.USERS)
        .child(AuthControllerActivity.USER_ID)
        .setValue(this)
        .addOnSuccessListener {
            //success
            Session.updateUser(this)
            showSuccess(mContext, "Successfully updated User Info.")
        }.addOnFailureListener {
            //failure
            showFailedToast(mContext, "Failed to update User Info.")
        }
}

