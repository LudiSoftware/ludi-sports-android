package io.usys.report.ui.ludi.note

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.DatabasePaths
import io.usys.report.realm.*
import io.usys.report.realm.model.Note
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.utils.bind
import io.usys.report.utils.bindTextView


fun RecyclerView?.setupTeamNoteList(teamId: String, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    realm().findAllByField<Note>("aboutTeamId", teamId)?.let { notes ->
        rv?.loadInCustomAttributes(notes.toSafeRealmList(), DatabasePaths.NOTES.path, onClick)
    }
}

fun RecyclerView?.setupPlayerNoteList(playerId: String, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    realm().findAllByField<Note>("aboutPlayerId", playerId)?.let { notes ->
        rv?.loadInCustomAttributes(notes.toSafeRealmList(), DatabasePaths.NOTES.path, onClick)
    }
}

class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtNoteName = itemView.bindTextView(R.id.cardNoteSmallTxtUserName)
    var txtNoteDate = itemView.bindTextView(R.id.cardNoteSmallTxtDate)
    var txtMessage = itemView.bindTextView(R.id.cardNoteSmallTxtMessage)
    var imgNoteProfile: ImageView? = itemView.bind(R.id.cardNoteSmallImgProfile)

    fun bind(note: Note?) {
        txtNoteName?.text = note?.ownerName
        txtNoteDate?.text = note?.dateCreated ?: "unknown"
        txtMessage?.text = note?.message ?: "No message"
    }
}