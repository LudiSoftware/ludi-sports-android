package io.usys.report.model

import androidx.room.PrimaryKey
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.db.FireDB
import io.usys.report.db.addUpdateDB
import io.usys.report.utils.newUUID
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
    var organizationId: String? = ""
    var organizationIds: RealmList<String>? = RealmList()
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sport: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = ""
    var estPeople: String? = ""
    var reviews: RealmList<String>? = RealmList()

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}


fun createCoach() {
    val coach = Coach()
    coach.apply {
        this.id = newUUID()
        this.name = "Coach Romeo"
        this.organizationId = "d72c7cd5-1789-437c-b620-bb1383d629e0"
        this.ownerId = "tnmjTR7r1HPwIaBb2oXrDrwXT842"
    }
    addUpdateDB(FireDB.COACHES, coach.id.toString(), coach)
}


