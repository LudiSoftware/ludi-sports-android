package io.usys.report.firebase.fireludi

import io.realm.Realm
import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.singleValueEvent
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.toLudiObjects
import io.usys.report.realm.model.Note
import io.usys.report.utils.log


fun doubleId(ownerId:String, aboutId:String):String {
    return "${ownerId}:${aboutId}"
}

fun parseDoubleId(combinedId: String): Pair<String, String> {
    val ids = combinedId.split(":")
    if (ids.size != 2) {
        throw IllegalArgumentException("Invalid input format. Expected 'idOne:idTwo'.")
    }
    return Pair(ids[0], ids[1])
}


/**
 * Notes
 */
fun Realm.fireGetNotesByDoubleId(ownerId:String?, aboutId:String?) {
    if (ownerId == null || aboutId == null) return
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).orderByChild("ownerId").equalTo(doubleId(ownerId, aboutId))
            .singleValueEvent { ds ->
                ds?.toLudiObjects<Note>(this)
                log("Notes Updated")
            }
    }
}

fun fireAddNote(note:Note) {
    firebaseDatabase {
        it.child(DatabasePaths.NOTES.path).child(note.id).setValue(note)
    }
}
