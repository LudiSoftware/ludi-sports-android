package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.realm.safeWrite
import io.usys.report.realm.writeToRealm
import io.usys.report.utils.tryCatch



// Original Basic Parser to RealmObject
// Still Being used by User Object.
inline fun <reified T : Any> DataSnapshot?.toObject(): T? {
    this?.let {
        val hashmap = it.toHashMap()
        val temp = hashmap.toObject<T>()
        return temp
    }
    return null
}

// -> Well this is legit.
inline fun <reified T> HashMap<*, *>.toObject(): T {
    val obj = T::class.java.newInstance()
    val properties = T::class.java.declaredFields.map { it.name to it }.toMap()

    for ((key, value) in this) {
        if (properties.containsKey(key)) {
            val field = properties[key]
            field?.isAccessible = true
            val fieldType = field?.type
            if (fieldType != null) {
                val fieldValue = when (fieldType) {
                    String::class.java -> value.toString()
                    Int::class.java -> (value as? Long)?.toInt() ?: 0
                    Long::class.java -> value as? Long ?: 0L
                    Float::class.java -> (value as? Double)?.toFloat() ?: 0f
                    Double::class.java -> value as? Double ?: 0.0
                    Boolean::class.java -> value as? Boolean ?: false
                    else -> null
                }
                field.set(obj, fieldValue)
            }
        }
    }
    return obj
}

/**
 * 1. Master Parsing Function Part 1
 *    - From Firebase Object to Realm Object
 */

inline fun <reified T:RealmObject> DataSnapshot?.toLudiObject(): RealmObject? {
    this?.let {
        var temp: RealmObject? = null
        val hashmap = it.toHashMap()
        temp = hashmap.toRealmObject<T>()
        return temp
    }
    return null
}

inline fun <reified T:RealmObject> DataSnapshot?.toLudiObject(realm: Realm): RealmObject? {
    this?.let {
        var temp: RealmObject? = null
        val hashmap = it.toHashMap()
        temp = hashmap.toRealmObject<T>(realm)
        return temp
    }
    return null
}

/**
 * The Master List Of Firebase Objects Parser
 */
inline fun <reified T:RealmObject> DataSnapshot?.toLudiObjects(): RealmList<T>? {
    this?.let {
        val realmList = RealmList<T>()
        val hashmap = it.toHashMap()
        val hashSize = hashmap.size
        var temp: T?
        if (hashSize > 0) {
            for ((_,value) in hashmap) {
                if (value is HashMap<*,*>) {
                    val tempHash = value as HashMap<String, Any>
                    temp = tempHash.toRealmObject<T>()
                    temp?.let { itTemp-> realmList.add(itTemp) }
                }
            }
        } else {
            return null
        }
        return realmList
    }
    return null
}

/**
 * Master toHashMap Helper Function
 */
fun DataSnapshot.toHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    this.children.forEach {
        tryCatch {
            it.key?.let { key ->
                var newValue = it.value
                if (it.value is Long) {
                    newValue = (it.value as Long).toInt()
                }
                if (it.value is ArrayList<*>) {
                    val list = it.value as ArrayList<*>
                    val realmList = RealmList<Any>()
                    list.forEach { item ->
                        if (item != null) {
                            realmList.add(item)
                        }
                    }
                    newValue = realmList
                }
                hashMap[key] = newValue ?: ""
            }
        }
    }
    return hashMap
}
/** Master Helpers **/
inline fun <reified T:RealmObject> HashMap<String, Any>.toRealmObject(): T? {
    val jsonString = Gson().toJson(this)
    var result: Any? = null
    writeToRealm {
        result = it.createOrUpdateObjectFromJson(T::class.java, jsonString)
    }
    return result as? T
}

inline fun <reified T:RealmObject> HashMap<String, Any>.toRealmObject(realm: Realm): T? {
    val jsonString = Gson().toJson(this)
    var result: Any? = null
    realm.safeWrite {
        result = realm.createOrUpdateObjectFromJson(T::class.java, jsonString)
    }
    return result as? T
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