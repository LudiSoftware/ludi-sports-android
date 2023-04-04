package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable
/**
 * Created by ChazzCoin : November 2022.
 */

const val PLAYER_STATUS_OPEN = "open"
const val PLAYER_STATUS_CLOSED = "closed"
const val PLAYER_STATUS_SELECTED = "selected"
const val PLAYER_STATUS_OFFERED = "offered"
const val PLAYER_STATUS_PENDING = "pending"
const val PLAYER_STATUS_ACCEPTED = "accepted"
const val PLAYER_STATUS_REJECTED = "rejected"
open class PlayerRef : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var playerId: String? = newUUID()
    var name: String? = "unassigned"
    var rank: Int? = 0
    var number: Int = 0
    var tryoutTag: String? = "unassigned"
    var position: String? = "unassigned"
    var foot: String? = "unassigned"
    var dob: String? = "unassigned"
    var imgUrl: String? = "unassigned"
    var pointX: Int? = 0
    var pointY: Int? = 0
    var color: String? = "unassigned"
    var listPosition: Int? = 0
    var status: String? = "unassigned"
    var userId: String? = "unassigned"
    var parentId: String? = "unassigned"
    var orderIndex: Int? = 0
    // play metrics
    var team: String? = "unassigned"
    var season: String? = "unassigned"
    var player_first_name: String? = "unassigned"
    var player_last_name: String? = "unassigned"
    var gender: String? = "unassigned"
    var birth_date: String? = "unassigned"
    var parent1_email: String? = "unassigned"
    var parent1_first_name: String? = "unassigned"
    var parent1_last_name: String? = "unassigned"
    var parent1_mobile_number: String? = "unassigned"
    var parent2_email: String? = "unassigned"
    var parent2_first_name: String? = "unassigned"
    var parent2_last_name: String? = "unassigned"
    var parent2_mobile_number: String? = "unassigned"
    var street: String? = "unassigned"
    var city: String? = "unassigned"
    var state: String? = "unassigned"
    var zip: String? = "unassigned"
}


