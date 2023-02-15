package io.usys.report.ui.ysr.player


import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.loadInRealmListGridArrangable
import io.usys.report.realm.model.Player
import io.usys.report.realm.sessionPlayers
import io.usys.report.utils.*

/**
 * SPORT LIST VIEW CONTROLS
 */


fun RecyclerView?.setupPlayerListFromSession(onClickReturnViewRealmObject: ((View, Player) -> Unit)?) {
    val rv = this
    sessionPlayers {
        rv?.loadInRealmListGridArrangable(it, FireTypes.PLAYERS, onClickReturnViewRealmObject)
        return
    }
}

class PlayerTinyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<ImageView>(R.id.cardPlayerTinyTxtName)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerTinyTxtName)

    fun bind(player: Player?) {
        player?.let { txtItemPlayerName?.text = it.name }
    }
}
