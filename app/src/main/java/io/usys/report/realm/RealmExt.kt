package io.usys.report.realm

import io.realm.*
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.firebase.fireForceGetNameOfRealmObject
import io.usys.report.realm.model.*
import io.usys.report.utils.cast
import io.usys.report.utils.getAttribute
import io.usys.report.utils.isNullOrEmpty

/**
 * Created by ChazzCoin : December 2019.
 */

fun RealmObject.updateFieldsAndSave(newObject: RealmObject, realm: Realm) {
    realm.safeWrite {
        val fields = this.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            val newValue = field.get(newObject)
            field.set(this, newValue)
        }
        it.insertOrUpdate(this)
    }
}

// Verified
inline fun <T : RealmModel> T.applyAndFireSave(block: (T) -> Unit) {
    this.apply {
        block(this)
    }
    this.cast<T>()?.let { itObj ->
        this.getRealmId<T>()?.let { itId ->
            fireAddUpdateDBAsync(itObj.fireForceGetNameOfRealmObject(), itId, itObj as RealmObject)
        }
    }
}
// Verified
fun getRealmSchema(realmType: String): RealmObjectSchema? {
    return realm().schema.get(realmType)
}
// Verified
fun getRealmFields(realmType: String): MutableSet<String>? {
    return getRealmSchema(realmType)?.fieldNames
}
// Verified
fun RealmObject.getRealmId(defaultValue:String?=null) : String? {
    val id = this.getValue("id", defaultValue)
    if (id.isNullOrEmpty()) { return null }
    return id.toString()
}
// Verified
fun <T> RealmModel.getRealmId() : String? {
    val id = this.getAttribute<T>("id")
    if (id.isNullOrEmpty()) { return null }
    return id.toString()
}
// Verified
inline fun <reified R> RealmObject.getValue(fieldName: String, defaultValue: R): R {
    val field = this.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true

    return when (val value = field.get(this)) {
        is R -> value
        else -> defaultValue
    }
}

inline fun session(block: (Session) -> Unit) {
    Session.session?.let { block(it) }
}

inline fun sessionServices(block: (RealmList<Service>) -> Unit) {
    Session.session?.services?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionSports(block: (RealmList<Sport>) -> Unit) {
    Session.session?.sports?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}
inline fun sessionTeams(block: (RealmList<Team>) -> Unit) {
    Session.session?.teams?.let {
        if (!it.isNullOrEmpty()) { block(it) }
    }
}

