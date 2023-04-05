package io.usys.report.ui.ludi.player

import java.util.*

fun getPositionNumber(position: String): Int? {
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

    return positionMap[position.toLowerCase(Locale.ROOT)]
}

fun getPositionName(number: Int): String? {
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

    val invertedPositionMap = positionMap.entries.associate { it.value to it.key }

    return invertedPositionMap[number]
}