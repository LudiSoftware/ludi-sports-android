package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */

open class PlayerRef : RealmObject(), Serializable {
    @PrimaryKey
    var playerId: String? = "null"
    var id: String? = newUUID()
    var name: String? = "null"
    var rank: Int? = 0
    var tryoutTag: String? = "null"
    var imgUrl: String? = "null"

    fun getPlayer(): Player? {
        return null
    }
}

open class Player : RealmObject() {

    @PrimaryKey
    var id: String = newUUID()
    var teamRef: TeamRef? = TeamRef()
    var rank: Int = 0
    var number: Int = 0
    var tryoutTag: String? = null
    var notes: RealmList<Note>? = null
    var age: Int = 0
    var dob: String? = null
    var position: String? = null
    //Extras
    var playerName: String? = null
    var teamName: String? = null
    var organizationRefs: RealmList<OrganizationRef>? = null
    var teamIds: RealmList<String>? = null
    var hasReview: Boolean = false
    var reviewBundle: ReviewBundle? = null
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


inline fun getPlayerRefsByTeamId(id:String, block: (RealmList<PlayerRef>) -> Unit) {
    val team = getTeamById(id)
    team?.roster?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}

