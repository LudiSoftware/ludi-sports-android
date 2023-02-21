package io.usys.report.utils

import java.util.*

fun String.capitalizeFirstChar(): String {
    if (this.isEmpty()) {
        return this
    }
    return this.substring(0, 1).toUpperCase(Locale.ROOT) + this.substring(1)
}