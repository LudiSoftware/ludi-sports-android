package io.usys.report.ui.ludi.player

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.DatabasePaths
import io.usys.report.realm.findByField
import io.usys.report.realm.findRosterById
import io.usys.report.realm.loadInRealmListGridArrangable
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.ui.ludi.formationbuilder.TeamSession
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * SPORT LIST VIEW CONTROLS
 */

fun RecyclerView?.setupPlayerListFromRosterId(id: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
    val rv = this
    val roster = realm().findRosterById(id)
    val players = roster?.players
    players?.let {
        rv?.loadInRealmListGridArrangable(it, DatabasePaths.PLAYERS.path, onClickReturnViewRealmObject)
    }
}

fun RecyclerView?.setupPlayerListFromTeamSession(id: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
    val rv = this
    val ts = realm().findByField<TeamSession>(field = "teamId", value = id)
    val players = ts?.roster?.players
    players?.let {
        rv?.loadInRealmListGridArrangable(it, DatabasePaths.PLAYERS.path, onClickReturnViewRealmObject)
    }
}

class PlayerTinyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerTinyImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerTinyTxtName)
    var txtItemPlayerRank = itemView.bindTextView(R.id.cardPlayerTinyTxtRank)

    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let {
            txtItemPlayerName?.text = it.name
            txtItemPlayerRank?.text = position.toString()
            it.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }
        }
    }
}
