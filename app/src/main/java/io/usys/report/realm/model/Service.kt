package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.androidx.getTimeStamp
import io.usys.report.utils.newUUID

/**
 * Created by ChazzCoin : October 2022.
 */
open class Service : RealmObject() {

    companion object {
        const val PENDING : String = "pending"
        const val BOOKED : String = "booked"
    }

    @PrimaryKey
    var id: String = newUUID() //UUID
    var ownerId: String? = null
    var ownerName: String? = null
    var timeOfService: String? = null
    var dateOfService: String? = null
    var recurring: Boolean = false
    var maxPeople: Int? = null
    var ageRange: String? = null
    var cost: String? = null
    var staff: RealmList<String>? = null
    var reviews: RealmList<String>? = null
    var likes: Int? = null
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

}



