package io.usys.report.firebase.models

import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmChangeListener
import io.usys.report.firebase.coreFirebaseUserUid
import io.usys.report.firebase.getBoolean
import io.usys.report.firebase.getString
import io.usys.report.realm.model.users.Coach
import io.usys.report.realm.model.users.getUserId
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.safeWrite
import io.usys.report.utils.log

fun DataSnapshot.toRealmCoach(realm: Realm): Coach {
    val coach = Coach()

    coach.id = this.getString("id") ?: realm.getUserId() ?: coreFirebaseUserUid() ?: "unassigned"
    coach.userId = this.getString("userId")
    coach.name = this.getString("name") ?: "unassigned"
    coach.title = this.getString("title")

    this.child("organizationIds").value?.let { organizationIds ->
        val tempList = (organizationIds as? ArrayList<*>)
        tempList?.forEach { coach.organizationIds?.add(it as? String) }
    }

    this.child("teams").value?.let { teams ->
        val tempList = (teams as? ArrayList<*>)
        tempList?.forEach {
            when (it) {
                is HashMap<*, *> -> {
                    val t = it as? HashMap<String, Any>
                    val tid = t?.get("teamId") as? String
                    if (tid != null) { coach.teams?.add(tid) }
                }
                is String -> { coach.teams?.add(it) }
            }
        }
    }

    coach.hasReview = this.child("hasReview").getValue(Boolean::class.java) ?: false

    this.child("reviewIds").value?.let { reviewIds ->
        val tempList = (reviewIds as? ArrayList<*>)
        tempList?.forEach {
            coach.reviewIds?.add(it as? String)
        }
    }

    coach.dateCreated = this.getString("dateCreated")
    coach.dateUpdated = this.getString("dateUpdated")
    coach.firstName = this.getString("firstName")
    coach.lastName = this.getString("lastName")
    coach.type = this.getString("type")
    coach.subType = this.getString("subType")
    coach.details = this.getString("details")
    coach.isFree = this.getBoolean("isFree") ?: true
    coach.status = this.getString("status")
    coach.mode = this.getString("mode")
    coach.imgUrl = this.getString("imgUrl")
    coach.sport = this.getString("sport")

    realm.safeWrite { realm.insertOrUpdate(coach) }
    return coach
}



class CoachRealmSingleEventListener(private val onRealmChange: () -> Unit) : RealmChangeListener<Coach> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var coachResult: Coach
    private var coachId: String = "unassigned"

    init {
        realm.safeUserId { coachId = it }
        registerListener()
    }

    override fun onChange(t: Coach) {
        log("Coach listener called")
        onRealmChange()
    }

    fun registerListener() {
        coachResult = realm.where(Coach::class.java).equalTo("id", coachId).findFirstAsync()
        coachResult.addChangeListener(this)
    }

    fun unregisterListener() {
        coachResult.removeChangeListener(this)
        realm.close()
    }
}
