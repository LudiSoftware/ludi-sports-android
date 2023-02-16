package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class TeamRef : RealmObject(), Serializable {
    var teamId: String? = null
    var name: String? = null
    var sport: String? = null
    var status: String? = null
}

open class Team : RealmObject(), Serializable {

    @PrimaryKey
    var teamId: String = newUUID()
    var base: YsrRealmObject? = YsrRealmObject()
    var teamName: String? = null
    var headCoachId: String? = null
    var headCoachName: String? = null
    var coachRefs: RealmList<CoachRef>? = null
    var managerRefs: RealmList<ParentRef>? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
    var roster: RealmList<PlayerRef>? = null
    var year: String? = null
    var ageGroup: String? = null
    var isActive: Boolean = false
    var gender: String? = null
    var hasReview: Boolean = false
    var reviews: ReviewBundle? = null

}

