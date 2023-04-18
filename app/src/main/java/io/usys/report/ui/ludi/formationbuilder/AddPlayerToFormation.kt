package io.usys.report.ui.ludi.formationbuilder

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.cardview.widget.CardView
import io.usys.report.R
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.formationbuilder.menus.showPlayerMenuPopup
import io.usys.report.utils.inflateView
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

/** FORMATION LAYOUT: SETTING UP A PLAYER ON THE SOCCER FIELD **/
@SuppressLint("SetTextI18n")
fun RosterFormationFragment.addPlayerToFormation(playerId: String, x:Float?=null, y:Float?=null, loadingFromSession: Boolean = false) {
    // Create New PlayerView for Formation
    val playerRefViewItem = inflateView(requireContext(), R.layout.roster_player_card_formation)
    // Bind PlayerView's
    val vPlayerName = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtName)
    val vPlayerTryOutTag = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtTag)
    val vPlayerCircleLayout = playerRefViewItem.findViewById<CardView>(R.id.formationCardViewLayout)
    val vPlayerPosition = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtPosition)

    //Prepare PlayerView from Drag/Drop
    safePlayerFromRoster(playerId) { newPlayerRef ->
        if (!loadingFromSession) {
            realmInstance?.safeWrite { itRealm ->
                itRealm.rosterSessionById(rosterConfig.currentRosterId) { ts ->
                    ts.formationListIds?.let {
                        if (!it.contains(playerId)) {
                            it.add(playerId)
                        }
                    }
                }
            }
        }

        formationPlayerItemViewList.removeIf { it.tag == playerId }

        val layoutParams = preparePlayerLayoutParamsForFormation(loadingFromSession)
        // X
        if (x != null) {
            log("X: $x")
            layoutParams.leftMargin =  x.toInt() - 75
        } else if (!newPlayerRef.pointX.isNullOrEmpty() || newPlayerRef.pointX != 0) {
            layoutParams.topMargin = newPlayerRef.pointX ?: 0
        }
        // Y
        if (y != null) {
            log("Y: $y")
            layoutParams.topMargin = y.toInt() - 75
        } else if (!newPlayerRef.pointY.isNullOrEmpty() || newPlayerRef.pointY != 0) {
            layoutParams.leftMargin = newPlayerRef.pointY ?: 0
        }
        // Save New X,Y Position in Formation
        if (x != null && y != null) {
            realmInstance?.findPlayerRefById(playerId) { playerRef ->
                realmInstance?.safeWrite {
                    playerRef?.pointX = y.toInt() - 75
                    playerRef?.pointY = x.toInt() - 75
                    if (playerRef != null) {
                        it.copyToRealmOrUpdate(playerRef)
                    }
                }
            }
        }
        playerRefViewItem.layoutParams = layoutParams
        playerRefViewItem.tag = newPlayerRef.id
        vPlayerCircleLayout.tag = newPlayerRef.id
        vPlayerPosition.tag = newPlayerRef.id
        // Bind Data
        vPlayerName.text = newPlayerRef.name
        // Debugging player positions on the screen
//            vPlayerName.text = "X: [${newPlayerRef.pointX}] Y: [${newPlayerRef.pointY}]"
        vPlayerTryOutTag.text = "Tag: ${newPlayerRef.tryoutTag.toString()}"
        vPlayerPosition.text = newPlayerRef.position
        newPlayerRef.color?.let {
            vPlayerCircleLayout.setPlayerFormationBackgroundColor(it)
        }
        onTap = { _ ->
            log("Single Tap")
            this.showPlayerMenuPopup(playerRefViewItem)
        }
        // Gestures
        playerRefViewItem.onGestureDetectorRosterFormation(
            rosterId = rosterConfig.currentRosterId ?: "",
            playerId=playerId,
            onSingleTapUp = onTap,
            onLongPress = onLongPress
        )

        // Add to reference lists
        formationPlayerItemViewList.add(playerRefViewItem)
        formationPlayerColoredBackgroundViews.add(vPlayerCircleLayout)
        formationPlayerItemPositionTextViewList.add(vPlayerPosition)
        // Add to Layout
        formationRelativeLayout?.addView(playerRefViewItem)
    }

}