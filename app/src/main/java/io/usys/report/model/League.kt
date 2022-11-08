package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.applyAndFireSave
import io.usys.report.utils.newUUID

/**
 * Created by ChazzCoin : October 2022.
 */
open class League : RealmObject() {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    @PrimaryKey
    var id: String? = "" //UUID
    //Basic Info
    var name: String? = ""
    var details: String? = ""
    var dateCreated: String? = "" // timestamp
    //References
    var ownerId: String? = "unassigned"
    var ownerName: String? = "unassigned"
    var organizationId: String? = ""
    var organizationIds: RealmList<String>? = RealmList()
    //Address
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    //Sport
    var sport: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    //Review
    var hasReview: Boolean = false
    var reviews: RealmList<String>? = RealmList()

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}


fun createLocation() {
    Coach().applyAndFireSave() {
        it.id = newUUID()
        it.name = "Lucas Romeo"
        it.sport = "soccer"
        it.organizationId = "d72c7cd5-1789-437c-b620-bb1383d629e0"
    }
}


