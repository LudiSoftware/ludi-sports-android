package io.usys.report.realm

import io.realm.Realm
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
//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun Realm.safeWrite(crossinline block: (Realm) -> Unit) {
    tryCatch {
        if (this.isInTransaction) {
            this.executeTransactionAsync { block(it) }
        } else {
            this.executeTransaction { block(it) }
        }
    }
}

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
