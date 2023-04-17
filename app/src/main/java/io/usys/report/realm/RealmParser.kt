package io.usys.report.realm

import com.google.firebase.database.DataSnapshot
import io.realm.*


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

fun <T:Any> MutableList<T>?.safeAdd(item: T?): Boolean {
    if (this == null) return false
    if (item == null) return false
    if (this.contains(item)) return false
    this.add(item)
    return true
}

fun <T : Any> MutableList<T>?.safeReplace(item: T?): Boolean {
    if (this == null || item == null) return false

    val existingItemIndex = this.indexOfFirst { it == item }

    return if (existingItemIndex != -1) {
        this.removeAt(existingItemIndex)
        this.add(existingItemIndex, item)
        true
    } else {
        this.add(item)
        true
    }
}


fun <T:Any> RealmList<T>.safeAddAll(otherList: Collection<T>) {
    otherList.forEach { this.safeAdd(it) }
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











