package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 *
self.id = tryout_obj.get('id', str(uuid.uuid4()))
# From Team (5)
self.isActive = tryout_obj.get('isActive', True)
self.isFinalized = tryout_obj.get('isFinalized', False)
self.teamId = tryout_obj.get('teamId', "9374e9f6-53ce-4ca5-90c6-cd613ad52c6a")
self.headCoachId = tryout_obj.get('headCoachId', headCoachId)
self.headCoachName = tryout_obj.get('headCoachName', "Chazz Romeo")
# References (3)
self.coachIds = tryout_obj.get('coachIds', [dg.generate_coachRef(userId=headCoachId)])
self.managerIds = tryout_obj.get('managerIds', [])
self.organizationIds = tryout_obj.get('organizationIds', [])
# Rosters (1)
self.rosterId = tryout_obj.get('rosterId', "unassigned")
# Schedule (1)
# self.schedule = tryout_obj.get('schedule', dg.generate_tryout_schedule())
# Team Attributes (3)
self.year = tryout_obj.get('year', dg.generate_team_year())
self.ageGroup = tryout_obj.get('ageGroup', dg.generate_age_group())
self.gender = tryout_obj.get('gender', "female")
#base (12)
self.dateCreated = tryout_obj.get('dateCreated', str(time.time()))
self.dateUpdated = tryout_obj.get('dateUpdated', str(time.time()))
self.name = tryout_obj.get('name', "Tryouts: Fall2023/Spring2024")
self.type = tryout_obj.get('type', "competitive")
self.subType = tryout_obj.get('subType', "youth")
self.details = tryout_obj.get('details', "This is a no joke tryout joel!")
self.isFree = tryout_obj.get('isFree', False)
self.status = tryout_obj.get('status', "open")
self.mode = tryout_obj.get('mode', "edit")
self.imgUrl = tryout_obj.get('imgUrl', "")
self.sport = tryout_obj.get('sport', "soccer")
self.chatEnabled = tryout_obj.get('chatEnabled', True)
 */

open class TryOut : RealmObject(), Serializable {
    // Primary (5) -> 28
    @PrimaryKey
    var id: String = newUUID()
    var isActive: Boolean = false
    var isFinalized: Boolean = false
    var teamId: String? = null
    var headCoachId: String? = null
    var headCoachName: String? = null
    //TODO: ADD FIELD ABILITY!
//    var locoationId: String? = null
//    var fieldNumber: String? = null
    //TODO: Create a Form Object to hold urls for images uploaded and type of form.
//    var formUrls: RealmList<String>? = null
    // Master Roster for Tryouts (1)
    var rosterId: String? = null
    // References (3)
    var coachIds: RealmList<String>? = null
    var managerIds: RealmList<String>? = null
    var organizationIds: RealmList<String>? = null
    // Schedule (1)
//    var schedule: Schedule? = null
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
