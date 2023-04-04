package io.usys.report.ui.ludi.roster

import android.view.View
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.usys.report.R
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.model.PLAYER_STATUS_OPEN
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.utils.bind
import io.usys.report.utils.bindTextView
import io.usys.report.utils.views.getColor
import io.usys.report.utils.views.loadUriIntoImgView
import io.usys.report.utils.views.wiggleOnce
import org.jetbrains.anko.sdk27.coroutines.onLongClick

open class PlayerMediumGridViewHolder(var itemView: View, var config: RosterLayoutConfig) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerMediumGridImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerMediumGridTxtName)
    var txtItemPlayerOne = itemView.bindTextView(R.id.cardPlayerMediumGridTxtOne)
    var txtItemPlayerTwo = itemView.bindTextView(R.id.cardPlayerMediumGridTxtTwo)
    var cardBackground = itemView.bind<CardView>(R.id.cardPlayerMediumGridLayout)
    var cardChkSelected = itemView.bind<CheckBox>(R.id.cardPlayerMediumGridChkSelected)


    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let { itPlayer ->

            cardChkSelected.setOnCheckedChangeListener(null)
            cardChkSelected.isChecked = itPlayer.status == PLAYER_STATUS_SELECTED

            cardChkSelected.setOnCheckedChangeListener { _, isChecked ->
                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                val realm = realm()
                realm.safeWrite {
                    realm.findPlayerRefById(itPlayer.id)?.let { playerRef ->
                        playerRef.status = newStatus
                    }
                }
            }

            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Position: ${position.toString()}"
            txtItemPlayerTwo?.text = itPlayer.orderIndex.toString()
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }

            itemView.onLongClick {
                it?.wiggleOnce()
            }

            itemView.setOnClickListener {
                config.itemClickListener?.invoke(it, itPlayer)
            }

        }
    }

    fun bindTryout(player: PlayerRef?, position: Int, selectedCount:Int) {
        player?.let { itPlayer ->

            val isSelected = itPlayer.status == PLAYER_STATUS_SELECTED
            val isInTop = position <= selectedCount

            // Player Selection Color
            // If in top 20, make green
            if (isInTop) {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardSelected))
            } else if (isSelected) {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardYellow))
            } else {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.white))
            }

            // Selected CheckBox
            cardChkSelected.setOnCheckedChangeListener(null)
            cardChkSelected.isChecked = isSelected
            cardChkSelected.setOnCheckedChangeListener { _, isChecked ->
                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                val realm = realm()
                realm.safeWrite {
                    realm.findPlayerRefById(itPlayer.id)?.let { playerRef ->
                        playerRef.status = newStatus
                    }
                }
            }

            // Basic Tryout Attributes
            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Tag: ${itPlayer.tryoutTag}"
            txtItemPlayerTwo?.text = itPlayer.position
            // Image Profile
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }
        }
    }

}

class PlayerTinyHorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerTinyHorizontalImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerTinyHorizontalTxtName)

    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let {
            txtItemPlayerName?.text = it.name
            it.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }
        }
    }
}