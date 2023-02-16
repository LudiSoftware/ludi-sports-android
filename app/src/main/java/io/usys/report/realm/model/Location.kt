package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */

open class LocationRef : RealmObject(), Serializable {
    var locationId: String? = ""
    var name: String? = null
}

open class Location : RealmObject(), Serializable {

    @PrimaryKey
    var locationId: String = newUUID()
    var sports: RealmList<String>? = null
    var fields: RealmList<String>? = null
    var address: Address? = null
    var imgUris: RealmList<String>? = null
    var parkingInfo: String? = null // "Park on the third spot to the right"
    var estPeople: String? = null //Amount of expected people
    var locationManagerRefs: RealmList<Coach>? = null //Creators Display Name
    var organizationRefs: RealmList<OrganizationRef>? = null
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

}


open class Address : RealmObject(), Serializable {
    var name: String? = null
    var addressOne: String? = null // 2323 20th Ave South
    var addressTwo: String? = null // 2323 20th Ave South
    var city: String? = null // Birmingham
    var state: String? = null // AL
    var zip: String? = null // 35223
    var latitude: Double? = null
    var longitude: Double? = null
}









