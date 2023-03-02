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
    @PrimaryKey
    var id: String = newUUID()
    var locationId: String? = "unassigned"
    var fieldName: String? = "unassigned"
    var name: String? = "null"
}

open class Location : RealmObject(), Serializable {

    //(11) 27 + 12 = 39
    @PrimaryKey
    var id: String = newUUID()
    var sports: RealmList<String>? = null
    var fields: RealmList<String>? = null
    var address: Address? = null
    var imgUris: RealmList<String>? = null
    var parkingInfo: String? = null // "Park on the third spot to the right"
    var estPeople: String? = null //Amount of expected people
    var managerIds: RealmList<String>? = null //Creators Display Name
    var organizationRefs: RealmList<OrganizationRef>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null
    //Google (16)
    var googlePlaceBusinessStatus: String? = null
    var googlePlaceLat: String? = null
    var googlePlaceLng: String? = null
    var googlePlaceIcon: String? = null
    var iconBackgroundColor: String? = null
    var iconMaskBaseUri: String? = null
    var googlePlaceName: String? = null
    var googlePlacePhotos: RealmList<String>? = null
    var googlePlaceId: String? = null
    var googlePlaceRating: String? = null
    var googlePlaceScope: String? = null
    var googlePlaceTypes: RealmList<String>? = null
    var googlePlaceUserRatingsTotal: Int? = 0
    var googlePlaceAddress: String? = null
    var googlePlaceCsz: String? = null
    var googlePlaceCategory: String? = null
    // base (12)
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean = false
    var status: String? = null
    var mode: String? = null
    var imgUrl: String? = null
    var sport: String? = null
    var chatEnabled: Boolean = false

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









