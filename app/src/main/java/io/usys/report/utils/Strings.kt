package io.usys.report.utils

import androidx.core.text.isDigitsOnly
import java.util.*

fun String.capitalizeFirstChar(): String {
    if (this.isEmpty()) {
        return this
    }
    return this.substring(0, 1).toUpperCase(Locale.ROOT) + this.substring(1)
}

fun String.extractSelectionSplitNumber(): Int? {
    val lastDigits = this.takeLastWhile { it.isDigit() }
    return if (lastDigits.isDigitsOnly() && lastDigits.isNotEmpty()) {
        lastDigits.toInt()
    } else {
        null
    }
}
fun String.splitFullName(): Pair<String, String> {
    val names = this.split(" ")
    val firstName = names.firstOrNull() ?: ""
    val lastName = names.drop(1).joinToString(" ")
    return Pair(firstName, lastName)
}

fun String?.safe(default:String=""): String {
    return this ?: default
}

fun String?.isNBE(): Boolean {
    if (this != null) {
        if (this == "") return true
    }
    return this == null || this.isBlank()
}

fun String?.isNotNBE(): Boolean {
    if (this != null) {
        if (this == "") return false
    }
    return this != null && this.isNotBlank()
}