package io.usys.report.firebase.fireludi

import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.fairAddListenerForSingleValueEvent
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.toLudiObjects
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
                ds?.toLudiObjects<Note>()
                log("Notes Updated")
            }
    }
}

fun fireGetPlayerNotesInBackground(teamId:String?) {
    if (teamId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).orderByChild("aboutPlayerId").equalTo(teamId)
            .fairAddListenerForSingleValueEvent { ds ->
                ds?.toLudiObjects<Note>()
                log("Notes Updated")
            }
    }
}

fun fireAddNote(note:Note) {
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).child(note.id).setValue(note)
    }
}
