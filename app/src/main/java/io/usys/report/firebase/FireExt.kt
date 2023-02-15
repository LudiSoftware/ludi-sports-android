package io.usys.report.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.realm.toRealmList
import java.lang.Exception

// -> Well this is legit.
inline fun <reified T : Any> DataSnapshot?.toObject(): T? {
    this?.let {
        val hashmap = it.toHashMap()
        return hashmap.toObject()
    }
    return null
}

fun DataSnapshot.toHashMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    this.children.forEach {
        hashMap[it.key!!] = it.value!!
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
inline fun <reified T : Any> HashMap<String, Any>.toObject(): T {
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

//fun DataSnapshot.toHashMapWithRealmLists(): HashMap<String, Any> {
//    val hashMap = HashMap<String, Any>()
//    this.children.forEach { child ->
//        val key = child.key!!
//        val value = child.value
//        when (value) {
//            is RealmObject -> {
//                val realmObject = value
//                hashMap[key] = value
//            }
//            else -> { hashMap[key] = value!! }
//        }
//    }
//    return hashMap
//}

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
