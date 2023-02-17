package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class ParentRef : RealmObject(), Serializable {
    var id: String? = null
    var name: String? = null
    var playerRefs: RealmList<PlayerRef>? = null
    var teamRefs: RealmList<TeamRef>? = null
    var organizationRefs: RealmList<OrganizationRef>? = null

    fun getParent(): Parent? {
        return null
    }

}
open class Parent : RealmObject() {

    @PrimaryKey
    var id: String = ""
    var player: Boolean = false
    var playerRefs: RealmList<PlayerRef>? = null
    var team: Boolean = false
    var teamRefs: RealmList<TeamRef>? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
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

}




