package io.usys.report.ui.ludi.player

import android.widget.Spinner
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.views.spinners.LudiSpinnerAdapter
import io.usys.report.utils.views.onItemSelected

val positionMap = hashMapOf(
    "goalkeeper" to 1,
    "right back" to 2,
    "center back" to 4,
    "left back" to 3,
    "defensive midfielder" to 6,
    "right midfielder" to 7,
    "central midfielder" to 8,
    "left midfielder" to 11,
    "attacking midfielder" to 10,
    "right winger" to 7,
    "left winger" to 11,
    "center forward" to 9,
    "striker" to 9,
    "substitute" to 12,
    "reserve" to 13,
    "unknown" to 0
)

val positionMapDisplay = hashMapOf(
    "goalkeeper" to "(1) goalkeeper",
    "right back" to "(2) right back",
    "center back" to "(4) center back",
    "left back" to "(3) left back",
    "defensive midfielder" to "(6) defensive midfielder",
    "right midfielder" to "(7) right midfielder",
    "central midfielder" to "(8) central midfielder",
    "left midfielder" to "(11) left midfielder",
    "attacking midfielder" to "(10) attacking midfielder",
    "right winger" to "(7) right winger",
    "left winger" to "(11) left winger",
    "center forward" to "(9) center forward",
    "striker" to "(9) striker",
    "substitute" to "(12) substitute",
    "reserve" to "(13) reserve",
    "unknown" to "(0) Unknown"
)

fun getPositionFromDisplay(position: String): String {
    return positionMapDisplay.filter { it.value == position }.keys.first()
}
fun getPosition(number: Int): String? {
    val invertedPositionMap = positionMap.entries.associate { it.value to it.key }
    return invertedPositionMap[number]
}

fun Spinner?.setupPlayerPositionSpinner(playerId: String, playerView:TextView?=null) {
    if (this == null) return
    val realmInstance = realm()
    val positionsToDisplay = positionMapDisplay.values.toMutableList()
    val spinnerAdapter = LudiSpinnerAdapter(this.context, positionsToDisplay)
    this.adapter = spinnerAdapter

    // Setup Player Position
    realmInstance.findPlayerRefById(playerId)?.let {  player ->
        val pNumber = player.position.toIntOrDefault(12) // 12
        val pp = getPosition(pNumber) // "substitute"
        val p2 = positionMapDisplay[pp]
        positionsToDisplay.find { it == p2 }?.let {
            val tempIndex = positionsToDisplay.indexOf(it)
            this.setSelection(tempIndex)
        }
    }

    // POSITION SELECTION
    this.onItemSelected { parent, _, position, _ ->
        position?.let {
            val positionName = parent?.getItemAtPosition(it)
            val p2 = getPositionFromDisplay(positionName.toString())
            val positionNumber = positionMap[p2]
            realmInstance.safeWrite { itRealm ->
                itRealm.findPlayerRefById(playerId)?.position = positionNumber.toString()
            }
            playerView?.text = positionNumber.toString()
        }
    }

}
fun String?.toIntOrDefault(defaultValue: Int = 0): Int {
    if (this == null) return defaultValue
    return if (this.isDigitsOnly()) {
        this.toInt()
    } else {
        defaultValue
    }
}