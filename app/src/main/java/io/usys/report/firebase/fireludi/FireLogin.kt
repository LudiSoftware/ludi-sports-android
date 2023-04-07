package io.usys.report.firebase

import io.usys.report.realm.findByField
import io.usys.report.realm.model.users.User
import io.usys.report.realm.realm
import io.usys.report.realm.updateFieldsAndSave


/**
 * USER Login Handling
 *
 * 1. check if user exists in firebase.
 *      a. IF EXISTS: make user the realm user.
 *      b. IF DOES NOT EXIST: save new user.
 *
 *
 */

inline fun fireSyncUserWithDatabase(coreFireUser: User, crossinline block: (User?) -> Unit) {
    val tempRealm = realm()
    firebaseDatabase { itFB ->
        itFB.child(FireTypes.USERS).child(coreFireUser.id).fairAddListenerForSingleValueEvent { itDb ->
            var userProfile = itDb?.toObject<User>()
            userProfile?.let { itUpdatedUser ->
                // DOES EXIST: Firebase User Was Found
                coreFireUser.updateFieldsAndSave(itUpdatedUser, tempRealm)
                // Check if is Coach.
                if (coreFireUser.coach && coreFireUser.coachUser == null) {
                    tempRealm.fireGetCoachProfileCustom(coreFireUser.id)
                }
                block(coreFireUser)
                return@fairAddListenerForSingleValueEvent
            }
            // DOES NOT EXIST: Firebase User Was Not Found
            userProfile = coreFireUser
            userProfile.saveToFirebase()
            block(userProfile)
            return@fairAddListenerForSingleValueEvent
        }
    }
}