package io.usys.report.realm

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.isNBE
import io.usys.report.utils.isNotNBE

/** General Queries **/
inline fun <reified T : RealmObject> Realm.ifObjectExists(id: String?, crossinline action: (T) -> Unit) {
    if (id.isNBE()) return
    val result = this.where(T::class.java).equalTo("id", id).findFirst()
    result?.let {
        action(it)
    }
}
inline fun <reified T : RealmObject> Realm.ifObjectDoesNotExist(id: String?, crossinline action: (String) -> Unit) {
    if (id.isNBE()) return
    val result = this.where(T::class.java).equalTo("id", id).findFirst()
    if (result == null) {
        id?.let { action(id) }
    }
}
inline fun <reified T: RealmObject> Realm.findByField(field:String="id", value: String?): T? {
    return this.where(T::class.java).equalTo(field, value).findFirst()
}
inline fun <reified T: RealmObject> Realm.findAllByField(field:String="id", value: String?): RealmResults<T>? {
    return this.where(T::class.java).equalTo(field, value).findAll()
}

/** Coach Queries **/
fun Realm.findCoachBySafeId(): Coach? {
    var temp: Coach? = null
    this.safeUserId {
        temp = this.where(Coach::class.java).equalTo("id", it).findFirst()
    }
    return temp
}
/** Team Queries **/
fun Realm.findTeamById(teamId:String?): Team? {
    return this.where(Team::class.java).equalTo("id", teamId).findFirst()
}

/** Player Queries **/
fun Realm.findPlayerRefById(playerId:String?): PlayerRef? {
    return this.where(PlayerRef::class.java).equalTo("playerId", playerId).findFirst()
}
/** TryOut Queries **/
fun Realm.findTryOutById(tryoutId:String?): TryOut? {
    return this.where(TryOut::class.java).equalTo("id", tryoutId).findFirst()
}

/** Roster Queries **/
fun Realm.findRosterById(rosterId:String?): Roster? {
    return this.where(Roster::class.java).equalTo("id", rosterId).findFirst()
}
fun Realm.findRosterIdByTeamId(teamId:String?): String? {
    return this.findTeamById(teamId)?.rosterId
}
fun Realm.findTryOutIdByTeamId(teamId:String?): String? {
    return this.findTeamById(teamId)?.tryoutId
}
inline fun Realm.findTryOutIdByTeamId(teamId:String?, crossinline block: (String) -> Unit?) {
    val tryoutId = this.findTeamById(teamId)?.tryoutId
    if (tryoutId.isNotNBE()) {
        block(tryoutId!!)
    }
}
fun Realm.findPlayersInRosterById(rosterId:String?): RealmList<PlayerRef>? {
    return this.findRosterById(rosterId)?.players
}

/** Sports Queries **/
fun Realm.findAllSports(): RealmResults<Sport>? {
    return this.where(Sport::class.java).findAll()
}
fun Realm.findSportByName(name:String): Sport? {
    return this.where(Sport::class.java).equalTo("name", name).findFirst()
}