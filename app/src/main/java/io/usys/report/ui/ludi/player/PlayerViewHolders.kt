package io.usys.report.ui.ludi.player

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.loadInRealmListGridArrangable
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.getPlayerRefsByTeamId
import io.usys.report.utils.*

/**
 * SPORT LIST VIEW CONTROLS
 */

fun RecyclerView?.setupPlayerListFromSession(id: String, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
    val rv = this
    getPlayerRefsByTeamId(id) {
        rv?.loadInRealmListGridArrangable(it.players, FireTypes.PLAYERS, onClickReturnViewRealmObject)
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

        }
    }
}
