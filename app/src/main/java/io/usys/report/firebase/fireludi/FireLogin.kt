package io.usys.report.firebase

import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm
import io.usys.report.realm.updateFieldsAndSave
import io.usys.report.utils.log
import io.usys.report.utils.tryCatch

/**
 * USER Login Handling
 *
 * 1. check if user exists in firebase.
 *      a. IF EXISTS: make user the realm user.
 *      b. IF DOES NOT EXIST: save new user.
 */

inline fun fireSyncUserWithDatabase(coreFireUser: User, crossinline block: (User?) -> Unit) {
    val tempRealm = realm()
    firebaseDatabase { itFB ->
        itFB.child(FireTypes.USERS).child(coreFireUser.id).singleValueEvent { itDb ->
            itDb?.toLudiObject<User>(tempRealm)
            tempRealm.safeUser {
                log("User Found: ${it.id}")
                tryCatch { it.updateFieldsAndSave(coreFireUser, tempRealm) }
                // Check if is Coach.
                if (it.coach) { tempRealm.fireGetCoachProfileCustom(it.id) }
                // todo: parent, player
                block(it)
                return@singleValueEvent
            }
            return@singleValueEvent
        }
    }
}