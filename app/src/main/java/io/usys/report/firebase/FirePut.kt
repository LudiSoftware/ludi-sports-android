package io.usys.report.firebase

import io.realm.RealmObject

/**
 * Created by ChazzCoin : October 2022.
 */


/** ADD/UPDATE **/
// unverified
fun <T> T.fireAddUpdateInFirebase(id: String, callbackFunction: ((Boolean, String) -> Unit)?) {
    val collection = this.fireGetNameOfRealmObject() ?: return
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .fairAddOnSuccessCallback(callbackFunction)
    }
}


// Verified
fun fireAddUpdateDBAsync(collection: String, id: String, obj: RealmObject): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}
fun fireAddUpdateDBAsync(collection: String, id: String, obj: HashMap<String, Any>): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection).child(id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}
// Verified
fun fireUpdateSingleValueDBAsync(collection: String, id: String, single_attribute: String, single_value: String): Boolean {
    var result = false
    firebaseDatabase { database ->
        database.child(collection)
            .child(id)
            .child(single_attribute)
            .setValue(single_value)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}




