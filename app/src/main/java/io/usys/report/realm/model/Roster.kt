package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.realm
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : November 2022.
 */
open class RosterRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var rosterId: String? = "unassigned"
    var organizationId: String? = "unassigned"
    var coachId: String? = "unassigned"
    var teamId: String? = "unassigned"
}


open class Roster : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var organizationId: String? = "unassigned"
    var teamId: String? = "unassigned"
    var coachId: String? = "unassigned"
    var name: String? = "unassigned"
    var season: String? = "unassigned"
    var year: String? = "unassigned"
    var gender: String? = "unassigned"
    var isLocked: Boolean = false
    var status: String? = "open"
    var sport: String? = "unassigned"
    var players: RealmList<PlayerRef>? = RealmList()

}
fun Roster?.getPlayer(playerId: String): PlayerRef? {
    if (this.isNullOrEmpty()) return null
    var player: PlayerRef? = null
    this?.players?.forEach { itPlayerRef ->
        if (itPlayerRef.id == playerId) {
            player = itPlayerRef
            return@forEach
        }
    }
    if (player == null) {
        player = realm().where(PlayerRef::class.java).equalTo("id", playerId).findFirst()
    }
    return player
}