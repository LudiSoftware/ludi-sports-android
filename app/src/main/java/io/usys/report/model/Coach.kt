package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class Coach : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var imgUrl: String = ""
    var ownerId: String = "unassigned"
    var ownerName: String = "unassigned"
    var organizationId: String = ""
    var organizationName: String = ""
    var organizationIds: RealmList<String>? = null
    var addressOne: String = "" // 2323 20th Ave South
    var addressTwo: String = "" // 2323 20th Ave South
    var city: String = "" // Birmingham
    var state: String = "" // AL
    var zip: String = "" // 35223
    var sport: String = "unassigned"
    var details: String = ""
    var isFree: Boolean = false
    var teams: RealmList<String>? = null
    var hasReview: Boolean = false
    var reviews: RealmList<String>? = null
    var ratingScore: String = ""
    var ratingCount: String = ""
    var reviewAnswerCount: String = ""
    var reviewDetails: String = ""

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

    fun isIdentical(userTwo:Coach): Boolean {
        if (this == userTwo) return true
        return false
    }

    fun saveToRealm(): Coach {
        executeRealm {
            it.insertOrUpdate(this)
        }
        return this
    }

    fun update(newUser: Coach) {
        this.dateCreated = newUser.dateCreated
        this.dateUpdated = newUser.dateUpdated
        this.imgUrl = newUser.imgUrl
        this.ownerId = newUser.ownerId
        this.ownerName = newUser.ownerName
        this.organizationId = newUser.organizationId
        this.organizationName = newUser.organizationName
        this.organizationIds = newUser.organizationIds
        this.addressOne = newUser.addressOne
        this.addressTwo = newUser.addressTwo
        this.city = newUser.city
        this.state = newUser.state
        this.zip = newUser.zip
        this.sport = newUser.sport
        this.details = newUser.details
        this.isFree = newUser.isFree
        this.teams = newUser.teams
        this.hasReview = newUser.hasReview
        this.reviews = newUser.reviews
        this.ratingScore = newUser.ratingScore
        this.ratingCount = newUser.ratingCount
        this.reviewAnswerCount = newUser.reviewAnswerCount
        this.reviewDetails = newUser.reviewDetails
    }


}

//fun updateUserCoach(newUserCoach: Coach){
//    executeSession {
//        it?.userCoach = newUserCoach
//    }
//}

fun HashMap<String, Any>?.toCoachObject(): Coach {
    val coach = Coach()
    this?.let {
        coach.dateCreated = this["dateCreated"] as String
        coach.dateUpdated = this["dateUpdated"] as String
        coach.imgUrl = this["imgUrl"] as String
        coach.ownerId = this["ownerId"] as String
        coach.ownerName = this["ownerName"] as String
        coach.organizationId = this["organizationId"] as String
        coach.organizationName = this["organizationName"] as String
        coach.organizationIds = this["organizationIds"] as? RealmList<String>?
        coach.teams = this["teams"] as? RealmList<String>?
        coach.sport = this["sport"] as String
        coach.details = this["details"] as String
        coach.isFree = this["isFree"] as Boolean
        coach.hasReview = this["hasReview"] as Boolean
        coach.reviews = this["reviews"] as? RealmList<String>?
        coach.ratingScore = this["ratingScore"] as String
        coach.ratingCount = this["ratingCount"] as String
        coach.reviewAnswerCount = this["reviewAnswerCount"] as String
        coach.reviewDetails = this["reviewDetails"] as String
    }
    return coach
}

