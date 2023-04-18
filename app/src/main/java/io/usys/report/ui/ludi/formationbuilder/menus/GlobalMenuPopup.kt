package io.usys.report.ui.ludi.formationbuilder.menus

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import io.usys.report.R
import io.usys.report.providers.fireRosterUpdatePlayers
import io.usys.report.providers.fireRosterUpdateRoster
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.formationbuilder.RosterFormationFragment
import io.usys.report.ui.ludi.formationbuilder.onGestureDetectorRosterFormation
import io.usys.report.ui.ludi.formationbuilder.preparePlayerLayoutParamsForFormation
import io.usys.report.utils.attachViewsToOnClickListener
import io.usys.report.utils.inflateView
import io.usys.report.utils.log

/**
 * GLOBAL MENU: Floating Action Menu Functions - display process function
 */

fun RosterFormationFragment.createGlobalMenuButton() {
    this.globalFloatingMenuView = inflateView(requireContext(), R.layout.roster_menu_button_formation)
    val layoutParams = preparePlayerLayoutParamsForFormation()
    val viewTag = 999
    globalFloatingMenuView?.tag = viewTag
    globalFloatingMenuView?.layoutParams = layoutParams
    onMenuTap = { _ ->
        log("Single Tap")
        this.showGlobalMenuPopup(globalFloatingMenuView!!)
    }
    // Gestures
    globalFloatingMenuView.onGestureDetectorRosterFormation(
        rosterId=null,
        playerId=viewTag.toString(),
        onSingleTapUp=onMenuTap
    )

    // Add to Layout
    formationRelativeLayout?.addView(globalFloatingMenuView)
}
fun RosterFormationFragment.showGlobalMenuPopup(anchorView: View) {
    val popupView = LayoutInflater.from(this.requireContext()).inflate(R.layout.menu_formation_options, null)
    val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    val playerId = anchorView.tag.toString()
    val layoutSaveData = popupView.findViewById<LinearLayout>(R.id.menuFormationSaveDataLayout)
    val layoutSubmitRoster = popupView.findViewById<LinearLayout>(R.id.menuFormationSubmitRosterLayout)
    val layoutTeamColors = popupView.findViewById<LinearLayout>(R.id.menuFormationToggleTeamColorsLayout)
    val layoutResetFormation = popupView.findViewById<LinearLayout>(R.id.menuFormationResetPlayersLayout)

//    val layoutIsSelected = popupView.findViewById<LinearLayout>(R.id.menuPlayerIsSelectedLayout)
    val checkBoxTeamColors = popupView.findViewById<CheckBox>(R.id.menuFormationToggleTeamColorsCheckBox)
//    val btnProfile = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnProfile)
//    val btnChangeTeams = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnChangeTeams)

    // Load animations
    val unfoldAnimation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.unfold)
    val foldAnimation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fold)

    // If you want to dismiss the popup when clicking outside of it
    popupWindow.isOutsideTouchable = true
    popupWindow.isFocusable = true
    // Set the unfold animation when showing the popup
    popupWindow.contentView.startAnimation(unfoldAnimation)

    layoutSaveData.attachViewsToOnClickListener {
        log("layoutSaveData")
        rosterConfig.currentRosterId?.let {
            realmInstance?.fireRosterUpdatePlayers(it)
        }
        popupWindow.dismiss()
    }

    layoutSubmitRoster.attachViewsToOnClickListener {
        log("layoutSubmitRoster")

        popupWindow.dismiss()
    }

    layoutTeamColors.attachViewsToOnClickListener {
        log("menu_player_return_to_deck")
        popupWindow.dismiss()
    }

//    realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { rosterSession ->
//        realmInstance?.safeWrite {
//            if (rosterSession.teamColorsAreOn) {
////                menuItem.title = "Turn ON Team Colors"
//                log("Turn OFF Team Colors")
//                rosterSession.teamColorsAreOn = false
//            } else {
////                menuItem.title = "Turn OFF Team Colors"
//                log("Turn ON Team Colors")
//                rosterSession.teamColorsAreOn = true
//            }
//        }
//    }
    this.realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { rosterSession ->
        checkBoxTeamColors.isChecked = rosterSession.teamColorsAreOn
    }
    checkBoxTeamColors.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {

        } else {

        }
    }
    layoutResetFormation.setOnClickListener {
        log("layoutResetFormation")
        resetFormationLayout()
        popupWindow.dismiss()
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

