package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.applyAndFireSave
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class Service : RealmObject() {

    companion object {
        const val MORNING_TIME : String = "8AM-11AM"
        const val AFTERNOON_TIME : String = "12PM-3PM"
        const val EVENING_TIME : String = "4PM-7PM"
        const val NIGHT_TIME : String = "8PM-10PM"

        const val PRICE : String = "5.00"

        const val AVAILABLE : String = "available"
        const val PENDING : String = "pending"
        const val BOOKED : String = "booked"
    }

    @PrimaryKey
    var id: String = newUUID() //UUID
    var dateCreated: String = getTimeStamp()
    var name: String = "" //Name Given by Manager
    var ownerId: String = "unassigned"
    var ownerName: String = "unassigned"
    var addressOne: String = "" // 2323 20th Ave South
    var addressTwo: String = "" // 2323 20th Ave South
    var city: String = "" // Birmingham
    var state: String = "" // AL
    var zip: String = "" // 35223
    var imgUrl: String = ""
    var status : String = AVAILABLE //Has it been bought?
    var sport: String = "unassigned"
    var type: String = "unassigned"
    var subType: String = "unassigned"
    var timeOfService: String = ""
    var dateOfService: String = ""
    var recurring: Boolean = false
    var maxPeople: Int = 0
    var ageRange: String = "7-14"
    var cost: String = "0.00"
    var details: String = ""
    var staff: RealmList<String>? = null
    var reviews: RealmList<String>? = null
    var likes: Int = 0

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}

// ckrphone@gmail.com = tnmjTR7r1HPwIaBb2oXrDrwXT842
fun createService() {
    Service().applyAndFireSave {
        it.name = "YSR Private Training"
        it.ownerId = "tnmjTR7r1HPwIaBb2oXrDrwXT842"
        it.ownerName = "Lucas Romeo"
        it.sport = "soccer"
        it.timeOfService = "6pm-8pm"
        it.dateOfService = "Wednesday-Friday"
        it.recurring = true
        it.maxPeople = 10
        it.ageRange = "8-14"
        it.details = "I specialize in one on one training but love working with small groups of passionate kids."
    }
}


