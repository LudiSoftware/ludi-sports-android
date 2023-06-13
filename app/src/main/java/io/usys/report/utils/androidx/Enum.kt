package io.usys.report.utils.androidx

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.Locale

fun Enum<*>.toColor(context: Context): Int {
    val colorId = context.resources.getIdentifier(this.name, "color", context.packageName)
    return ContextCompat.getColor(context, colorId)
}

fun Enum<*>.toStringRes(context: Context): String {
    val stringId = context.resources.getIdentifier(this.name, "string", context.packageName)
    return context.getString(stringId)
}

fun Enum<*>.toDrawable(context: Context): Drawable? {
    val drawableId = context.resources.getIdentifier(this.name, "drawable", context.packageName)
    return ContextCompat.getDrawable(context, drawableId)
}


inline fun <reified T : Enum<T>> T.size(): Int = enumValues<T>().size

inline fun <reified T : Enum<T>> T.prev(): T {
    val values = enumValues<T>()
    val prevIndex = (ordinal - 1 + values.size) % values.size
    return values[prevIndex]
}

inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextIndex = (ordinal + 1) % values.size
    return values[nextIndex]
}

fun Enum<*>.toReadableString() = this.name.replace("_", " ").capitalize(Locale.getDefault())
