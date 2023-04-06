package io.usys.report.firebase

import io.realm.RealmList
import io.usys.report.realm.model.PlayerRef

class FirebaseModels {

    data class PlayerRefData(
        var id: String?,
        var playerId: String?,
        var name: String?,
        var rank: Int?,
        var number: Int,
        var tryoutTag: String?,
        var position: String?,
        var foot: String?,
        var dob: String?,
        var imgUrl: String?,
        var pointX: Int?,
        var pointY: Int?,
        var color: String?,
        var listPosition: Int?,
        var status: String?,
        var userId: String?,
        var parentId: String?,
        var orderIndex: Int?,
        // play metrics
        var team: String?,
        var season: String?,
        var player_first_name: String?,
        var player_last_name: String?,
        var gender: String?,
        var birth_date: String?,
        var parent1_email: String?,
        var parent1_first_name: String?,
        var parent1_last_name: String?,
        var parent1_mobile_number: String?,
        var parent2_email: String?,
        var parent2_first_name: String?,
        var parent2_last_name: String?,
        var parent2_mobile_number: String?,
        var street: String?,
        var city: String?,
        var state: String?,
        var zip: String?,
    )

}

fun realmListToDataList(realmList: RealmList<PlayerRef>): List<FirebaseModels.PlayerRefData> {
    return realmList.map { playerRef ->
        FirebaseModels.PlayerRefData(
            id = playerRef.id,
            playerId = playerRef.playerId,
            name = playerRef.name,
            rank = playerRef.rank,
            number = playerRef.number,
            tryoutTag = playerRef.tryoutTag,
            position = playerRef.position,
            foot = playerRef.foot,
            dob = playerRef.dob,
            imgUrl = playerRef.imgUrl,
            pointX = playerRef.pointX,
            pointY = playerRef.pointY,
            color = playerRef.color,
            listPosition = playerRef.listPosition,
            status = playerRef.status,
            userId = playerRef.userId,
            parentId = playerRef.parentId,
            orderIndex = playerRef.orderIndex,
            team = playerRef.team,
            season = playerRef.season,
            player_first_name = playerRef.player_first_name,
            player_last_name = playerRef.player_last_name,
            gender = playerRef.gender,
            birth_date = playerRef.birth_date,
            parent1_email = playerRef.parent1_email,
            parent1_first_name = playerRef.parent1_first_name,
            parent1_last_name = playerRef.parent1_last_name,
            parent1_mobile_number = playerRef.parent1_mobile_number,
            parent2_email = playerRef.parent2_email,
            parent2_first_name = playerRef.parent2_first_name,
            parent2_last_name = playerRef.parent2_last_name,
            parent2_mobile_number = playerRef.parent2_mobile_number,
            street = playerRef.street,
            city = playerRef.city,
            state = playerRef.state,
            zip = playerRef.zip
        )
    }
}


