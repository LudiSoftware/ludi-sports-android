package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.YsrMode
import io.usys.report.utils.applyAndFireSave
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class Team : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_ORGANIZATION = "organizationId"
    }

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var name: String = UNASSIGNED //Name Given by Manager
    var imgUrl: String = UNASSIGNED
    var coachId: String = UNASSIGNED
    var coachName: String = UNASSIGNED
    var parentId: String = UNASSIGNED
    var parentName: String = UNASSIGNED
    var organizationId: String = UNASSIGNED
    var organizationName: String = UNASSIGNED
    var organizationIds: RealmList<String>? = RealmList()
    var sport: String = UNASSIGNED
    var type: String = UNASSIGNED
    var subType: String = UNASSIGNED
    var details: String = UNASSIGNED
    var email: String = UNASSIGNED
    var phone: String = UNASSIGNED
    var isFree: Boolean = false
    var mode: String = YsrMode.VIEW_ONLY
    var hasReview: Boolean = false
    var reviews: RealmList<String>? = RealmList()
    var ratingScore: String = UNASSIGNED
    var ratingCount: String = UNASSIGNED
    var reviewAnswerCount: String = UNASSIGNED
    var reviewDetails: String = UNASSIGNED

}




