package io.usys.report.ui.views.listAdapters.rosterLiveList

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
import io.usys.report.utils.makeGone
import io.usys.report.utils.views.getColor
import io.usys.report.utils.views.loadUriIntoImgView
import io.usys.report.utils.views.wiggleOnce
import org.jetbrains.anko.sdk27.coroutines.onLongClick


open class RosterPlayerViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerMediumGridImgProfile)
//    var imgIconOne = itemView.bind<ImageView>(R.id.cardPlayerMediumGridImgIconOne)
//    var imgIconTwo = itemView.bind<ImageView>(R.id.cardPlayerMediumGridImgIconTwo)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerMediumGridTxtName)
    var txtItemPlayerOne = itemView.bindTextView(R.id.cardPlayerMediumGridTxtOne)
//    var txtItemPlayerTwo = itemView.bindTextView(R.id.cardPlayerMediumGridTxtTwo)
    var cardBackground = itemView.bind<CardView>(R.id.cardPlayerMediumGridLayout)
    var constraintBackground = itemView.bind<View>(R.id.cardPlayerConstraintLayout)
    var cardChkSelected = itemView.bind<CheckBox>(R.id.cardPlayerMediumGridChkSelected)

    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let { itPlayer ->

            cardChkSelected?.makeGone()

            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Position: ${position.toString()}"
//            txtItemPlayerTwo?.text = itPlayer.orderIndex.toString()
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile?.loadUriIntoImgView(url)
            }

        }
    }

    fun bindTryout(player: PlayerRef?, adapter: RosterListLiveAdapter, counter: Int): Boolean {
        player?.let { itPlayer ->
            val isSelected = itPlayer.status == PLAYER_STATUS_SELECTED

            val isInTop = counter < adapter.getRosterSize()
            setTryoutColor(isInTop, isSelected)
            // Selected CheckBox
            cardChkSelected?.setOnCheckedChangeListener(null)
            cardChkSelected?.isChecked = isSelected
            cardChkSelected?.setOnCheckedChangeListener { _, isChecked ->
                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                val realm = realm()
                realm.safeWrite {
                    realm.findPlayerRefById(itPlayer.id)?.let { playerRef ->
                        playerRef.status = newStatus
                    }
                }
                adapter.refresh()
            }

            // Basic Tryout Attributes
            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Tag: ${itPlayer.tryoutTag.toString()}"
            // Image Profile
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile?.loadUriIntoImgView(url)
            }
            itemView.onLongClick {
                it?.wiggleOnce()
            }
            itemView.setOnClickListener {
                adapter.config.toPlayerProfile(itPlayer.id)
            }
            return isSelected
        }
        return false
    }

    fun bindSelection(player: PlayerRef?, position: Int, rosterLimit:Int) {
        player?.let { itPlayer ->
            val isSelected = itPlayer.status == PLAYER_STATUS_SELECTED
            val isInTop = position + 1 <= rosterLimit
            setSelectionColor(isInTop, isSelected)
            // Selected CheckBox
            cardChkSelected?.setOnCheckedChangeListener(null)
            cardChkSelected?.isChecked = isSelected
            cardChkSelected?.setOnCheckedChangeListener { _, isChecked ->
                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                val realm = realm()
                realm.safeWrite {
                    realm.findPlayerRefById(itPlayer.id)?.let { playerRef ->
                        playerRef.status = newStatus
                    }
                }
                setSelectionColor(isInTop, isChecked)
            }
            // Basic Tryout Attributes
            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Tag: ${itPlayer.tryoutTag.toString()}"
            // Image Profile
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile?.loadUriIntoImgView(url)
            }
            itemView.onLongClick {
                it?.wiggleOnce()
            }
            itemView.setOnClickListener {
//                toPlayerProfile(teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
            }
        }
    }
    private fun setSelectionColor(isInTop: Boolean, isSelected: Boolean) {
        // Player Selection Color
        // If in top 20, make green
        if (isInTop) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardSelected))
        } else if (isSelected) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardYellow))
        } else {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardRed))
        }
    }

    private fun setTryoutColor(isInTop: Boolean, isSelected: Boolean) {
        // Player Selection Color
        // If in top 20, make green
        if (isSelected) {
            if (isInTop) {
                constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardSelected))
            } else {
                constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardYellow))
            }
        } else {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardRed))
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
                imgPlayerProfile?.loadUriIntoImgView(url)
            }
        }
    }
}