package io.usys.report.utils

import java.util.*

fun String.capitalizeFirstChar(): String {
    if (this.isEmpty()) {
        return this
    }
    return this.substring(0, 1).toUpperCase(Locale.ROOT) + this.substring(1)
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