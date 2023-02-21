package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.executeRealm
import io.usys.report.realm.realm
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class TeamRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = null
    var sport: String? = null
    var status: String? = null
}

open class Team : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var coachRefs: RealmList<CoachRef>? = RealmList()
    var managerRefs: RealmList<Parent>? = RealmList()
    var organizationRefs: RealmList<Organization>? = RealmList()
    var roster: RealmList<PlayerRef>? = RealmList()
    var season: String? = "null"
    var schedule: Schedule? = Schedule()
    var year: String? = "null"
    var ageGroup: String? = "null"
    var isActive: Boolean = false
    var gender: String? = "null"
    var hasReview: Boolean = false
    var reviews: ReviewBundle? = ReviewBundle()
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = "null"
    var type: String? = "null"
    var subType: String? = "null"
    var details: String? = "null"
    var isFree: Boolean = false
    var status: String? = "null"
    var mode: String? = "null"
    var imgUrl: String? = "null"
    var sport: String? = "null"
    var chatEnabled: Boolean = false

    fun saveToRealm() {
        executeRealm { realm ->
            realm.insertOrUpdate(this)
        }
    }

    fun updateAndSave(newTeam: Team?) {
        if (newTeam.isNullOrEmpty()) return
//        if (this.isIdenticalCoach(newUser!!)) return
        executeRealm {
            this.apply {
                this.id = newTeam?.id ?: newUUID()
                this.name = newTeam?.name
                this.organizationRefs = newTeam?.organizationRefs
                this.roster = newTeam?.roster
                this.coachRefs = newTeam?.coachRefs
            }
        }
        this.saveToRealm()
    }

}

fun executeGetTeamById(teamId:String) : Team? {
    var team: Team? = null
    try {
        executeRealm {
            team = realm().where(Team::class.java).equalTo("id", teamId).findFirst()
        }
        return team
    } catch (e: Exception) { e.printStackTrace() }
    return team
}

fun getTeamById(teamId:String) : Team? {
    var team: Team? = null
    try {
        team = realm().where(Team::class.java).equalTo("id", teamId).findFirst()
        return team
    } catch (e: Exception) { e.printStackTrace() }
    return team
}