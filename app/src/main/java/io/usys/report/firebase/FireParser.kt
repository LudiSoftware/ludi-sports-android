package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.usys.report.realm.executeRealm
import io.usys.report.utils.tryCatch

fun DataSnapshot.toHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    this.children.forEach {
        tryCatch {
            it.key?.let { key ->
                hashMap[key] = it.value ?: ""
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

/**
 * 2. Master Parsing Function Part 2
 *    - From HashMap<String,Any> to Realm Object
 */
fun HashMap<String, Any>.mapHashMapToRealmObject(): Any? {
    val realm = Realm.getDefaultInstance()
    val mediator = realm.configuration.realmObjectClasses
    var result: Any? = null
    try {
        // Loop through all classes in the schema
        for (clazz in mediator) {
            val schema = realm.schema.get(clazz.simpleName)
            if (schema != null && clazz.isAnnotationPresent(RealmClass::class.java)) {
                try {
                    val realmFieldNames = schema.fieldNames
                    val checker = hashMapKeysMatch(realmFieldNames.toList(), this)
                    if (checker) {
                        val jsonString = Gson().toJson(this)
                        executeRealm {
                            result = realm.createOrUpdateObjectFromJson(clazz, jsonString)
                        }
                    }
                    if (result != null) {
                        return result
                    }
                    continue
                } catch (e: Exception) {
                    // Log the error and continue to the next class
                    e.printStackTrace()
                }
            }
        }
    } catch (e: Exception) {
        // Log the error here
        e.printStackTrace()
    }
    return null
}


//This does work.
fun HashMap<String, Any>.mapHashMap(realmObj: RealmObject?): RealmObject? {
    tryCatch {
        for ((key, value) in this) {
            val field = realmObj?.javaClass?.superclass?.getDeclaredField(key)
            if (field.toString() == "id") {
                continue
            }
            field?.isAccessible = true
            val fieldType = field?.type
            val fieldValue = when (fieldType) {
                String::class.java -> value.toString()
                Int::class.java -> (value as? Long)?.toInt() ?: 0
                Long::class.java -> value as? Long ?: 0L
                Float::class.java -> (value as? Double)?.toFloat() ?: 0f
                Double::class.java -> value as? Double ?: 0.0
                Boolean::class.java -> value as? Boolean ?: false
                RealmList::class.java -> {
                    val arrayList: ArrayList<*>? = value as? ArrayList<*>
                    arrayList?.let {
                        val realmList: RealmList<Any> = RealmList()
                        for (item in it) {
                            if (item is HashMap<*, *>) {
                                val temp = (item as? HashMap<String, Any>)?.mapHashMapToRealmObject()
                                temp?.let { temp ->
                                    realmList.add(temp)
                                }
                            }
                            if (item is String) {
                                item?.let { item ->
                                    realmList.add(item)
                                }
                            }
                        }
                        realmList
                    }
                }
                else -> realmObj
            }
            if (fieldValue != null) {
                field?.set(realmObj, fieldValue)
            }
        }
    }
    return realmObj
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