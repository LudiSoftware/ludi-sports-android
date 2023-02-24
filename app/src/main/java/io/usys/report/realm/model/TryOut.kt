package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */


open class TryOut : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var isActive: Boolean = false
    var teamId: String? = null
    var headCoachId: String? = null
    var headCoachName: String? = null
    var notes: RealmList<Note>? = null
    // Master Roster for Tryouts
    var tryoutRoster: Roster? = null
    // Each Coach gets a their own List of Rosters.
    var coachRosters: RealmList<CoachRosters>? = null
    // References
    var coaches: RealmList<CoachRef>? = null
    var managers: RealmList<ParentRef>? = null
    var organizations: RealmList<OrganizationRef>? = null
    var schedule: Schedule? = null
    var year: String? = null
    var ageGroup: String? = null
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
    var chatEnabled: Boolean = false


    //todo: save tryout to firebase/realm
    //todo: create note
    //todo: register player


}
