package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import io.usys.report.model.Coach
import io.usys.report.model.User
import io.usys.report.model.getCoachByOwnerId
import io.usys.report.model.toCoachObject
import io.usys.report.utils.executeRealm
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

/**
 * USER
 */

// -> Save
fun fireSaveUserToFirebaseAsync(user:User?) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
    }
}

// -> Save
inline fun fireSaveUserToFirebaseAsync(user:User?, crossinline block: (Any?) -> Unit) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
            .fairAddOnCompleteListener { ds ->
                block(ds)
            }
    }
}

// -> Get
inline fun fireGetSyncUserProfile(userId:String, crossinline block: (User?) -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.USERS).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                val userObject = ds?.getValue(User::class.java)
                block(userObject)
            }
    }
}



/**
 * USER COACH
 */

// -> Save
fun fireSaveCoachToFirebaseAsync(coach: Coach?) {
    if (coach.isNullOrEmpty()) return
    if (coach?.ownerId.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(coach?.ownerId ?: "unknown").setValue(coach)
    }
}

// -> Get
fun fireGetCoachesByOrg(orgId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    fireGetOrderByEqualToCallback(FireTypes.COACHES,
        Coach.ORDER_BY_ORGANIZATION, orgId, callbackFunction)
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

fun fireGetCoachProfileForSession(userId:String) {
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                executeRealm {
                    val coachObject = ds?.toHashMapWithRealmLists().toCoachObject()
                    val coach = getCoachByOwnerId(userId)
                    coach?.let {
                        it.update(coachObject)
                        it.saveToRealm()
                    } ?: run {
                        coachObject.saveToRealm()
                    }
                    log("Coach Updated")
                }

            }
    }
}

