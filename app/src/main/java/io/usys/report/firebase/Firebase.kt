package io.usys.report.firebase

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
                REVIEWS -> return R.layout.card_review
                USERS -> return R.layout.card_sport
                COACHES -> return R.layout.card_sport
                else -> return R.layout.card_sport
            }
        }
    }
}

/** UTILS **/

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

// Verified.
inline fun <reified T> getBaseObjects(dbName:String, crossinline block: RealmList<T>?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).fairAddParsedListenerForSingleValueEvent<T> { realmList ->
                block(realmList)
            }
    }
}

fun loadSportsIntoSessionAsync() {
    firebaseDatabase {
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
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .fairAddOnSuccessCallback(callbackFunction)
    }
}


// Verified
fun addUpdateDBAsync(collection: String, id: String, obj: Any): Boolean {
    var result = false
    firebaseDatabase { database ->
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

fun Review.addUpdateReviewDBAsync(block: ((Any) -> Unit)? = null): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(FireTypes.REVIEWS).child(this.recieverId!!).child(this.id)
            .setValue(this).fairAddOnCompleteListener { itReturn ->
                block?.let { block(itReturn) }
            }
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
        firebaseDatabase {
        it.child(FireTypes.USERS).child(id).fairAddListenerForSingleValueEvent {
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
fun getReviewsByReceiverIdToCallback(receiverId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(FireTypes.REVIEWS).child(receiverId)
            .fairAddListenerForSingleValueEvent(callbackFunction)
    }
}

// Verified
fun getOrderByEqualToCallback(dbName:String, orderBy: String, equalTo: String,
                              callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent(callbackFunction)
    }
}

// Verified
inline fun getOrderByEqualToAsync(dbName:String, orderBy: String, equalTo: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

inline fun saveProfileToFirebaseAsync(user:User?, crossinline block: (Any) -> Unit) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
            .fairAddOnCompleteListener { ds ->
                block(ds)
            }
    }
}




