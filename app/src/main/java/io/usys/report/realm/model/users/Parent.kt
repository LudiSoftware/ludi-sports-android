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
open class ParentRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var parentId: String? = newUUID()
    var name: String? = null
    var isManager: Boolean = false
    var players: RealmList<PlayerRef>? = null
    var teams: RealmList<TeamRef>? = null
    var organizations: RealmList<OrganizationRef>? = null

    fun getParent(): Parent? {
        return null
    }

}
open class Parent : RealmObject() {

    @PrimaryKey
    var id: String = ""
    var hasPlayer: Boolean = false
    var players: RealmList<PlayerRef>? = null
    var team: Boolean = false
    var teams: RealmList<TeamRef>? = null
    var organizations: RealmList<OrganizationRef>? = null
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




