package io.usys.report.ui.ludi.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.fragment.app.Fragment
import io.usys.report.R
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
    "striker" to 9
)

fun getPosition(position: String): Int? {
    return positionMap[position.toLowerCase(Locale.ROOT)]
}
fun getPosition(number: Int): String? {
    val invertedPositionMap = positionMap.entries.associate { it.value to it.key }
    return invertedPositionMap[number]
}

fun Spinner?.setupPlayerPositionSpinner(context: Context) {
    val positions = positionMap.keys.toMutableList()
    val spinnerAdapter = LudiSpinnerAdapter(context, positions)
    this?.adapter = spinnerAdapter

    // ROSTER SELECTION
    this?.onItemSelected { parent, _, position, _ ->
        val positionName = parent.getItemAtPosition(position)
        val positionNumber = positionMap[positionName]
    }

}
