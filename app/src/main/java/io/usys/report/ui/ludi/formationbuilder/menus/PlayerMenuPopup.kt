package io.usys.report.ui.ludi.formationbuilder.menus

import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import io.usys.report.R
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.model.PLAYER_STATUS_OPEN
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.formationbuilder.RosterFormationFragment
import io.usys.report.ui.ludi.formationbuilder.setPlayerFormationBackgroundColor
import io.usys.report.ui.ludi.player.setupPlayerPositionSpinner
import io.usys.report.ui.views.navController.toPlayerProfile
import io.usys.report.utils.views.attachViewsToOnClickListener
import io.usys.report.utils.log

/** PLAYER MENU: PLAYER POPUP MENU **/
fun RosterFormationFragment.showPlayerMenuPopup(anchorView: View) {
    val popupView = LayoutInflater.from(this.requireContext()).inflate(R.layout.menu_player_options, null)
    val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    val playerId = anchorView.tag.toString()
    val layoutPlayerProfile = popupView.findViewById<LinearLayout>(R.id.menuPlayerLayoutPlayerProfile)
    val layoutChangeTeams = popupView.findViewById<LinearLayout>(R.id.menuPlayerChangeTeamsLayout)
    val layoutReturnToDeck = popupView.findViewById<LinearLayout>(R.id.menuPlayerReturnToRosterLayout)
    val imgReturnToDeck = popupView.findViewById<ImageView>(R.id.menuPlayerReturnToRosterImgBtn)
    val layoutIsSelected = popupView.findViewById<LinearLayout>(R.id.menuPlayerIsSelectedLayout)
    val checkBoxIsSelected = popupView.findViewById<CheckBox>(R.id.menuPlayerCheckIsSelected)
    val btnProfile = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnProfile)
    val btnChangeTeams = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnChangeTeams)

    // Find and set up the Spinner
    val positionSpinner = popupView.findViewById<Spinner>(R.id.menuPlayerPositionSpinner)
    positionSpinner.setupPlayerPositionSpinner(playerId, this.getFormationPlayerPositionTextView(playerId))
    // Load animations
    val unfoldAnimation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.unfold)
    val foldAnimation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fold)

    // If you want to dismiss the popup when clicking outside of it
    popupWindow.isOutsideTouchable = true
    popupWindow.isFocusable = true
    // Set the unfold animation when showing the popup
    popupWindow.contentView.startAnimation(unfoldAnimation)

    layoutPlayerProfile.attachViewsToOnClickListener(btnProfile) {
        log("menu_player_profile")
        this.toPlayerProfile(playerId)
        popupWindow.dismiss()
    }

    layoutChangeTeams.attachViewsToOnClickListener(btnChangeTeams) {
        log("menu_player_change_teams")
        this.safePlayerFromRoster(playerId) { playerRef ->
            when (playerRef.color) {
                "red" -> {
                    this.realmInstance?.safeWrite {
                        playerRef.color = "blue"
                    }
                }
                "blue" -> {
                    this.realmInstance?.safeWrite {
                        playerRef.color = "red"
                    }
                }
            }
            this.findPlayerViewInFormation(playerId) { playerView ->
                playerView.setPlayerFormationBackgroundColor(playerRef.color)
            }
        }
        popupWindow.dismiss()
    }

    layoutReturnToDeck.attachViewsToOnClickListener(imgReturnToDeck) {
        log("menu_player_return_to_deck")
        this.movePlayerToDeck(playerId)
        popupWindow.dismiss()
    }

    this.realmInstance?.findPlayerRefById(playerId)?.let { itPlayer ->
        checkBoxIsSelected.isChecked = itPlayer.status == PLAYER_STATUS_SELECTED
    }
    checkBoxIsSelected.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            this.safePlayerFromRoster(playerId) { playerRef ->
                this.realmInstance?.safeWrite {
                    playerRef.status = PLAYER_STATUS_SELECTED
                }
            }
        } else {
            this.safePlayerFromRoster(playerId) { playerRef ->
                this.realmInstance?.safeWrite {
                    playerRef.status = PLAYER_STATUS_OPEN
                }
            }
        }
    }
    layoutIsSelected.setOnClickListener {
        log("menu_player_is_selected")
        checkBoxIsSelected.toggle()
    }
    // Set the fold animation when dismissing the popup
    popupWindow.setOnDismissListener {
        popupView.startAnimation(foldAnimation)
    }

    //Setup Up/Down Creation
    val location = IntArray(2)
    anchorView.getLocationOnScreen(location)
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val anchorViewBottom = location[1] + anchorView.height
    val yOffset = if (anchorViewBottom + popupWindow.contentView.measuredHeight > screenHeight) {
        -(anchorView.height + popupWindow.contentView.measuredHeight)
    } else {
        anchorView.height
    }
    // Show PopupMenu
    popupWindow.showAtLocation(anchorView, Gravity.TOP or Gravity.START, location[0], location[1] + yOffset)
}