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
    // Primary (5) -> 27
    @PrimaryKey
    var id: String = newUUID()
    var isActive: Boolean = false
    var isFinalized: Boolean = false
    var teamId: String? = null
    var headCoachId: String? = null
    var headCoachName: String? = null
    //TODO: ADD FIELD ABILITY!
    var fieldNumber: String? = null
    //TODO: Create a Form Object to hold urls for images uploaded and type of form.
    var formUrls: RealmList<String>? = null
    // Notes (1)
    var notes: RealmList<Note>? = null
    // Master Roster for Tryouts (1)
    var tryoutRoster: Roster? = null
    // Each Coach gets a their own List of Rosters. (1)
    var coachRosters: RealmList<CoachRosters>? = null
    // References (3)
    var coaches: RealmList<CoachRef>? = null
    var managers: RealmList<ParentRef>? = null
    var organizations: RealmList<OrganizationRef>? = null
    // Schedule (1)
    var schedule: Schedule? = null
    // Team Attributes (3)
    var year: String? = null
    var ageGroup: String? = null
    var gender: String? = null
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


    //todo: save tryout to firebase/realm
    //todo: create note
    //todo: register player


}
