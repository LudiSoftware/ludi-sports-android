package io.usys.report.utils

import android.app.Activity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.usys.report.model.*
import io.realm.Realm
import io.realm.RealmList

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

fun HashMap<*,*>.toJsonRealmList(): RealmList<Any> {
    var resultList: RealmList<Any> = RealmList()
    for ((_,v) in this) {
        val test = (v as? HashMap<*,*>)?.toJSON()
        resultList.add(test)
    }
    return resultList
}

fun realm() : Realm {
    return Realm.getDefaultInstance()
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    realm().executeTransaction { block(it) }
}

// Verified
inline fun firebase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}

// in progress
//inline fun <T> T.applyAndSave(block: T.() -> Unit): T {
//    this.apply {
//        block()
//    }
//    addUpdateDB(FireDB.REVIEWS, rev.id!!, this)
//}

// Untested
fun <T> DataSnapshot.toClass(clazz: Class<T>): T? {
    return this.getValue(clazz)
}


inline fun session(block: (Session) -> Unit) {
    Session.session?.let { block(it) }
}

inline fun userOrLogout(activity: Activity? = null, block: (User) -> Unit) {
    Session.user?.let { block(it) } ?: run { activity?.let { Session.restartApplication(it) } }
}

fun sessionAndUser(block: (Session, User) -> Unit) {
    session { itSession ->
        userOrLogout { itUser ->
            block(itSession, itUser)
        }
    }
}



inline fun locations(block: (RealmList<Organization>) -> Unit) {
    Session.session?.organizations?.let { block(it) }
}


