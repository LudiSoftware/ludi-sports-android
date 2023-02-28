package io.usys.report.realm.model

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.fireSaveCoachToFirebaseAsync
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.realmUser
import io.usys.report.realm.writeToRealm
import io.usys.report.realm.realm
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class CoachRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var coachId: String? = ""
    var name: String? = null
    var isHeadCoach: Boolean = false
    var title: String? = null
}
open class Coach : RealmObject() {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }
    @PrimaryKey
    var id: String = "unassigned"
    var userId: String? = "unassigned"
    var name: String = "unassigned"
//    var organizations: RealmList<OrganizationRef>? = RealmList()
    var teams: RealmList<TeamRef>? = RealmList()
//    var evaluations: RealmList<PlayerEvaluationRef>? = RealmList()
    var hasReview: Boolean = false
//    var reviewBundle: ReviewBundle? = null

    // base (12)
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var firstName: String? = null
    var lastName: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean = false
    var status: String? = null
    var mode: String? = null
    var imgUrl: String? = null
    var sport: String? = null

}

/**
 * USER COACH
 */
inline fun Realm.safeCoach(block: (Coach) -> Unit) {
    val coach = this.findCoachBySafeId()
    coach?.let {
        block(it)
    }
}