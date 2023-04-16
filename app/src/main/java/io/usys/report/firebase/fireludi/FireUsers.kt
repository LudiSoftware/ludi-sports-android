package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.usys.report.firebase.fireludi.fireGetTeamProfileInBackground
import io.usys.report.firebase.models.toRealmCoach
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

fun Realm.fireGetCoachProfileCustom(userId:String) {
    firebaseDatabase {
        it.child(FireTypes.COACHES).child(userId)
            .singleValueEvent { ds ->
                val coach = ds?.toRealmCoach(this)
                if (coach != null) {
                    coach.teams?.let { teams ->
                        for (teamId in teams) {
                            this.fireGetTeamProfileInBackground(teamId)
                        }
                    }
                }
                log("Coach Updated")
            }
    }
}

class CoachFireListener(val realm: Realm): ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot.toLudiObject<Coach>(realm)
        log("Roster Updated")
    }

    override fun onCancelled(databaseError: DatabaseError) {
        log("Error: ${databaseError.message}")
    }
}
