package io.usys.report.db

import android.content.Context
import android.net.Uri
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.FireHelper
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log
import io.usys.report.utils.toRealmList
import java.lang.Exception

/**
 * Created by ChazzCoin : December 2019.
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
        const val SPORTS: String = "sports"
        const val COACHES: String = "coaches"

        var USER_PROFILE_IMAGE_PATH_BY_ID : (String) -> String = {"$USERS/$it/profile/profile_image.jpg"}
        var ORG_PROFILE_IMAGE_PATH_BY_ID : (String) -> String = {"$ORGANIZATIONS/$it/profile/profile_image.jpg"}
//        var ADMIN_IMAGE_PATH_BY_ID : (String) -> String = {"$ADMIN/$it/profile/profile_image.jpg"}

        fun getLayout(type: String): Int {
            when (type) {
                ORGANIZATIONS -> return R.layout.card_organization
                SPORTS -> return R.layout.card_sport
                REVIEWS -> return R.layout.card_sport
                USERS -> return R.layout.card_sport
                COACHES -> return R.layout.card_sport
                else -> return R.layout.card_sport
            }
        }
    }
}

/** UTILS **/

fun getStorageReference(): StorageReference {
    return Firebase.storage.reference
}


fun getStorageRefByPath(path:String): StorageReference {
    return Firebase.storage.reference.child(path)
}

inline fun StorageReference.getDownloadUrlAsync(crossinline block:(Uri) -> Unit) {
    this.downloadUrl.addOnCompleteListener { itUri ->
        block(itUri.result)
    }
}

// Verified
inline fun firebase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}

fun getFirebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

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

fun masterFirebaseLogoutAsync(context: Context): Task<Void> {
    return AuthUI.getInstance().signOut(context)
}


// Verified.
inline fun <reified T> getBaseObjects(dbName:String, crossinline block: RealmList<T>?.() -> Unit) {
    firebase {
        it.child(dbName).addYsrParsedListenerForSingleValueEvent<T> { realmList ->
                block(realmList)
            }
    }
}

fun loadSportsIntoSessionAsync() {
    firebase {
        it.child(FireDB.SPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.loadIntoSession<Sport>()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
}

/** ADD/UPDATE **/
// unverified
fun <T> T.addUpdateInFirebase(id: String, callbackFunction: ((Boolean, String) -> Unit)?) {
    val collection = this.getNameOfRealmObject() ?: return
    firebase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .addYsrOnSuccessCallback(callbackFunction)
    }
}

private fun <TResult> Task<TResult>.addYsrOnSuccessCallback(callbackFunction: ((Boolean, String) -> Unit)?) {
    this.addOnSuccessListener {
        callbackFunction?.invoke(true, "success")
    }.addOnCompleteListener {
        callbackFunction?.invoke(true, "complete")
    }.addOnFailureListener {
        callbackFunction?.invoke(false, "failure")
    }
}

// Verified
fun addUpdateDBAsync(collection: String, id: String, obj: Any): Boolean {
    var result = false
    firebase { database ->
        database.child(collection).child(id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}

/** GET **/

inline fun getUserUpdatesFromFirebaseAsync(id: String, crossinline block: (User?) -> Unit): User? {
    var userUpdates: User? = null
        firebase {
        it.child(FireHelper.USERS).child(id).addYsrListenerForSingleValueEvent {
            userUpdates = it?.getValue(User::class.java)
            userUpdates?.let { itUser ->
                if (itUser.id == id){
                    AuthControllerActivity.USER_AUTH = itUser.auth
                    AuthControllerActivity.USER_ID = itUser.id.toString()
                    Session.updateUser(itUser)
                }
            }
            block(userUpdates)
        }
    }
    return userUpdates
}

fun getCoachesByOrg(orgId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    getOrderByEqualToCallback(FireDB.COACHES, ORDER_BY_ORGANIZATION, orgId, callbackFunction)
}

// Verified
fun getOrderByEqualToCallback(dbName:String, orderBy: String, equalTo: String,
                              callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .addYsrListenerForSingleValueEvent(callbackFunction)
    }
}

// Verified
inline fun getOrderByEqualToAsync(dbName:String, orderBy: String, equalTo: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .addYsrListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

inline fun saveProfileToFirebaseAsync(user:User?, crossinline block: (Any) -> Unit) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebase {
        it.child(FireHelper.USERS).child(user?.id ?: "unknown").setValue(user)
            .addYsrCompleteListener { ds ->
                block(ds)
            }
    }
}

fun <TResult> Task<TResult>.addYsrCompleteListener(block: (TResult) -> Unit): Task<TResult> {
    this.addOnCompleteListener {
        block(it.result)
    }
    return this
}

fun <TResult> Task<TResult>.addYsrFailureListener(block: (Exception) -> Unit): Task<TResult> {
    this.addOnFailureListener {
        block(it)
    }
    return this
}

// Verified
fun Query.addYsrListenerForSingleValueEvent(callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            callbackFunction?.invoke(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            callbackFunction?.invoke(null)
        }
    })
}

@JvmName("addYsrListenerForSingleValueEvent1")
fun Query.addYsrListenerForSingleValueEvent(block: (DataSnapshot?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}

inline fun <reified T> Query.addYsrParsedListenerForSingleValueEvent(crossinline block: (RealmList<T>?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot.toRealmList())
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}
