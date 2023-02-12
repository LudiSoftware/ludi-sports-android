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
open class Coach : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var imgUrl: String = ""
    var ownerId: String = "unassigned"
    var ownerName: String = "unassigned"
    var organizationId: String = ""
    var organizationName: String = ""
    var organizationIds: RealmList<String>? = RealmList()
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
    var reviews: RealmList<String>? = RealmList()
    var ratingScore: String = ""
    var ratingCount: String = ""
    var reviewAnswerCount: String = ""
    var reviewDetails: String = ""

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

}


fun createCoach() {
    Coach().applyAndFireSave() {
        it.sport = "soccer"
        it.organizationId = "d72c7cd5-1789-437c-b620-bb1383d629e0"
    }
}


