package io.usys.report.providers

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

/**
 * Creates a map of time intervals in 5 minute increments.
 * @return HashMap<String, Int> - A map of time intervals in 5 minute increments.
 *
 * 5 Minute Intervals: 24 Hours in a Day
 * 60 / 5 = (12) 5 minute time slots in 1 hour.
 * 12 * 24 = (288) 5 minute time slots in 24 hours.
 * 1 Field = (288) 5 minute time slots in 1 day.
 *
 * - getTime(slot: Int)
 * - getSlot(time: String)
 */

fun createFiveMinuteIntervalMap(): HashMap<String, Int> {
    val timeIntervalMap = HashMap<String, Int>()

    val formatter = DateTimeFormatter.ofPattern("hh:mma")
    var currentTime = LocalTime.MIDNIGHT
    val fiveMinuteInterval = 5

    for (i in 1..288) {
        val formattedTime = currentTime.format(formatter).toUpperCase(Locale.ROOT)
        timeIntervalMap[formattedTime] = i
        currentTime = currentTime.plusMinutes(fiveMinuteInterval.toLong())
    }

    return timeIntervalMap
}
