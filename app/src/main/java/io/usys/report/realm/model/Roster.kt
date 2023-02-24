package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class CoachRosters : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var coachId: String? = ""
    var rosters: RealmList<Roster>? = RealmList()
}


open class Roster : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var teamId: String? = ""
    var players: RealmList<PlayerRef>? = RealmList()
}
