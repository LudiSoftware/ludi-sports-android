package io.usys.report.ui.ludi.player

import android.widget.Spinner
import io.usys.report.realm.findPlayerRefById
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.roster.LudiSpinnerAdapter
import io.usys.report.utils.views.onItemSelected
import java.util.*

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

val positionMap2 = hashMapOf(
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
    "striker" to "(9) striker"
)

fun getPosition(position: String): Int? {
    return positionMap[position.toLowerCase(Locale.ROOT)]
}
fun getPosition2(position: String): String? {
    return positionMap2[position.toLowerCase(Locale.ROOT)]
}
fun getPosition(number: Int): String? {
    val invertedPositionMap = positionMap.entries.associate { it.value to it.key }
    return invertedPositionMap[number]
}

fun Spinner?.setupPlayerPositionSpinner(playerId: String) {
    if (this == null) return
    val realmInstance = realm()
    val positions = positionMap2.keys.toMutableList()
    val spinnerAdapter = LudiSpinnerAdapter(this.context, positions)
    this.adapter = spinnerAdapter

    val player = realmInstance.findPlayerRefById(playerId)
    getPosition(player?.position!!)?.let {
        positionMap2.forEach { (key, _) ->
            positions.find { it == key }?.let {
                val tempIndex = positions.indexOf(it)
                this.setSelection(tempIndex)
            }}
        }

    // ROSTER SELECTION
    this.onItemSelected { parent, _, position, _ ->
        val positionName = parent.getItemAtPosition(position)
        val positionNumber = positionMap[positionName]
        realmInstance.safeWrite {
            it.findPlayerRefById(playerId)?.position = positionNumber.toString()
        }
    }

}
