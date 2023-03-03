package io.usys.report.firebase.fireludi

import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.fairAddListenerForSingleValueEvent
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.toRealmObjects
import io.usys.report.realm.model.Note
import io.usys.report.utils.log

/**
 * Notes
 */
fun fireGetTeamNotesInBackground(teamId:String?) {
    if (teamId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).orderByChild("aboutTeamId").equalTo(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<Note>()
                log("Notes Updated")
            }
    }
}

fun fireGetPlayerNotesInBackground(playerId:String?) {
    if (playerId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).orderByChild("playerId").equalTo(playerId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toRealmObjects<Note>()
                log("Notes Updated")
            }
    }
}