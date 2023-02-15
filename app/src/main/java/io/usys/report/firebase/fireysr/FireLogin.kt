package io.usys.report.firebase

import io.usys.report.realm.model.User
import io.usys.report.realm.model.getCoachByOwnerId

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
    firebaseDatabase { itFB ->
        itFB.child(FireTypes.USERS).child(coreFireUser.id).fairAddListenerForSingleValueEvent { itDb ->
            var userProfile = itDb?.toObject<User>()
            userProfile?.let { itUpdatedUser ->
                // DOES EXIST: Firebase User Was Found
                coreFireUser.updateAndSave(itUpdatedUser)
                // Check if is Coach.
                if (coreFireUser.coach && coreFireUser.coachUser == null) {
                    fireGetCoachProfileForSession(coreFireUser.id)
                }
                block(coreFireUser)
                return@fairAddListenerForSingleValueEvent
            }
            // DOES NOT EXIST: Firebase User Was Not Found
            userProfile = coreFireUser
            userProfile.saveToRealm()
            userProfile.saveToFirebase()
            block(userProfile)
            return@fairAddListenerForSingleValueEvent
        }
    }
}