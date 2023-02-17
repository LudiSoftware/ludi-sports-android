package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

open class Note : RealmObject(), Serializable {
    /**
     * /notes/{ownerId}/{noteId}
     */
    @PrimaryKey
    var id: String = newUUID()
    var ownerId: String? = null
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var coachId: String? = null
    var aboutTeamId: String? = null
    var aboutPlayerId: String? = null
    var message: String? = null
}