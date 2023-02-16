package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import io.usys.report.realm.executeRealm
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.safeUser
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

// -> Save
fun fireSaveProfileToFirebaseAsync(id:String, realmObject: Any?, type:String) {
    if (realmObject.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(type).child(id).setValue(realmObject)
    }
}

/**
 * USER
 */

// -> Save
fun fireSaveUserToFirebaseAsync(user: User?) {
    if (user.isNullOrEmpty()) return
    if (user?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.USERS).child(user?.id ?: "unknown").setValue(user)
    }
}

// -> Save
inline fun fireSaveUserToFirebaseAsync(user: User?, crossinline block: (Any?) -> Unit) {
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
    if (coach?.coachId.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(coach?.coachId ?: "unknown").setValue(coach)
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
                    val coachObject = ds?.toObject<Coach>()
                    val coach = getCoachByOwnerId(userId)
                    safeUser { itUser ->
                        coach?.let { itCoach ->
                            itUser.makeCoachAndSave(itCoach)
                        } ?: run {
                            itUser.makeCoachAndSave(coachObject)
                        }
                    }
                }
                log("Coach Updated")
            }
    }
}


