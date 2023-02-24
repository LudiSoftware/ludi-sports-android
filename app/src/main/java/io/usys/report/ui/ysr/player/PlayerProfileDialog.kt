package io.usys.report.ui.ysr.player

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateReviewDBAsync
import io.usys.report.firebase.toRealmObject
import io.usys.report.firebase.toRealmObjects
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.ui.onClickDataSnapShot
import io.usys.report.ui.views.YsrCreateListView
import io.usys.report.ui.views.YsrTitleListView
import io.usys.report.ui.ysr.review.engine.calculateAverageRatingScore
import io.usys.report.ui.ysr.review.engine.updateOrgRatingCount
import io.usys.report.ui.ysr.review.engine.updateOrgRatingScore
import io.usys.report.utils.bind
import io.usys.report.utils.log
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible
import org.w3c.dom.Text

fun popPlayerProfileDialog(activity: Activity, player: PlayerRef) : Dialog {

    var onClick: ((dataSnapshot: DataSnapshot?) -> Unit)?


    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_player_profile_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    // Buttons
    val btnCreateEval = dialog.bind<Button>(R.id.btnPlayerDialogCreateEval)
    val btnSendOffer = dialog.bind<Button>(R.id.btnPlayerDialogSendOffer)
    // Includes
    val playerProfile = dialog.bind<View>(R.id.includePlayerProfileHeader)
    val playerName = playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderTxtProfileName)
    val playerRank = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogRank)
    val playerTryoutTag = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogTryOutTag)
    val playerPosition = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogPosition)
    val includeNotes = playerProfile.rootView?.findViewById<View>(R.id.includeYsrListViewNotes)
    val includeNotesRecyclerView = includeNotes?.rootView?.findViewById<RecyclerView>(R.id.ysrCreateListRecycler)
    val includeNotesTitle = includeNotes?.rootView?.findViewById<TextView>(R.id.ysrCreateListTxtTitle)
//    includeNotes?.makeGone()
    // Hide unused views
    playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderTxtProfileReviewCount)?.makeGone()
    playerProfile.rootView?.findViewById<RatingBar>(R.id.cardUserHeaderRatingBar)?.makeGone()

    fun loadData() {
        player?.let {
            playerName?.text = it.name
            playerRank?.text = "Rank: ${it.rank.toString()}"
            playerPosition?.text = "Forward"
            playerTryoutTag?.text = "TryOut Tag: ${it.tryoutTag}"
        }
    }

    onClick = { itSnapShot ->
        val notes = itSnapShot?.toRealmObjects()
        log("notes: $notes")
        if (!notes.isNullOrEmpty() && notes.size > 0) {
            includeNotes?.makeVisible()
            includeNotesTitle?.text = "Notes (${notes?.size})"
            includeNotesRecyclerView?.loadInRealmList(notes, FireTypes.NOTES, null)
        }
    }
//    createButton?.setOnClickListener {
//        includeNotesIncludeCreateNote?.toggleCreateLayout()
//    }

    player.id?.let {
        getPlayerNotes(it, onClick)
    }
    loadData()

    btnCreateEval.setOnClickListener {
        dialog.dismiss()
    }
    btnSendOffer.setOnClickListener {
        dialog.dismiss()
    }

    return dialog
}