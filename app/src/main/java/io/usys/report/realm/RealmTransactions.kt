package io.usys.report.realm

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.main

/**
 * Realm Rules
    * 1. Read like normal.
    * 2. Write under a ExecuteTransaction Function
 */
fun realm() : Realm {
    return Realm.getDefaultInstance()
}
//This works.
inline fun <reified T: RealmObject> Realm.findByField(field:String="id", value: String): T? {
    return this.where(T::class.java).equalTo(field, value).findFirst()
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
fun Realm.getRosterIdForTeamId(teamId:String): String? {
    return this.findTeamById(teamId)?.rosterId
}
fun Realm.getPlayersForRosterId(rosterId:String): RealmList<PlayerRef>? {
    return this.findRosterById(rosterId)?.players
}

inline fun writeToRealmOnMain(crossinline block: (Realm) -> Unit) {
    main {
        realm().executeTransaction { block(it) }
    }
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun writeToRealm(crossinline block: (Realm) -> Unit) {
    val realm = realm()
    if (realm.isInTransaction) {
        realm.executeTransactionAsync { block(it) }
    } else {
        realm.executeTransaction { block(it) }
    }
}
