package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.realm.mapHashMapToRealmObject
import io.usys.report.realm.toObject
import io.usys.report.utils.tryCatch

fun DataSnapshot.toHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    this.children.forEach {
        tryCatch {
            it.key?.let { key ->
                var newValue = it.value
                if (it.value is Long) {
                    newValue = (it.value as Long).toInt()
                }
                hashMap[key] = newValue ?: ""
            }
        }
    }
    return hashMap
}

// Original Basic Parser to RealmObject
inline fun <reified T : Any> DataSnapshot?.toObject(): T? {
    this?.let {
        val hashmap = it.toHashMap()
        val temp = hashmap.toObject<T>()
        return temp
    }
    return null
}

/**
 * 1. Master Parsing Function Part 1
 *    - From Firebase Object to Realm Object
 */
fun DataSnapshot?.toRealmObject(): RealmObject? {
    this?.let {
        var temp: RealmObject? = null
        val hashmap = it.toHashMap()
        temp = hashmap.mapHashMapToRealmObject() as? RealmObject
        return temp
    }
    return null
}
fun DataSnapshot?.toRealmObjects(): RealmList<RealmObject>? {
    this?.let {
        val realmList = RealmList<RealmObject>()
        val hashmap = it.toHashMap()
        val hashSize = hashmap.size
        var temp: RealmObject?
        if (hashSize > 1) {
            for ((_,value) in hashmap) {
                if (value is HashMap<*,*>) {
                    val tempHash = value as HashMap<String, Any>
                    temp = tempHash.mapHashMapToRealmObject() as? RealmObject
                    temp?.let { itTemp-> realmList.add(itTemp) }
                }
            }
        } else {
            temp = hashmap.mapHashMapToRealmObject() as? RealmObject
            temp?.let { itTemp-> realmList.add(itTemp) }
        }
        return realmList
    }
    return null
}

// all keys in hashmap HAVE to appear in keys
fun hashMapKeysMatch(keys: List<String>, hashMap2: HashMap<String, Any>): Boolean {
    tryCatch {
        for (key in hashMap2.keys) {
            if (!keys.contains(key)) {
                return false
            }
        }
        return true
    }
    return false
}