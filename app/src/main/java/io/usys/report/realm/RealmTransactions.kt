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
import io.usys.report.utils.tryCatch

/**
 * Realm Rules
    * 1. Read like normal.
    * 2. Write under a ExecuteTransaction Function
 */

object RealmInstance {
    val instance: Realm by lazy {
        Realm.getDefaultInstance()
    }
}
fun realm() : Realm {
    return RealmInstance.instance
}
inline fun writeToRealmOnMain(crossinline block: (Realm) -> Unit) {
    main {
        realm().executeTransaction { block(it) }
    }
}
//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun Realm.safeWrite(crossinline block: (Realm) -> Unit) {
    if (this.isInTransaction) {
        this.executeTransactionAsync { block(it) }
    } else {
        this.executeTransaction { block(it) }
    }
}

////LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun writeToRealm(crossinline block: (Realm) -> Unit) {
    val realm = realm()
    tryCatch {
        if (realm.isInTransaction) {
            realm.executeTransactionAsync { block(it) }
        } else {
            realm.executeTransaction { block(it) }
        }
    }
}
