package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable


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
    var location: Location? = null
    var field: String? = "null"

}

open class Schedule : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = "null"
    var startDate: String? = "null"
    var endDate: String? = "null"
    var monday: Event? = Event()
    var tuesday: Event? = Event()
    var wednesday: Event? = Event()
    var thursday: Event? = Event()
    var friday: Event? = Event()
    var saturday: Event? = Event()
    var sunday: Event? = Event()
}





