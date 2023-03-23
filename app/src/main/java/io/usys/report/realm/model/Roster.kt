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
//open class RosterRef : RealmObject(), Serializable {
//    @PrimaryKey
//    var id: String? = newUUID()
//    var rosterId: String? = "unassigned"
//    var organizationId: String? = "unassigned"
//    var coachId: String? = "unassigned"
//    var teamId: String? = "unassigned"
//}

const val ROSTER_STATUS_OPEN = "open"
const val ROSTER_STATUS_CLOSED = "closed"
const val ROSTER_STATUS_READY_FOR_SUBMISSION = "ready for submission"
const val ROSTER_STATUS_SUBMITTED = "submitted"
const val ROSTER_STATUS_ACCEPTED = "accepted"
const val ROSTER_STATUS_REJECTED = "rejected"

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

fun Roster?.finalizeForSubmission(): Roster? {
    this?.apply {
        this.isLocked = true
        this.status = ROSTER_STATUS_READY_FOR_SUBMISSION
    }
    return this
}

fun Roster?.setPlayerAsAccepted(playerId: String): Roster? {
    this?.apply {
        this.players?.find { it.id == playerId }?.apply {
            this.status = PLAYER_STATUS_ACCEPTED
        }
        this.isLocked = true
        this.status = ROSTER_STATUS_READY_FOR_SUBMISSION
    }
    return this
}

fun Roster?.setPlayerAsRejected(playerId: String): Roster? {
    this?.apply {
        this.players?.find { it.id == playerId }?.apply {
            this.status = PLAYER_STATUS_REJECTED
        }
        this.isLocked = true
        this.status = ROSTER_STATUS_READY_FOR_SUBMISSION
    }
    return this
}

fun Roster?.setPlayerAsOffered(playerId: String): Roster? {
    this?.apply {
        this.players?.find { it.id == playerId }?.apply {
            this.status = PLAYER_STATUS_OFFERED
        }
        this.isLocked = true
        this.status = ROSTER_STATUS_READY_FOR_SUBMISSION
    }
    return this
}