package io.usys.report.firebase

import com.google.firebase.database.*
import io.realm.RealmList
import io.usys.report.model.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.model.Session.Companion.createSession
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

/** Get List of ALL Base Objects */
inline fun <reified T> fireGetBaseYsrObjects(dbName:String, crossinline block: RealmList<T>?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).fairAddParsedListenerForSingleValueEvent<T> { realmList ->
            block(realmList)
        }
    }
}

/** Get One Single Value by Single Attribute */
inline fun fireGetSingleValueAsync(collection:String, objId: String, singleAttribute: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(collection).child(objId).child(singleAttribute)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}
inline fun fireGetSyncUserProfile(userId:String, crossinline block: (User?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.USERS).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                val userObject = ds?.getValue(User::class.java)
                block(userObject)
            }
    }
}

inline fun fireGetCoachProfile(userId:String, crossinline block: (Coach?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                val userObject = ds?.getValue(Coach::class.java)
                block(userObject)
            }
    }
}

/** Get List by Single Attribute AsyncBlock */
inline fun fireGetOrderByEqualToAsync(dbName:String, orderBy: String, equalTo: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/** Get List by Single Attribute with Callback */
fun fireGetOrderByEqualToCallback(dbName:String, orderBy: String, equalTo: String,
                                  callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .fairAddListenerForSingleValueEvent(callbackFunction)
    }
}


/** Review Template */

inline fun fireGetReviewTemplateAsync(templateType:String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.REVIEW_TEMPLATES).child(templateType)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

inline fun fireGetReviewTemplateQuestionsAsync(templateType:String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.REVIEW_TEMPLATES).child(templateType).child("master").child("questions")
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/**
 * SPORTS
 */
fun fireGetAndLoadSportsIntoSessionAsync() {
    firebaseDatabase {
        it.child(FireTypes.SPORTS)
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

/**
 * USER
 */

inline fun fireGetUserUpdatesFromFirebaseAsync(id: String, crossinline block: (User?) -> Unit): User? {
    var userUpdates: User? = null
        firebaseDatabase {
        it.child(FireTypes.USERS).child(id).fairAddListenerForSingleValueEvent {
            userUpdates = it?.getValue(User::class.java)
            ysrUpdateRealmUser()
            userUpdates?.let { itUser ->
                if (itUser.id == id){
                    AuthControllerActivity.USER_AUTH = itUser.auth
                    AuthControllerActivity.USER_ID = itUser.id
                    createSession()
                    Session.updateUser(itUser)
                }
            }
            block(userUpdates)
        }
    }
    return userUpdates
}

fun fireGetCoachesByOrg(orgId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    fireGetOrderByEqualToCallback(FireTypes.COACHES, ORDER_BY_ORGANIZATION, orgId, callbackFunction)
}

/**
 * SERVICES
 */
inline fun fireGetAllServicesAsync(crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.SERVICES)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}

/**
 * REVIEWS
 */

// Verified
fun fireGetReviewsByReceiverIdToCallback(receiverId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(FireTypes.REVIEWS).child(receiverId)
            .fairAddListenerForSingleValueEvent(callbackFunction)
    }
}






