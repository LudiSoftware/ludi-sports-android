package io.usys.report.utils

import android.net.Uri
import android.util.Patterns
import androidx.core.text.isDigitsOnly
import com.google.gson.Gson
import java.util.*


fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = this % 2 != 0

inline fun Boolean.doIfTrue(action: () -> Unit) {
    if (this) action()
}
inline fun Boolean.doIfFalse(action: () -> Unit) {
    if (!this) action()
}

fun CharSequence?.isNotBlankOrEmpty(): Boolean {
    return this != null && this.isNotBlank() && this.isNotEmpty()
}


fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

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