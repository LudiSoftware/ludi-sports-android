package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class OrganizationRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var organizationId: String? = ""
    var name: String? = null
}
open class Organization : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_SPORTS = "sport"
    }

    @PrimaryKey
    var id: String = newUUID()
    var ratingScore: String? = null
    var ratingCount: String? = null
    var officeHours: String? = null
    var websiteUrl: String? = null
    var imgOrgIconUri: String? = null
    var managerId: String? = null
    var managerName: String? = null
    var estMemberCount: String? = null
    var estStaffCount: String? = null
    var staff: RealmList<CoachRef>? = null
    var reviews: RealmList<String>? = null
    var leagues: RealmList<LeagueRef>? = null
    var regions: RealmList<String>? = null
    var locations: RealmList<LocationRef>? = null
    var imgUris: RealmList<String>? = null
    var fromGoogle: Boolean = false
    var google_place_category: String? = null
    var business_status: String? = null
    var google_place_icon: String? = null
    var google_place_rating: String? = null
    var tags: String? = null
    var google_place_id: String? = null
    var google_place_user_ratings_total: String? = null
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
    var chatEnabled: Boolean = false


    fun addUpdateOrgToFirebase(): Boolean {
        return fireAddUpdateDBAsync(FireTypes.ORGANIZATIONS, this.id, this)
    }

}
