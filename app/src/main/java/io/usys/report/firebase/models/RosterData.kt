package io.usys.report.firebase.models

import io.usys.report.firebase.FirebaseModels
import io.usys.report.firebase.realmListToDataList
import io.usys.report.realm.model.Roster

/** Firebase Model */
data class RosterData (
    var id: String?,
    var organizationId:String?,
    var teamId: String?,
    var coachId: String?,
    var name: String?,
    var season: String? ,
    var year: String? ,
    var gender: String?,
    var isLocked: Boolean,
    var status: String?,
    var sport: String?,
    var players: List<FirebaseModels.PlayerRefData>?,
)

/** Converter */
fun Roster.convertForFirebase(): RosterData {
    return RosterData(
        id = this.id,
        organizationId = this.organizationId,
        teamId = this.teamId,
        coachId = this.coachId,
        name = this.name,
        season = this.season,
        year = this.year,
        gender = this.gender,
        isLocked = this.isLocked,
        status = this.status,
        sport = this.sport,
        players = this.players?.let { realmListToDataList(it) }
    )
}
