package io.usys.report.realm

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.users.safeUserId

//This works.
inline fun <reified T: RealmObject> Realm.findByField(field:String="id", value: String): T? {
    return this.where(T::class.java).equalTo(field, value).findFirst()
}

inline fun <reified T: RealmObject> Realm.findAllByField(field:String="id", value: String): RealmResults<T>? {
    return this.where(T::class.java).equalTo(field, value).findAll()
}

fun Realm.findCoachBySafeId(): Coach? {
    var temp: Coach? = null
    this.safeUserId {
        temp = this.where(Coach::class.java).equalTo("id", it).findFirst()
    }
    return temp
}
fun Realm.findTeamById(teamId:String): Team? {
    return this.where(Team::class.java).equalTo("id", teamId).findFirst()
}

fun Realm.findRosterById(rosterId:String): Roster? {
    return this.where(Roster::class.java).equalTo("id", rosterId).findFirst()
}
fun Realm.findPlayerRefById(playerId:String): PlayerRef? {
    return this.where(PlayerRef::class.java).equalTo("playerId", playerId).findFirst()
}
fun Realm.getRosterIdForTeamId(teamId:String): String? {
    return this.findTeamById(teamId)?.rosterId
}
fun Realm.getPlayersForRosterId(rosterId:String): RealmList<PlayerRef>? {
    return this.findRosterById(rosterId)?.players
}