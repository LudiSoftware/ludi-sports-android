package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.androidx.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */

open class LeagueRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var leagueId: String? = null
    var leagueName: String? = null
    var sport: String? = null
}

open class League : RealmObject() {

    @PrimaryKey
    var id: String = newUUID()
    var name: String? = null
    var organizationIds: RealmList<String>? = null
    var hasReview: Boolean = false
    var reviews: ReviewBundle? = null
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
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





