package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.utils.*
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class OrganizationRef : RealmObject(), Serializable {
    var organizationId: String? = null
    var organizationName: String? = null
}
open class Organization : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_SPORTS = "sport"
    }


    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var name: String = UNASSIGNED //Name Given by Manager
    var addressOne: String = UNASSIGNED // 2323 20th Ave South
    var addressTwo: String = UNASSIGNED // 2323 20th Ave South
    var city: String = UNASSIGNED// Birmingham
    var state: String = UNASSIGNED // AL
    var zip: String = UNASSIGNED // 35223
    var sport: String = UNASSIGNED
    var type: String = UNASSIGNED
    var subType: String = UNASSIGNED
    var ratingScore: String = "0.0"
    var ratingCount: String = "0"
    var details: String = UNASSIGNED
    var officeHours: String = UNASSIGNED
    var websiteUrl: String = UNASSIGNED
    var imgOrgIconUri: String = UNASSIGNED
    var managerId: String = UNASSIGNED
    var managerName: String = UNASSIGNED
    var estMemberCount: String = UNASSIGNED
    var estStaffCount: String = UNASSIGNED
    var staffIds: RealmList<String>? = null
    var reviewIds: RealmList<String>? = null
    var leagueIds: RealmList<String>? = null
    var regionIds: RealmList<String>? = null
    var locationIds: RealmList<String>? = null
    var imgUris: RealmList<String>? = null
    var latitude: String = UNASSIGNED
    var csz: String = UNASSIGNED
    var fromGoogle: Boolean = false
    var longitude: String = UNASSIGNED
    var google_place_category: String = UNASSIGNED
    var business_status: String = UNASSIGNED
    var google_place_icon: String = UNASSIGNED
    var google_place_rating: String = UNASSIGNED
    var tags: String = UNASSIGNED
    var google_place_id: String = UNASSIGNED
    var google_place_user_ratings_total: String = UNASSIGNED

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }

    fun addUpdateOrgToFirebase(): Boolean {
        return fireAddUpdateDBAsync(FireTypes.ORGANIZATIONS, this.id, this)
    }

}

fun createOrg() {
    val org = Organization()
    org.apply {
        this.sport = "soccer"
        this.city = "birmingham"
        this.name = "USYSR Club"
    }
    fireAddUpdateDBAsync(FireTypes.ORGANIZATIONS, org.id, org)
}


// -> works but not used.
fun mapJsonToOrganization(json: HashMap<String,Any>): Organization {
    val org = Organization()
    org.id = json.getSafe("id").toString()
    org.name = json.getSafe("name", UNASSIGNED).toString()
    org.addressOne = json.getSafe("addressOne", UNASSIGNED).toString()
    org.addressTwo = json.getSafe("addressTwo", UNASSIGNED).toString()
    org.city = json.getSafe("city", UNASSIGNED).toString()
    org.state = json.getSafe("state", UNASSIGNED).toString()
    org.zip = json.getSafe("zip", UNASSIGNED).toString()
    org.sport = json.getSafe("sport", UNASSIGNED).toString()
    org.type = json.getSafe("type", UNASSIGNED).toString()
    org.subType = json.getSafe("subType", UNASSIGNED).toString()
    org.ratingScore = json.getSafe("ratingScore", "0.0").toString()
    org.ratingCount = json.getSafe("ratingCount", "0").toString()
    org.details = json.getSafe("details", UNASSIGNED).toString()
    org.officeHours = json.getSafe("officeHours", UNASSIGNED).toString()
    org.websiteUrl = json.getSafe("websiteUrl", UNASSIGNED).toString()
    org.imgOrgIconUri = json.getSafe("imgOrgIconUri", UNASSIGNED).toString()
    org.managerId = json.getSafe("managerId", UNASSIGNED).toString()
    org.managerName = json.getSafe("managerName", UNASSIGNED).toString()
    org.estMemberCount = json.getSafe("estMemberCount", UNASSIGNED).toString()
    org.estStaffCount = json.getSafe("estStaffCount", UNASSIGNED).toString()
    org.latitude = json.getSafe("latitude", UNASSIGNED).toString()
    org.longitude = json.getSafe("longitude", UNASSIGNED).toString()
    org.fromGoogle = json.getBoolean("fromGoogle", false)
    org.google_place_category = json.getSafe("google_place_category", UNASSIGNED).toString()
    org.business_status = json.getSafe("business_status", UNASSIGNED).toString()
    org.google_place_icon = json.getSafe("google_place_icon", UNASSIGNED).toString()
    org.google_place_id = json.getSafe("google_place_id", UNASSIGNED).toString()
    org.google_place_user_ratings_total =
        json.getSafe("google_place_user_ratings_total", UNASSIGNED).toString()
    org.csz = json.getSafe("csz", UNASSIGNED).toString()
    org.tags = json.getSafe("tags", UNASSIGNED).toString()

    return org
}