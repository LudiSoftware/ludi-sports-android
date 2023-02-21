package io.usys.report.realm.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable


open class Schedule : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = null
    var weeklyEvents: RealmList<WeeklyEvent>? = null
}

open class WeeklyEvent : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = null
    var type: String? = null
    var monday: DayEvent? = null
    var tuesday: DayEvent? = null
    var wednesday: DayEvent? = null
    var thursday: DayEvent? = null
    var friday: DayEvent? = null
    var saturday: DayEvent? = null
    var sunday: DayEvent? = null
}

open class DayEvent : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var type: String? = null
    var name: String? = "Practice"
    var startTime: String? = "5:45pm"
    var endTime: String? = "7:15pm"
    var location: Location? = null
    var field: String? = "1B"
}




