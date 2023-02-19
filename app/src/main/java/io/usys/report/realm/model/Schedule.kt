package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable

open class Event : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = null
    var type: String? = null
    var schedule: Schedule? = null

}

open class Schedule : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var name: String? = null
    var startDate: String? = null
    var startTime: String? = null
    var endDate: String? = null
    var endTime: String? = null
    var location: Location? = null
    var field: String? = null
}


