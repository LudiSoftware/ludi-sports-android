package io.usys.report.firebase

import io.realm.Realm
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.User
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

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

/**
 * USER COACH
 */

// -> Save
fun fireSaveCoachToFirebaseAsync(coach: Coach?) {
    if (coach.isNullOrEmpty()) return
    if (coach?.id.isNullOrEmpty()) return
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(coach?.id ?: "unknown").setValue(coach)
    }
}


fun fireGetCoachProfileInBackground(userId:String) {
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toLudiObject<Coach>()
                log("Coach Updated")
            }
    }
}
fun Realm.fireGetCoachProfileInBackground(userId:String) {
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(userId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toLudiObject<Coach>(this)
                log("Coach Updated")
            }
    }
}

