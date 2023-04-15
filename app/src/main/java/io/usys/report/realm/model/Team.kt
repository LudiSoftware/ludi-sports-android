package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.providers.TeamMode
import io.usys.report.providers.TeamStatus
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */



open class TeamRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var teamId: String? = "null"
    var tryoutId: String? = "null"
    var rosterId: String? = "null"
    var name: String? = "null"
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var year: String? = "null"
    var ageGroup: String? = "null"
    var gender: String? = "null"
    var sport: String? = "null"
    var status: String? = TeamMode.TRYOUT.mode
    var imgUrl: String? = "null"
}

open class Team : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var headCoachId: String? = "null"
    var headCoachName: String? = "null"
    var coaches: RealmList<CoachRef>? = RealmList()
//    var managers: RealmList<String>? = RealmList()
//    var organizations: RealmList<String>? = RealmList()
    var rosterId: String? = "null"
    var tryoutId: String? = "null"
    var season: String? = "null"
    var locationIds: RealmList<String>? = null
//    var schedule: Schedule? = null
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




