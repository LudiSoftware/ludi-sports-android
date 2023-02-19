package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.fireSaveCoachToFirebaseAsync
import io.usys.report.realm.executeRealm
import io.usys.report.realm.realm
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class CoachRef : RealmObject(), Serializable {
    @PrimaryKey
    var coachId: String? = "null"
    var id: String? = newUUID()
    var name: String? = null
    var title: String? = null
}
open class Coach : RealmObject() {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }
    @PrimaryKey
    var id: String = "unassigned"
    var name: String = "unassigned"
    var title: String? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
    var teamRefs: RealmList<TeamRef>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null

    // base
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

    fun isIdenticalCoach(userTwo: Coach): Boolean {
        if (this == userTwo) return true
        return false
    }

    fun loadTeamRefsFromFirebase() {
        return
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
                this.id = newUser.id
                this.name = newUser.name
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
fun getCoachByCoachId(ownerId:String) : Coach? {
    var coach: Coach? = null
    try {
        coach = realm().where(Coach::class.java).equalTo("id", ownerId).findFirst()
        if (coach == null) {
            coach = realm().createObject(Coach::class.java, ownerId)
        }
        return coach
    } catch (e: Exception) { e.printStackTrace() }
    return coach
}
fun executeGetCoachByCoachId(ownerId:String) : Coach? {
    var coach: Coach? = null
    try {
        executeRealm {
            coach = realm().where(Coach::class.java).equalTo("id", ownerId).findFirst()
            if (coach == null) {
                coach = realm().createObject(Coach::class.java, ownerId)
            }
        }
        return coach
    } catch (e: Exception) { e.printStackTrace() }
    return coach
}

