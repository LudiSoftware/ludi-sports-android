package io.usys.report.firebase

import com.google.firebase.database.*
import io.realm.RealmList

/**
 * Created by ChazzCoin : October 2022.
 */

/** Get List of ALL Base Objects */
inline fun <reified T> fireGetBaseLudiObjects(dbName:String, crossinline block: RealmList<T>?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).fairAddParsedListenerForSingleValueEvent<T> { realmList ->
            block(realmList)
        }
    }
}

/** Get One Single Value by Single Attribute */
inline fun fireGetSingleValueAsync(collection:String, objId: String, singleAttribute: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(collection).child(objId).child(singleAttribute)
            .singleValueEvent { ds ->
                block(ds)
            }
    }
}

/** Get List by Single Attribute AsyncBlock */
inline fun fireGetOrderByEqualToAsync(dbName:String, orderBy: String, equalTo: String, crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .singleValueEvent { ds ->
                block(ds)
            }
    }
}

/** Get List by Single Attribute with Callback */
fun fireGetOrderByEqualToCallback(dbName:String, orderBy: String, equalTo: String,
                                  callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .singleValueEventCallBack(callbackFunction)
    }
}

/** Get List by Single Attribute with Callback */
fun fireGetOrderByEqualToCallback(collection:String, childOwnerId:String, orderBy: String, equalTo: String,
                                  callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebaseDatabase(collection) {
        it.child(childOwnerId).orderByChild(orderBy).equalTo(equalTo)
            .singleValueEventCallBack(callbackFunction)
    }
}















