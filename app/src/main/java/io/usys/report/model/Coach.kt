package io.usys.report.model

import androidx.room.PrimaryKey
import io.realm.RealmList
import io.realm.RealmObject
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class Coach : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var ownerId: String? = "unassigned"
    var ownerName: String? = "unassigned"
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sport: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = ""
    var staff: RealmList<String>? = null
    var estPeople: String? = ""
    var reviews: RealmList<String>? = null

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}



