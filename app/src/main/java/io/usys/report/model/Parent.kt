package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class Parent : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    var ownerId: String = ""
    var ownerName: String = ""
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var imgUrl: String = ""
    var details: String = ""
    var isFree: Boolean = false
    var hasReview: Boolean = false
    var player: Boolean = false
    var playerIds: RealmList<String>? = RealmList()
    var team: Boolean = false
    var teamIds: RealmList<String>? = RealmList()
    var organizationIds: RealmList<String>? = RealmList()
    var reviews: RealmList<String>? = RealmList()
    var ratingScore: String = ""
    var ratingCount: String = ""
    var reviewAnswerCount: String = ""
    var reviewDetails: String = ""

}




