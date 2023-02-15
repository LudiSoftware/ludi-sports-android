package io.usys.report.utils

import com.google.firebase.database.DataSnapshot
import io.usys.report.model.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.firebase.fireForceGetNameOfRealmObject
import io.usys.report.ui.AuthControllerActivity
import io.usys.report.ui.AuthControllerActivity.Companion.realmThread
import kotlinx.coroutines.launch

/**
 * Created by ChazzCoin : December 2019.
 */

/** -> TRIED AND TRUE! <- */
fun <T> RealmList<T>?.toMutableList() : MutableList<T> {
    val listOfT = mutableListOf<T>()
    this?.let {
        for (item in it) {
            listOfT.add(item)
        }
    }
    return listOfT
}

fun <K, V> HashMap<K, V>?.toRealmList() : RealmList<Any> {
    val listOfT = RealmList<Any>()
    this?.let {
        for ((_,value) in it) {
            listOfT.add(value as? Any)
        }
    }
    return listOfT
}

fun <T> ArrayList<T>.toRealmList(): RealmList<T> {
    val realmList = RealmList<T>()
    for (item in this) {
        realmList.add(item)
    }
    return realmList
}

inline fun <reified T> DataSnapshot.toRealmList(): RealmList<T> {
    val realmList: RealmList<T> = RealmList()
    for (ds in this.children) {
        val org: T? = ds.getValue(T::class.java)
        org?.let {
            realmList.add(org)
        }
    }
    return realmList
}

fun realm() : Realm {
    return Realm.getDefaultInstance()
}

inline fun <reified T: RealmObject> createRealmObject(id:String): T {
    return realm().createObject(T::class.java, id)
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    realm().executeTransaction { block(it) }
}

inline fun executeRealmOnRealmThread(crossinline block: (Realm) -> Unit) {
    realmThread.launch {
        realm().executeTransaction { block(it) }
    }
}

inline fun executeRealmOnMain(crossinline block: (Realm) -> Unit) {
    main {
        realm().executeTransaction { block(it) }
    }
}

// Verified
inline fun <T : RealmModel> T.applyAndFireSave(block: (T) -> Unit) {
    this.apply {
        block(this)
    }
    this.cast<T>()?.let { itObj ->
        this.getRealmId<T>()?.let { itId ->
            fireAddUpdateDBAsync(itObj.fireForceGetNameOfRealmObject(), itId, itObj)
        }
    }
}

fun <T> RealmModel.getRealmId() : String? {
    val id = this.getAttribute<T>("id")
    if (id.isNullOrEmpty()) { return null }
    return id.toString()
}

fun addUpdateRealmObject(realmObject: RealmModel){
    executeRealm { itRealm ->
        itRealm.insertOrUpdate(realmObject)
    }
}

// Untested
fun <T> DataSnapshot.toClass(clazz: Class<T>): T? {
    return this.getValue(clazz)
}

fun sessionAndUser(block: (Session, User) -> Unit) {
    session { itSession ->
        userOrLogout { itUser ->
            block(itSession, itUser)
        }
    }
}

inline fun session(block: (Session) -> Unit) {
    Session.session?.let { block(it) }
}

inline fun sessionAndSave(crossinline block: (Session) -> Session) {
    executeRealm { itRealm ->
        Session.session?.let { itRealm.insertOrUpdate(block(it))}
    }
}

fun Session.saveToRealm() {
    executeRealm {
        it.insertOrUpdate(this)
    }
}

inline fun executeSession(crossinline block: (Session?) -> Unit) {
    main {
        realm().executeTransaction {
            Session.session?.let { itSession ->
                block(itSession)
                it.insertOrUpdate(itSession)
            }
        }
    }
}

inline fun sessionServices(block: (RealmList<Service>) -> Unit) {
    Session.session?.services?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionSports(block: (RealmList<Sport>) -> Unit) {
    Session.session?.sports?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionTeams(block: (RealmList<Team>) -> Unit) {
    Session.session?.teams?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionPlayers(block: (RealmList<Player>) -> Unit) {
    Session.session?.players?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionOrganizationList(block: (RealmList<Organization>) -> Unit) {
    Session.session?.organizations?.let { block(it) }
}


