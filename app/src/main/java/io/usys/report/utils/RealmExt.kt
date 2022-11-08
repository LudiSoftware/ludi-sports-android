package io.usys.report.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import io.usys.report.model.*
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.firebase.forceGetNameOfRealmObject

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


//fun HashMap<*,*>.toJsonRealmList(): RealmList<Any> {
//    var resultList: RealmList<Any> = RealmList()
//    for ((_,v) in this) {
//        val test = (v as? HashMap<*,*>)?.toJSON()
//        resultList.add(test)
//    }
//    return resultList
//}

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

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    realm().executeTransaction { block(it) }
}

// in progress
inline fun <T : RealmModel> T.applyAndFireSave(block: (T) -> Unit) {
    this.apply {
        block(this)
    }
    this.cast<T>()?.let { itObj ->
        this.getRealmId<T>()?.let { itId ->
            fireAddUpdateDBAsync(itObj.forceGetNameOfRealmObject(), itId, itObj)
        }
    }
}

fun <T> RealmModel.getRealmId() : String? {
    val id = this.getAttribute<T>("id")
    if (id.isNullOrEmpty()) { return null }
    return id.toString()
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

inline fun sessionOrganizationList(block: (RealmList<Organization>) -> Unit) {
    Session.session?.organizations?.let { block(it) }
}


