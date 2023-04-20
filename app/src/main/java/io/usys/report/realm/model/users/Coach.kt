package io.usys.report.realm.model

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.findCoachBySafeId
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Coach : RealmObject() {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }
    @PrimaryKey
    var id: String = "unassigned"
    var userId: String? = "unassigned"
    var name: String = "unassigned"
    var title: String? = null
    var organizationIds: RealmList<String>? = null
    var teams: RealmList<String>? = RealmList()
    var hasReview: Boolean = false
    var reviewIds: RealmList<String>? = null
    // base (12)
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var firstName: String? = "null"
    var lastName: String? = "null"
    var type: String? = "null"
    var subType: String? = "null"
    var details: String? = "null"
    var isFree: Boolean = true
    var status: String? = "null"
    var mode: String? = "null"
    var imgUrl: String? = "null"
    var sport: String? = "null"

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