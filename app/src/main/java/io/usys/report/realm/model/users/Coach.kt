package io.usys.report.realm.model.users

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.fireludi.fireSaveCoachToFirebaseAsync
import io.usys.report.realm.safeWrite
import io.usys.report.utils.*
import io.usys.report.utils.androidx.getTimeStamp

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

fun Realm.createCoach(user:User, saveToFirebase:Boolean=false) : Coach {
    if (user.isNullOrEmpty()) return Coach()
    val coach = Coach()
    this.safeWrite {
        coach.apply {
            this.id = user.id
            this.name = user.name ?: user.firstName ?: user.lastName ?: "UNKNOWN"
            this.imgUrl = user.photoUrl
            this.teams = RealmList("0")
            this.organizationIds = RealmList("0")
        }
        it.insertOrUpdate(coach)
    }
    if (saveToFirebase) fireSaveCoachToFirebaseAsync(coach)
    return coach
}