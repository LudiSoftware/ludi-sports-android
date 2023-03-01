package io.usys.report.realm.model

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.*
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.toRealmList
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

fun RecyclerView?.setupNoteList(realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    var organizationList: RealmList<Organization>?
    fireGetOrderByEqualToAsync(DatabasePaths.NOTES.path, Organization.ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
        organizationList = this?.toRealmList()
        rv?.loadInRealmList(organizationList, FireTypes.ORGANIZATIONS, onClick)
    }
}

class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtNoteName = itemView.bindTextView(R.id.cardTeamSmallTxtName)
    var txtNoteDate = itemView.bindTextView(R.id.cardNoteSmallTxtDate)
    var txtMessage = itemView.bindTextView(R.id.cardNoteSmallTxtMessage)
    var imgNoteProfile: ImageView = itemView.bind(R.id.cardNoteSmallImgProfile)

    fun bind(note: Note?) {
        txtNoteName?.text = note?.ownerName
        txtNoteDate?.text = note?.dateCreated ?: "unknown"
        txtMessage?.text = note?.message ?: "No message"
    }
}