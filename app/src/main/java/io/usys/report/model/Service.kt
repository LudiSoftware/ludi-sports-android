package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
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
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var ownerId: String? = "unassigned"
    var ownerName: String? = "unassigned"
    var addressOne: String = "" // 2323 20th Ave South
    var addressTwo: String = "" // 2323 20th Ave South
    var city: String = "" // Birmingham
    var state: String = "" // AL
    var zip: String = "" // 35223
    var status : String? = AVAILABLE //Has it been bought?
    var sport: String = "unassigned"
    var type: String = "unassigned"
    var subType: String = "unassigned"
    var timeOfService: String = ""
    var dateOfService: String = ""
    var maxPeople: Int = 0
    var details: String = ""
    var staff: RealmList<String>? = null
    var reviews: RealmList<String>? = null

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}



