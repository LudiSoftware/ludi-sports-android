package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.YsrMode
import io.usys.report.utils.applyAndFireSave
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class Team : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var headCoachId: String = UNASSIGNED
    var headCoachName: String = UNASSIGNED
    var coachIds: RealmList<String>? = null
    var managerId: String = UNASSIGNED
    var managerName: String = UNASSIGNED
    var managerIds: RealmList<String>? = null
    var organizationId: String = UNASSIGNED
    var organizationName: String = UNASSIGNED
    var organizationIds: RealmList<String>? = null
    var roster: RealmList<String>? = null
    var name: String = UNASSIGNED
    var sport: String = "soccer"
    var year: String = UNASSIGNED
    var ageGroup: String = UNASSIGNED
    var isActive: Boolean = false
    var gender: String = UNASSIGNED
    var type: String = UNASSIGNED
    var subType: String = UNASSIGNED
    var details: String = UNASSIGNED
    var isFree: Boolean = false
    var mode: String = "viewonly"
    var status: String = "active"
    var hasReview: Boolean = false
    var reviews: RealmList<String>? = null
    var ratingScore: String = UNASSIGNED
    var ratingCount: String = UNASSIGNED
    var reviewAnswerCount: String = UNASSIGNED
    var reviewDetails: String = UNASSIGNED

}


fun HashMap<String, Any>?.toTeamObject(): Team {
    val team = Team()
    this?.let {
        team.id = this["id"] as String
        team.dateCreated = this["dateCreated"] as String
        team.dateUpdated = this["dateUpdated"] as String
        team.headCoachId = this["headCoachId"] as String
        team.headCoachName = this["headCoachName"] as String
        team.coachIds = this["coachIds"] as? RealmList<String>?
        team.managerId = this["managerId"] as String
        team.managerName = this["managerName"] as String
        team.managerIds = this["managerIds"] as? RealmList<String>?
        team.organizationId = this["organizationId"] as String
        team.organizationName = this["organizationName"] as String
        team.organizationIds = this["organizationIds"] as? RealmList<String>?
        team.roster = this["roster"] as? RealmList<String>?
        team.name = this["name"] as String
        team.sport = this["sport"] as String
        team.year = this["year"] as String
        team.ageGroup = this["ageGroup"] as String
        team.isActive = this["isActive"] as Boolean
        team.gender = this["gender"] as String
        team.type = this["type"] as String
        team.subType = this["subType"] as String
        team.details = this["details"] as String
        team.isFree = this["isFree"] as Boolean
        team.mode = this["mode"] as String
        team.status = this["status"] as String
        team.hasReview = this["hasReview"] as Boolean
        team.reviews = this["reviews"] as? RealmList<String>?
        team.ratingScore = this["ratingScore"] as String
        team.ratingCount = this["ratingCount"] as String
        team.reviewAnswerCount = this["reviewAnswerCount"] as String
        team.reviewDetails = this["reviewDetails"] as String
    }
    return team
}

