package io.usys.report.ui.views.listAdapters.rosterLiveList

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.usys.report.R
import io.usys.report.realm.model.PLAYER_STATUS_OPEN
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.updatePlayerStatus
import io.usys.report.ui.views.dialogs.TeamSelectionDialog
import io.usys.report.utils.bind
import io.usys.report.utils.bindTextView
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible
import io.usys.report.utils.views.getColor
import io.usys.report.utils.views.loadUriIntoImgView
import io.usys.report.utils.views.wiggleOnce
import org.jetbrains.anko.sdk27.coroutines.onLongClick


open class RosterPlayerViewHolder(var itemView: View, val adapter: RosterListLiveAdapter) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerMediumGridImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerMediumGridTxtName)
    var txtItemPlayerOne = itemView.bindTextView(R.id.cardPlayerMediumGridTxtOne)
    var txtItemTwo = itemView.bindTextView(R.id.cardPlayerMediumGridTextViewTwo)
    var cardBackground = itemView.bind<CardView>(R.id.cardPlayerMediumGridLayout)
    var constraintBackground = itemView.bind<View>(R.id.cardPlayerConstraintLayout)
    var cardChkSelected = itemView.bind<CheckBox>(R.id.cardPlayerMediumGridChkSelected)
    var cardChkSelectedTxt = itemView.bind<TextView>(R.id.cardPlayerSelectedTxtView)
    var txtOrderIndex = itemView.bind<TextView>(R.id.cardPlayerMediumGridOrderIndex)
    var btnNotes = itemView.bind<ImageButton>(R.id.cardPlayerMediumGridBtnNotes)

    @SuppressLint("SetTextI18n")
    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let { itPlayer ->

            cardChkSelected?.makeGone()
            cardChkSelectedTxt?.makeGone()

            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Position: ${itPlayer.position}"
            txtOrderIndex?.text = itPlayer.orderIndex.toString()
            itPlayer.imgUrl?.let { url ->
                imgPlayerProfile?.loadUriIntoImgView(url)
            }

            itemView.setOnClickListener {
                adapter.config.toPlayerProfile(itPlayer.id)
            }
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.white))
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindTryout(player: PlayerRef?) {
        player?.let { itPlayer ->
            val isSelected = itPlayer.status == PLAYER_STATUS_SELECTED
            val isInTop = adapter.config.selectionCounter < adapter.config.rosterSizeLimit

            cardChkSelected?.makeVisible()
            cardChkSelectedTxt?.makeVisible()

            setTryoutColor(itPlayer.id, isInTop, isSelected)
            // Selected CheckBox
            cardChkSelected?.setOnCheckedChangeListener(null)
            cardChkSelected?.isChecked = isSelected
            cardChkSelected?.setOnCheckedChangeListener { _, isChecked ->
                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                adapter.realmInstance.updatePlayerStatus(itPlayer.id, newStatus)
                adapter.refresh()
            }
            // Basic Tryout Attributes
            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Tag: ${itPlayer.tryoutTag.toString()}"
            txtItemTwo?.text = "Position: ${itPlayer.position}"
            txtOrderIndex?.text = itPlayer.orderIndex.toString()
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
            if (isSelected) {
                adapter.config.selectionCounter++
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindSelection(player: PlayerRef?, position: Int) {
        player?.let { itPlayer ->
            val playerId = itPlayer.id
            val isSelected = itPlayer.status == PLAYER_STATUS_SELECTED
            val isInTop = position + 1 <= adapter.config.rosterSizeLimit
            setSelectionColor(playerId, isInTop, isSelected)

            cardChkSelected?.makeVisible()
            cardChkSelectedTxt?.makeVisible()

            // Selected CheckBox
            cardChkSelected?.setOnCheckedChangeListener(null)
            cardChkSelected?.isChecked = isSelected
            cardChkSelected?.setOnCheckedChangeListener { _, isChecked ->
                val teamSelectionDialog = TeamSelectionDialog()
                teamSelectionDialog.show(adapter.config.parentFragment!!.childFragmentManager, "team_selection_dialog")

                var newStatus = PLAYER_STATUS_OPEN
                if (isChecked) newStatus = PLAYER_STATUS_SELECTED
                adapter.realmInstance.updatePlayerStatus(playerId, newStatus)
                setSelectionColor(playerId, isInTop, isChecked)
            }
            // Basic Tryout Attributes
            txtItemPlayerName?.text = itPlayer.name
            txtItemPlayerOne?.text = "Tag: ${itPlayer.tryoutTag.toString()}"
            txtItemTwo?.text = "Position: ${itPlayer.position}"
            txtOrderIndex?.text = itPlayer.orderIndex.toString()
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

            if (isSelected) {
                adapter.config.selectionCounter++
            }
        }
    }
    private fun setSelectionColor(playerId:String?, isInTop: Boolean, isSelected: Boolean) {
        // Player Selection Color
        if (adapter.config.selectedItemColors.containsKey(playerId)) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, adapter.config.selectedItemColors[playerId]!!))
            return
        }
        // If in top 20, make green
        var color = R.color.ludiRosterCardRed
        if (isInTop) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardSelected))
            color = R.color.ludiRosterCardSelected
        } else if (isSelected) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardYellow))
            color = R.color.ludiRosterCardYellow
        } else {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardRed))
        }
        playerId?.let { adapter.config.selectedItemColors[it] = color }
    }

    private fun setTryoutColor(playerId:String?, isInTop: Boolean, isSelected: Boolean) {
        // Player Selection Color
        if (adapter.config.selectedItemColors.containsKey(playerId)) {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, adapter.config.selectedItemColors[playerId]!!))
            return
        }
        // If in top 20, make green
        var color = R.color.ludiRosterCardRed
        if (isSelected) {
            if (isInTop) {
                constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardSelected))
                color = R.color.ludiRosterCardSelected
            } else {
                constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardYellow))
                color = R.color.ludiRosterCardYellow
            }
        } else {
            constraintBackground?.setBackgroundColor(getColor(itemView.context, R.color.ludiRosterCardRed))
        }
        playerId?.let { adapter.config.selectedItemColors[it] = color }
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