package io.usys.report.realm

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.utils.main
import kotlinx.coroutines.launch

fun realm() : Realm {
    return Realm.getDefaultInstance()
}

fun realmIsInTransaction() : Boolean {
    return realm().isInTransaction
}

inline fun <reified T: RealmObject> createRealmObject(id:String): T {
    return realm().createObject(T::class.java, id)
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    if (realmIsInTransaction()) {
        realm().executeTransactionAsync { block(it) }
    } else {
        realm().executeTransaction { block(it) }
    }
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealmAsync(crossinline block: (Realm) -> Unit) {
    realm().executeTransactionAsync { block(it) }
}

inline fun executeRealmOnRealmThread(crossinline block: (Realm) -> Unit) {
    AuthControllerActivity.realmThread.launch {
        realm().executeTransaction { block(it) }
    }
}

inline fun executeRealmOnMain(crossinline block: (Realm) -> Unit) {
    main {
        realm().executeTransaction { block(it) }
    }
}

fun addUpdateRealmObject(realmObject: RealmModel){
    executeRealm { itRealm ->
        itRealm.insertOrUpdate(realmObject)
    }
}