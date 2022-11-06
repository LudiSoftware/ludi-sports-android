package io.usys.report.firebase

import com.google.firebase.database.*
import io.realm.RealmList
import io.usys.report.model.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

/** Get List of ALL Base Objects */
inline fun <reified T> getBaseObjects(dbName:String, crossinline block: RealmList<T>?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).fairAddParsedListenerForSingleValueEvent<T> { realmList ->
            block(realmList)
        }
    }
}

/** Get One Single Value by Single Attribute */
inline fun getSingleValueAsync(collection:String, objId: String, singleAttribute: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(collection).child(objId).child(singleAttribute)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/** Get List by Single Attribute AsyncBlock */
inline fun getOrderByEqualToAsync(dbName:String, orderBy: String, equalTo: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/** Get List by Single Attribute with Callback */
fun getOrderByEqualToCallback(dbName:String, orderBy: String, equalTo: String,
                              callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent(callbackFunction)
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

// Verified
fun updateSingleValueDBAsync(collection: String, id: String, single_attribute: String, single_value: String): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection)
            .child(id)
            .child(single_attribute)
            .setValue(single_value)
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
        database.child(FireTypes.REVIEWS).child(this.receiverId!!).child(this.id)
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
                    AuthControllerActivity.USER_ID = itUser.id
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




