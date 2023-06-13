package io.usys.report.utils

inline fun <T> Iterable<T>.checkAllMatch(predicate: (T) -> Boolean): Boolean {
    return all(predicate)
}

fun <T> Collection<T>?.nullSafe(): Collection<T> = this ?: emptyList()

fun <T> List<T>.getSafe(index: Int): T? {
    return if (index in 0 until size) get(index) else null
}
