package io.usys.report.providers

import com.google.firebase.database.DatabaseReference
import io.usys.report.firebase.*
import io.usys.report.firebase.fireludi.CoachFireListener
import io.usys.report.realm.*
import io.usys.report.realm.model.users.Coach
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.model.users.safeUserId

class UserProvider() {

    val realmInstance = realm()
    var userId: String? = null
    var userIsComplete: Boolean = false

    var isCoach: Boolean = false
    var coachIsComplete: Boolean = false

    var userReference: DatabaseReference? = null
    var coachReference: DatabaseReference? = null
    private var coachFireListener: CoachFireListener

    init {
        realmInstance.safeUserId {
            this.userId = it
            verifyOrLoadUser()
        }
        realmInstance.safeUser {
            this.isCoach = it.coach
            if (it.coach) verifyOrLoadCoach()
        }
        if (this.userId == null) this.userId = coreFirebaseUserUid()
        firebaseDatabase {
            userReference = it.child(DatabasePaths.USERS.path)
            coachReference = it.child(DatabasePaths.COACHES.path)
        }
        coachFireListener = CoachFireListener(realmInstance)
    }

    /** FireUsers
     * 1. User (ALL)
     * 2. Coach
     * 3. Parent
     * 4. Player
     * */

    private fun verifyOrLoadUser() {
        realmInstance.ifObjectDoesNotExist<User>(this.userId ?: coreFirebaseUserUid()) { userId ->
            userReference?.child(userId)?.singleValueEvent { ds ->
                val user = ds?.toLudiObject<User>(realmInstance)
                user?.let {
                    if (it.coach) {
                        this.isCoach = true
                        verifyOrLoadCoach()
                    }
                }
                userIsComplete = true
            }
        }
    }

    private fun verifyOrLoadCoach() : Boolean {
        realmInstance.ifObjectDoesNotExist<Coach>(this.userId) { userId ->
            coachReference?.child(userId)?.singleValueEvent { ds ->
                ds?.toLudiObject<Coach>(realmInstance)
                coachIsComplete = true
            }
        }
        return coachIsComplete
    }

    /** Is Completion Checkers **/
    private fun userIsComplete() : Boolean {
        realmInstance.ifObjectExists<User>(this.userId) {
            this.userIsComplete = true
        }
        return userIsComplete
    }

    private fun coachIsComplete() : Boolean {
        realmInstance.ifObjectExists<Coach>(this.userId) {
            this.coachIsComplete = true
        }
        return coachIsComplete
    }

}

