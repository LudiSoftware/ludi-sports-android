package io.usys.report.realm

import io.realm.RealmResults
import io.usys.report.realm.model.Roster

fun RealmResults<Roster>?.findById(rosterId: String): Roster? {
    return this?.find { it.id == rosterId }
}