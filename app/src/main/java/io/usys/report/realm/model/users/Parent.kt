package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.androidx.getTimeStamp

/**
 * Created by ChazzCoin : November 2022.
 */
open class Parent : RealmObject() {

    @PrimaryKey
    var id: String = ""
    var userId: String? = "unassigned"
    var hasPlayer: Boolean = false
    var players: RealmList<String>? = null
    var team: Boolean = false
    var teams: RealmList<String>? = null
    var organizations: RealmList<String>? = null
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




