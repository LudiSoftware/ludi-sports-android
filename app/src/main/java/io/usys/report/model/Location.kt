package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by ChazzCoin : October 2022.
 */
open class Location : RealmObject() {


    @PrimaryKey
    var id: String? = ""
    var name: String? = ""
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sports: RealmList<String>? = null
    var dateCreated: String? = "" // timestamp
    var parkingInfo: String? = "" // "Park on the third spot to the right"
    var estPeople: String? = "" //Amount of expected people
    var status : String? = "" //Has it been bought?
    var locationManager: String? = "" //Creators Display Name
    var organizationId: String? = ""
    var hasReview: Boolean = false
    var reviewId: String? = ""


}












