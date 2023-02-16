package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.usys.report.firebase.fireSaveCoachToFirebaseAsync
import io.usys.report.realm.executeRealm
import io.usys.report.realm.realm
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class CoachRef : RealmObject(), Serializable {
    var coachId: String? = null
    var coachName: String? = null
    var coachTitle: String? = null
}
open class Coach : RealmObject() {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }
    @PrimaryKey
    var coachId: String = "unassigned"
    var coachName: String = "unassigned"
    var coachTitle: String? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
    var teamRefs: RealmList<TeamRef>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null

    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = null
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

    fun isIdenticalCoach(userTwo: Coach): Boolean {
        if (this == userTwo) return true
        return false
    }

    fun saveToFirebase(): Coach {
        fireSaveCoachToFirebaseAsync(this)
        return this
    }

    fun saveCoachToRealm(): Coach {
        executeRealm {
            it.insertOrUpdate(this)
        }
        return this
    }

    fun updateAndSave(newUser: Coach?) {
        if (newUser.isNullOrEmpty()) return
        if (this.isIdenticalCoach(newUser!!)) return
        executeRealm {
            this.apply {
//                this.base = newUser.base
                this.coachId = newUser.coachId
                this.coachName = newUser.coachName
                this.organizationRefs = newUser.organizationRefs
                this.teamRefs = newUser.teamRefs
                this.hasReview = newUser.hasReview
                this.reviewBundle = newUser.reviewBundle
            }
        }
        this.saveCoachToRealm()
    }


}

/**
 * USER COACH
 */
fun getCoachByOwnerId(ownerId:String) : Coach? {
    var coach: Coach? = null
    try {
        executeRealm {
            coach = realm().where(Coach::class.java).equalTo("ownerId", ownerId).findFirst()
            if (coach == null) {
                coach = realm().createObject(Coach::class.java, ownerId)
            }
        }
        return coach
    } catch (e: Exception) { e.printStackTrace() }
    return coach
}


