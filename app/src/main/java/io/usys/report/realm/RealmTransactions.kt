package io.usys.report.realm

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.main
import kotlinx.coroutines.launch

/**
 * Realm Rules
    * 1. Read like normal.
    * 2. Write under a ExecuteTransaction Function
 */

//This works.
inline fun <reified T: RealmObject> Realm.findByField(field:String="id", value: String): T? {
    return this.where(T::class.java).equalTo(field, value).findFirst()
}

fun realm() : Realm {
    return Realm.getDefaultInstance()
}


//inline fun <reified T: RealmObject> createRealmObject(id:String): T {
//    return realm().createObject(T::class.java, id)
//}

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
