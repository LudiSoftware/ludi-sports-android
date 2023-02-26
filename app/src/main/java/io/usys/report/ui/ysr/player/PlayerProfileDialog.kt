package io.usys.report.ui.ysr.player

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.Realm
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.toRealmObjects
import io.usys.report.realm.*
import io.usys.report.realm.model.*
import io.usys.report.utils.*
import kotlin.collections.isNullOrEmpty

fun popPlayerProfileDialog(activity: Activity, teamId:String, playerId: String) : Dialog {

    var onClick: ((dataSnapshot: DataSnapshot?) -> Unit)?

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_player_profile_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    // Buttons
    val btnCreateEval = dialog.bind<Button>(R.id.btnPlayerDialogCreateEval)
    val btnSendOffer = dialog.bind<Button>(R.id.btnPlayerDialogSendOffer)
    // Includes
    val playerProfile = dialog.bind<View>(R.id.includePlayerProfileHeader)
    val playerTxtName = playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderBasicTxtProfileName)
    val playerTxtRank = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogRank)
    val playerTxtTryoutTag = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogTryOutTag)
    val playerTxtPosition = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogPosition)
    val includeNotes = playerProfile.rootView?.findViewById<View>(R.id.includeYsrListViewNotes)
    val includeNotesRecyclerView = includeNotes?.rootView?.findViewById<RecyclerView>(R.id.ysrCreateListRecycler)
    val includeNotesTitle = includeNotes?.rootView?.findViewById<TextView>(R.id.ysrCreateListTxtTitle)
    // Hide unused views
    playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderTxtProfileReviewCount)?.makeGone()
    playerProfile.rootView?.findViewById<RatingBar>(R.id.cardUserHeaderRatingBar)?.makeGone()

    fun loadData() {
        val player = realm().findByField<PlayerRef>("playerId", playerId)
        player?.let {
            playerTxtName?.text = it.name
            playerTxtRank?.text = it.rank.toString()
            playerTxtPosition?.text = it.position
            playerTxtTryoutTag?.text = it.tryoutTag
        }
    }

    onClick = { itSnapShot ->
        val notes = itSnapShot?.toRealmObjects<Note>()
        log("notes: $notes")
        if (!notes.isNullOrEmpty() && notes.size > 0) {
            includeNotes?.makeVisible()
            includeNotesTitle?.text = "Notes (${notes?.size})"
            includeNotesRecyclerView?.loadInRealmList(notes, FireTypes.NOTES, null)
        }
    }

//    getPlayerNotes(playerId, onClick)
    loadData()

    btnCreateEval.setOnClickListener {
        dialog.dismiss()
    }
    btnSendOffer.setOnClickListener {
        dialog.dismiss()
    }

    return dialog
}