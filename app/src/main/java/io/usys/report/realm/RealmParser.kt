package io.usys.report.realm

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import io.realm.*
import io.realm.annotations.RealmClass
import io.usys.report.firebase.hashMapKeysMatch
import io.usys.report.utils.tryCatch


fun <T:Any> RealmResults<T>?.toSafeRealmList(): RealmList<T>? {
    if (this == null) return null
    val realmList = RealmList<T>()
    for (item in this) {
        realmList.safeAdd(item)
    }
    return realmList
}

fun <T:Any> RealmList<T>.safeAdd(item: T?): Boolean {
    if (item == null) return false
    if (this.contains(item)) return false
    this.add(item)
    return true
}

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











