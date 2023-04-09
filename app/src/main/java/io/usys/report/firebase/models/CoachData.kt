package io.usys.report.firebase.models

import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.usys.report.firebase.coreFirebaseUserUid
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.users.getUserId
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.log

class CoachData  {

    var id: String = "unassigned"
    var userId: String? = "unassigned"
    var name: String = "unassigned"
    var title: String? = null
    var organizationIds: RealmList<String>? = null
    var teams: List<String>? = null
    var hasReview: Boolean = false
    var reviewIds: RealmList<String>? = null
    // base (12)
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var firstName: String? = "null"
    var lastName: String? = "null"
    var type: String? = "null"
    var subType: String? = "null"
    var details: String? = "null"
    var isFree: Boolean = true
    var status: String? = "null"
    var mode: String? = "null"
    var imgUrl: String? = "null"
    var sport: String? = "null"

}

fun Coach.mapToCoachData(): CoachData {
    val coachData = CoachData()
    coachData.id = this.id
    coachData.userId = this.userId
    coachData.name = this.name
    coachData.title = this.title
    coachData.organizationIds = this.organizationIds
    coachData.teams = this.teams?.toList()
    coachData.hasReview = this.hasReview
    coachData.reviewIds = this.reviewIds
    coachData.dateCreated = this.dateCreated
    coachData.dateUpdated = this.dateUpdated
    coachData.firstName = this.firstName
    coachData.lastName = this.lastName
    coachData.type = this.type
    coachData.subType = this.subType
    coachData.details = this.details
    coachData.isFree = this.isFree
    coachData.status = this.status
    coachData.mode = this.mode
    coachData.imgUrl = this.imgUrl
    coachData.sport = this.sport
    return coachData
}
fun DataSnapshot.toRealmCoach(): Coach {
    val coach = Coach()
    val realm = realm()

    coach.id = this.child("id").getValue(String::class.java) ?: realm.getUserId() ?: coreFirebaseUserUid() ?: "unassigned"
    coach.userId = this.child("userId").getValue(String::class.java)
    coach.name = this.child("name").getValue(String::class.java) ?: "unassigned"
    coach.title = this.child("title").getValue(String::class.java)

    this.child("organizationIds").value?.let { organizationIds ->
        val tempList = (organizationIds as? ArrayList<*>)
        tempList?.forEach {
            coach.organizationIds?.add(it as? String)
        }
    }

    this.child("teams").value?.let { teams ->
        val tempList = (teams as? ArrayList<*>)
        tempList?.forEach {
            when (it) {
                is HashMap<*, *> -> {
                    val t = it as? HashMap<String, Any>
                    val tid = t?.get("teamId") as? String
                    if (tid != null) {
                        coach.teams?.add(tid)
                    }
                }
                is String -> {
                    coach.teams?.add(it)
                }
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

    coach.dateCreated = this.child("dateCreated").getValue(String::class.java)
    coach.dateUpdated = this.child("dateUpdated").getValue(String::class.java)
    coach.firstName = this.child("firstName").getValue(String::class.java)
    coach.lastName = this.child("lastName").getValue(String::class.java)
    coach.type = this.child("type").getValue(String::class.java)
    coach.subType = this.child("subType").getValue(String::class.java)
    coach.details = this.child("details").getValue(String::class.java)
    coach.isFree = this.child("isFree").getValue(Boolean::class.java) ?: true
    coach.status = this.child("status").getValue(String::class.java)
    coach.mode = this.child("mode").getValue(String::class.java)
    coach.imgUrl = this.child("imgUrl").getValue(String::class.java)
    coach.sport = this.child("sport").getValue(String::class.java)

    return coach
}



class CoachRealmSingleEventListener(private val onRealmChange: () -> Unit) : RealmChangeListener<Coach> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var coachResult: Coach
    private var coachId: String = "unassigned"

    init {
        realm.safeUserId { userId ->
            coachId = userId
        }
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
