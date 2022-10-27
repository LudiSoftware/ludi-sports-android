package io.usys.report.db

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.model.Organization
import io.usys.report.model.Session
import io.usys.report.model.Sport
import io.usys.report.model.Spot
import io.usys.report.utils.firebase
import io.usys.report.utils.ioLaunch
import io.usys.report.utils.log
import kotlinx.coroutines.*

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
        const val ORGANIZATIONS: String = "organizations"
        const val REVIEWS: String = "reviews"
        const val SPORTS: String = "sports"

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

fun DataSnapshot.loadIntoRealm(type: String) {
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
fun getSports() {
    firebase {
        it.child(FireDB.SPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.loadIntoRealm(FireTypes.SPORTS)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
}

fun getOrgantionsBySports(sportName: String) {
    firebase {
        it.child(FireDB.ORGANIZATIONS).orderByChild("name").equalTo(sportName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.loadIntoRealm(FireTypes.ORGANIZATIONS)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
}

private val job = SupervisorJob()
private val usysrIODispatcher = CoroutineScope(Dispatchers.IO + job)

fun getOrgsAsync() = usysrIODispatcher.async {
    getOrganizationsSuspended()
}

fun getOrganizationsBlocked(): RealmList<Organization> {
    val orgList: RealmList<Organization> = RealmList()
    runBlocking {
        firebase { it ->
            it.child(FireDB.ORGANIZATIONS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val org: Organization? = ds.getValue(Organization::class.java)
                            org?.let {
                                orgList.add(it)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
        return@runBlocking orgList
    }
    return orgList
}

private suspend fun getOrganizationsSuspended() = withContext(usysrIODispatcher.coroutineContext) {
    val orgList: RealmList<Organization> = RealmList()
    firebase { it ->
        it.child(FireDB.ORGANIZATIONS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val org: Organization? = ds.getValue(Organization::class.java)
                        org?.let {
                            orgList.add(it)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
    return@withContext orgList
}

// unverified
fun <T> T.addUpdateInFirebase(collection: String, id: String): Boolean {
    var result = false
    firebase { database ->
        database.child(collection).child(id)
            .setValue(this)
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