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
    // Main (3) TOTAL = (37)
    @PrimaryKey
    var id: String = newUUID()
    var officeHours: String? = null
    var websiteUrl: String? = null
    // Counts (3)
    var estMemberCount: Int? = 0
    var estStaffCount: Int? = 0
    var estTeamCount: Int? = 0
    // References (5)
    var staff: RealmList<CoachRef>? = null
    var coaches: RealmList<CoachRef>? = null
    var leagues: RealmList<LeagueRef>? = null
    var teams: RealmList<TeamRef>? = null
    var regions: RealmList<String>? = null
    // Locations (1)
    var locations: RealmList<LocationRef>? = null
    // Images (2)
    var imgUris: RealmList<String>? = null
    var imgOrgIconUri: String? = null
    // Ratings/Reviews (3)
    var ratingScore: Int? = 0
    var ratingCount: Int? = 0
    var reviews: RealmList<String>? = null
    // Google Specific (8)
    var fromGoogle: Boolean = false
    var google_place_category: String? = null
    var business_status: String? = null
    var google_place_icon: String? = null
    var google_place_rating: String? = null
    var google_place_id: String? = null
    var google_place_user_ratings_total: String? = null
    var tags: RealmList<String>? = null
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
