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
