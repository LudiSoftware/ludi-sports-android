package io.usys.report.ui.ysr.player

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateReviewDBAsync
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Review
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.ui.ysr.review.engine.calculateAverageRatingScore
import io.usys.report.ui.ysr.review.engine.updateOrgRatingCount
import io.usys.report.ui.ysr.review.engine.updateOrgRatingScore
import io.usys.report.utils.bind
import io.usys.report.utils.makeGone

fun createPlayerProfileDialog(activity: Activity, player: PlayerRef) : Dialog {

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_player_profile_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    // Buttons
    val btnCreateEval = dialog.bind<Button>(R.id.btnPlayerDialogCreateEval)
    val btnCreateNote = dialog.bind<Button>(R.id.btnPlayerDialogCreateNote)
    val btnSendOffer = dialog.bind<Button>(R.id.btnPlayerDialogSendOffer)
    // Includes
    val playerProfile = dialog.bind<View>(R.id.includePlayerProfileHeader)
    val playerName = playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderTxtProfileName)
    val playerRank = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogRank)
    val playerTryoutTag = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogTryOutTag)
    val playerPosition = playerProfile.rootView?.findViewById<TextView>(R.id.txtPlayerDialogPosition)
    // Hide unused views
    playerProfile.rootView?.findViewById<TextView>(R.id.cardUserHeaderTxtProfileReviewCount)?.makeGone()
    playerProfile.rootView?.findViewById<RatingBar>(R.id.cardUserHeaderRatingBar)?.makeGone()

    fun loadData() {
        playerName?.text = player.name
        playerRank?.text = player.rank.toString()
//        playerPosition?.text = player.position
        playerTryoutTag?.text = player.tryoutTag
    }
    loadData()

    btnCreateEval.setOnClickListener {
        dialog.dismiss()
    }
    btnCreateNote.setOnClickListener {
        dialog.dismiss()
    }
    btnSendOffer.setOnClickListener {
        dialog.dismiss()
    }

    return dialog


}