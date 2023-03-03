package io.usys.report.realm

import com.google.firebase.database.DataSnapshot
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
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

inline fun <reified T:RealmObject> HashMap<String, Any>.toRealmObject(): T? {
    val jsonString = Gson().toJson(this)
    var result: Any? = null
    writeToRealm {
        result = it.createOrUpdateObjectFromJson(T::class.java, jsonString)
    }
    return result as? T
}

/**
 * 2. Master Parsing Function Part 2
 *    - From HashMap<String,Any> to Realm Object
 */
fun HashMap<String, Any>.mapHashMapToRealmObject(): Any? {
    if (this.isEmpty()) return null
    val realm = Realm.getDefaultInstance()
    var result: Any? = null
    val mediator = realm.configuration.realmObjectClasses

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
                        writeToRealm {
                            result = it.createOrUpdateObjectFromJson(clazz, jsonString)
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
fun RealmObject.toHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    val fieldNames = realm.schema.get(this.javaClass.simpleName)?.fieldNames

    fieldNames?.forEach { fieldName ->
        val field = this.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true

        when (val value = field.get(this)) {
            is RealmList<*> -> {
                val arrayList = ArrayList<HashMap<String, Any>>()

                value.forEach { element ->
                    if (element is RealmObject) {
                        arrayList.add(element.toHashMap())
                    }
                }

                hashMap[fieldName] = arrayList
            }
            is RealmObject -> {
                hashMap[fieldName] = value.toHashMap()
            }
            else -> {
                hashMap[fieldName] = value ?: ""
            }
        }
    }

    return hashMap
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