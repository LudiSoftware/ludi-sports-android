package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable


open class Schedule : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = "null"
    var startDate: String? = "null"
    var endDate: String? = "null"
    var eventIds: RealmList<String>? = null
    var ownerId: String? = "null"
    var assignedUserIds: RealmList<String>? = null
}
open class Event : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var eventName: String? = "null"
    var startTime: String? = "null"
    var endTime: String? = "null"
    var startDate: String? = "null"
    var endDate: String? = "null"
    var day: String? = "null"
    var isRecurring: Boolean? = false
    var locationId: String? = null
    var field: String? = "null"
    var ownerId: String? = "null"
    var assignedUserIds: RealmList<String>? = null
}






