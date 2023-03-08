package io.usys.report.realm.model

import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.*
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.*
import java.io.Serializable

open class Note : RealmObject(), Serializable {
    /**
     * /notes/{ownerId}/{noteId}
     */
    @PrimaryKey
    var id: String = newUUID()
    var ownerId: String? = "null"
    var ownerName: String? = "null"
    var coachId: String? = "null"
    var dateCreated: String = getTimeStamp()
    var dateUpdated: String = getTimeStamp()
    var aboutTeamId: String? = "null"
    var aboutPlayerId: String? = "null"
    var aboutCoachId: String? = "null"
    var sport: String? = "null"
    var type: String? = "null"
    var subtype: String? = "null"
    var message: String? = "null"
}

enum class NoteTypes(val type: String) {
    PLAYER("player"),
    TEAM("team"),
    TRYOUT("tryout"),
    GAME("game"),
    PRACTICE("practice"),
    PRIVATE("private"),
    EVENT("event"),
    ORGANIZATION("organization"),
    COACH("coach"),
    PARENT("parent")
}

fun Realm.getPlayerNotes(playerId:String, onClick: ((DataSnapshot?) -> Unit)?) {
    safeUserId { itUserId ->
        fireGetOrderByEqualToCallback(DatabasePaths.NOTES.path, itUserId, "aboutPlayerId", playerId, onClick)
    }
}
