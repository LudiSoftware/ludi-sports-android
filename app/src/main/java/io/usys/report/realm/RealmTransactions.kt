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
fun realm() : Realm {
    return Realm.getDefaultInstance()
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
fun Realm.safeTransaction(transaction: (realm: Realm) -> Unit) {
    try {
        this.safeWrite {
            transaction(it)
        }
    } catch (e: Exception) {
        cancelTransaction()
        throw e
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

inline fun Realm.safeCommit(crossinline block: (Realm) -> Unit) {
    tryCatch {
        if (this.isInTransaction) {
            this.commitTransaction()
            this.executeTransactionAsync { block(it) }
        } else {
            this.executeTransaction { block(it) }

        }
    }
}