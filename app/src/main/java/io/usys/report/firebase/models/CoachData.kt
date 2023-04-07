package io.usys.report.firebase.models

import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.usys.report.firebase.toObject
import io.usys.report.firebase.toRealmObject
import io.usys.report.realm.model.Coach
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.utils.getTimeStamp

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

    coach.id = this.child("id").getValue(String::class.java) ?: "unassigned"
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
//fun parseCoachFromHashMap(map: HashMap<String, Any>): Coach {
//    val coach = Coach()
//
//    coach.id = (map["id"] as? String) ?: "unassigned"
//    coach.userId = map["userId"] as? String
//    coach.name = (map["name"] as? String) ?: "unassigned"
//    coach.title = map["title"] as? String
//    val organizationIds = map["organizationIds"] as? List<String>
//    coach.organizationIds = if (organizationIds != null) RealmList(*organizationIds.toTypedArray()) else null
//    val teams = map["teams"] as? List<TeamRef>
//    coach.teams = if (teams != null) RealmList(*teams.toTypedArray()) else null
//    coach.hasReview = (map["hasReview"] as? Boolean) ?: false
//    val reviewIds = map["reviewIds"] as? List<String>
//    coach.reviewIds = if (reviewIds != null) RealmList(*reviewIds.toTypedArray()) else null
//    coach.dateCreated = map["dateCreated"] as? String
//    coach.dateUpdated = map["dateUpdated"] as? String
//    coach.firstName = map["firstName"] as? String
//    coach.lastName = map["lastName"] as? String
//    coach.type = map["type"] as? String
//    coach.subType = map["subType"] as? String
//    coach.details = map["details"] as? String
//    coach.isFree = (map["isFree"] as? Boolean) ?: true
//    coach.status = map["status"] as? String
//    coach.mode = map["mode"] as? String
//    coach.imgUrl = map["imgUrl"] as? String
//    coach.sport = map["sport"] as? String
//
//    return coach
//}
