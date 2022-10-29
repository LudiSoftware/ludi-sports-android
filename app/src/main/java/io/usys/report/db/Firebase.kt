package io.usys.report.db

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.firebase
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : December 2019.
 */

class FireDB {

    companion object {
        const val SPOT_MONTH_DB = "MMMyyyy"
        const val SPOT_DATE_FORMAT = "yyyy-MM-d"
        const val DATE_MONTH = "MMMM"
        const val FIRE_DATE_FORMAT = "EEE, MMM d yyyy, hh:mm:ss a"
        //ADMIN
        const val ADMIN: String = "admin"
        const val SYSTEM: String = "system"
        // -> Main Database Structure
        const val USERS: String = "users"
        const val SPORTS: String = "sports"
        const val ORGANIZATIONS: String = "organizations"
        const val COACHES: String = "coaches"
        const val REVIEWS: String = "reviews"
        const val SERVICES: String = "services"


        /**
         * organizations - organization(id)
         * users - user(id)
         * reviews - review(id)
         *
         */
    }
}

class FireTypes {
    companion object {
        const val USERS: String = "users"
        const val ORGANIZATIONS: String = "organizations"
        const val REVIEWS: String = "reviews"
        const val SPORTS: String = "sports"
        const val COACHES: String = "coaches"

        fun getLayout(type: String): Int {
            when (type) {
                ORGANIZATIONS -> return R.layout.item_list_organization
                SPORTS -> return R.layout.item_list_sports_two
                REVIEWS -> return R.layout.item_list_sports_two
                USERS -> return R.layout.item_list_sports_two
                COACHES -> return R.layout.item_list_sports_two
                else -> return R.layout.item_list_sports_two
            }
        }
    }
}


fun getFirebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

fun Any?.getNameForRealmObject(): String? {
    this?.let {
        when (it) {
            is User -> {
                return FireTypes.USERS
            }
            is Sport -> {
                return FireTypes.SPORTS
            }
            is Organization -> {
                return FireTypes.ORGANIZATIONS
            }
            is Coach -> {
                return FireTypes.COACHES
            }
            else -> {
                return null
            }
        }
    }
    return null
}

fun DataSnapshot.loadIntoSession(type: String) {
    when (type) {
        FireTypes.ORGANIZATIONS -> {
            for (ds in this.children) {
                val org: Organization? = ds.getValue(Organization::class.java)
                org?.let {
                    Session.addOrganization(it)
                }
            }
        }
        FireTypes.SPORTS -> {
            for (ds in this.children) {
                val sport: Sport? = ds.getValue(Sport::class.java)
                sport?.let {
                    Session.addSport(it)
                }
            }
        }
    }
}


// Verified.
fun loadSportsIntoSession() {
    firebase {
        it.child(FireDB.SPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.loadIntoSession(FireTypes.SPORTS)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
}

// unverified
fun <T> T.addUpdateInFirebase(id: String, callbackFunction: ((Boolean, String) -> Unit)?) {
    val collection = this.getNameForRealmObject() ?: return
    firebase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .addYsrOnSuccessListener(callbackFunction)
    }
}

private fun <TResult> Task<TResult>.addYsrOnSuccessListener(callbackFunction: ((Boolean, String) -> Unit)?) {
    this.addOnSuccessListener {
        callbackFunction?.invoke(true, "success")
    }.addOnCompleteListener {
        callbackFunction?.invoke(true, "complete")
    }.addOnFailureListener {
        callbackFunction?.invoke(false, "failure")
    }
}


// Verified
fun addUpdateDB(collection: String, id: String, obj: Any): Boolean {
    var result = false
    firebase { database ->
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

fun getOrderByEqualTo(dbName:String, orderBy: String, equalTo: String,
                      callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    firebase {
        it.child(dbName).orderByChild(orderBy).equalTo(equalTo)
            .addYsrListenerForSingleValueEvent(callbackFunction)
    }
}

fun getCoachesByOrg(orgId:String, callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    getOrderByEqualTo(FireDB.COACHES, "organizationId", orgId, callbackFunction)
}

fun Query.addYsrListenerForSingleValueEvent(callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)?) {
    this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            callbackFunction?.invoke(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            callbackFunction?.invoke(null)
        }
    })
}
