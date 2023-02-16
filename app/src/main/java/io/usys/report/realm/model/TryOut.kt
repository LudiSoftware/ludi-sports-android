package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.AuthTypes.Companion.UNASSIGNED
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class CoachNote : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var coachId: String? = null
    var aboutTeamId: String? = null
    var aboutPlayerId: String? = null
    var message: String? = null
}

open class TryOut : RealmObject(), Serializable {

    @PrimaryKey
    var teamId: String = newUUID()
    var coachNotes: RealmList<CoachNote>? = null
    var playersRegisteredRefs: RealmList<PlayerRef>? = null
    var playersRankedRefs: RealmList<PlayerRef>? = null
    var headCoachId: String? = null
    var headCoachName: String? = null
    var coachRefs: RealmList<CoachRef>? = null
    var managerRefs: RealmList<ParentRef>? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
    var year: String? = null
    var ageGroup: String? = null
    var isActive: Boolean = false
    var gender: String? = null
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


    //todo: save tryout to firebase/realm
    //todo: create note
    //todo: register player


}
