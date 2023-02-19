package io.usys.report.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.gson.Gson
import io.realm.*
import io.realm.annotations.RealmClass
import io.usys.report.realm.executeRealm
import io.usys.report.realm.model.getClassName
import io.usys.report.realm.realm
import io.usys.report.realm.toRealmList
import io.usys.report.utils.cast
import io.usys.report.utils.tryCatch


// -> Well this is legit.
fun DataSnapshot?.toObjectRealm(): RealmObject? {
    this?.let {
        var temp: RealmObject? = null
        val hashmap = it.toHashMap()
        temp = hashmap.mapHashMapToRealmObject() as? RealmObject
        return temp
    }
    return null
}

inline fun <reified T : Any> DataSnapshot?.toObject(): T? {
    this?.let {
        val hashmap = it.toHashMap()
        val temp = hashmap.toObject<T>()
        return temp
    }
    return null
}

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

fun DataSnapshot.toHashMapWithRealmLists(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    this.children.forEach { child ->
        val key = child.key!!
        val value = child.value
        when (value) {
            is ArrayList<*> -> {
                val arrayList: ArrayList<*> = value
                hashMap[key] = arrayList.toRealmList()
            }
            else -> { hashMap[key] = value!! }
        }
    }
    return hashMap
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

// MASTER FUNCTION FOR PARSING TO REALM OBJECTS
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
                        var primaryField: String? = null
                        tryCatch {
                            primaryField = schema.primaryKey
                        }
                        val _id = this[primaryField ?: "id"]
                        // query realm for item with id first
                        val query = realm().where(clazz).equalTo(primaryField, _id.toString()).findFirst()
                        if (query != null) {
                            result = query
                        } else {
                            val jsonString = Gson().toJson(this)
                            executeRealm {
                                result = realm.createObjectFromJson(clazz, jsonString)
                            }
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



fun hashMapKeysMatch(hashMap1: HashMap<String, Any>, hashMap2: HashMap<String, Any>): Boolean {
    for (key in hashMap1.keys) {
        if (!hashMap2.containsKey(key)) {
            return false
        }
    }
    return true
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



// Verified
inline fun firebaseDatabase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}


fun <TResult> Task<TResult>.fairAddOnCompleteListener(block: (TResult) -> Unit): Task<TResult> {
    this.addOnCompleteListener {
        block(it.result)
    }
    return this
}

fun <TResult> Task<TResult>.fairAddFailureListener(block: (Exception) -> Unit): Task<TResult> {
    this.addOnFailureListener {
        block(it)
    }
    return this
}

// Verified
fun Query.fairAddListenerForSingleValueEvent(callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            callbackFunction?.invoke(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            callbackFunction?.invoke(null)
        }
    })
}

@JvmName("fairAddYsrListenerForSingleValueEvent1")
fun Query.fairAddListenerForSingleValueEvent(block: (DataSnapshot?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}

inline fun <reified T> Query.fairAddParsedListenerForSingleValueEvent(crossinline block: (RealmList<T>?) -> Unit) {
    return this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            block(dataSnapshot.toRealmList())
        }
        override fun onCancelled(databaseError: DatabaseError) {
            block(null)
        }
    })
}

fun <TResult> Task<TResult>.fairAddOnSuccessCallback(callbackFunction: ((Boolean, String) -> Unit)?) {
    this.addOnSuccessListener {
        callbackFunction?.invoke(true, "success")
    }.addOnCompleteListener {
        callbackFunction?.invoke(true, "complete")
    }.addOnFailureListener {
        callbackFunction?.invoke(false, "failure")
    }
}
