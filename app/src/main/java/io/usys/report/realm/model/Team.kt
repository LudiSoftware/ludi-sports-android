package io.usys.report.realm.model

import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.addChildStringList
import io.usys.report.firebase.getBoolean
import io.usys.report.firebase.getString
import io.usys.report.providers.TeamMode
import io.usys.report.providers.TeamStatus
import io.usys.report.realm.safeWrite
import io.usys.report.utils.androidx.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class Team : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var coaches: RealmList<String>? = RealmList()
    var managers: RealmList<String>? = RealmList()
    var organizations: RealmList<String>? = RealmList()
    var rosterId: String? = "null"
    var tryoutId: String? = "null"
    var season: String? = "null"
    var locationIds: RealmList<String>? = null
    var schedule: String? = null
    var year: String? = "null"
    var ageGroup: String? = "null"
    var isActive: Boolean = false
    var gender: String? = "null"
    var hasReview: Boolean = false
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = "null"
    var type: String? = "null"
    var subType: String? = "null"
    var details: String? = "null"
    var isFree: Boolean = false
    var status: String? = TeamStatus.TRYOUT.status
    var mode: String? = TeamMode.TRYOUT.mode
    var imgUrl: String? = "null"
    var sport: String? = "null"
    var chatEnabled: Boolean = false
}

fun DataSnapshot.toRealmTeam(realm: Realm): Team {
    val team = Team()
    team.id = this.getString("id") ?: newUUID()
    team.headCoachId = this.getString("headCoachId")
    team.headCoachName = this.getString("headCoachName")
    this.addChildStringList("coaches", team.coaches)
    this.addChildStringList("managers", team.managers)
    this.addChildStringList("organizations", team.organizations)
    this.addChildStringList("locationIds", team.locationIds)
    team.rosterId = this.getString("rosterId")
    team.tryoutId = this.getString("tryoutId")
    team.season = this.getString("season")
    team.schedule = this.getString("schedule")
    team.year = this.getString("year")
    team.ageGroup = this.getString("ageGroup")
    team.isActive = this.getBoolean("isActive") ?: false
    team.gender = this.getString("gender")
    team.hasReview = this.getBoolean("hasReview") ?: false
    team.dateCreated = this.getString("dateCreated")
    team.dateUpdated = this.getString("dateUpdated")
    team.name = this.getString("name")
    team.type = this.getString("type")
    team.subType = this.getString("subType")
    team.details = this.getString("details")
    team.isFree = this.getBoolean("isFree") ?: false
    team.status = this.getString("status")
    team.mode = this.getString("mode")
    team.imgUrl = this.getString("imgUrl")
    team.sport = this.getString("sport")
    team.chatEnabled = this.getBoolean("chatEnabled") ?: false
    // Save to Realm
    realm.safeWrite { realm.insertOrUpdate(team) }
    realm.refresh()
    return team
}


