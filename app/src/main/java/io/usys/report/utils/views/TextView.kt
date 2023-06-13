package io.usys.report.utils.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
fun TextView.setHtml(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        text = Html.fromHtml(html)
    }
}

fun TextView.clear() {
    text = ""
}

fun TextView.makeLinksClickable() {
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setDrawableLeft(@DrawableRes drawableRes: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0)
}

fun TextView.setDrawableRight(@DrawableRes drawableRes: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)
}
fun TextView.underlineText() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}
fun TextView.strikeThroughText() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}
fun TextView.boldText() {
    this.setTypeface(typeface, Typeface.BOLD)
}
fun TextView.italicText() {
    this.setTypeface(typeface, Typeface.ITALIC)
}
fun TextView.copyTextToClipboard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(null, this.text)
    clipboard.setPrimaryClip(clip)
}

fun TextView.appendWithSpace(text: String) {
    this.append(" ")
    this.append(text)
}
fun TextView.changeTextColor(@ColorRes color: Int) {
    this.setTextColor(ContextCompat.getColor(context, color))
}
// make text red
fun TextView.changeTextToRed() {
    this.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
}
fun TextView.changeTextSize(size: Float) {
    this.textSize = size
}
fun TextView.setTextFromResources(@StringRes stringRes: Int) {
    this.text = context.getString(stringRes)
}

fun TextView.appendNewLine(text: String) {
    this.append("\n")
    this.append(text)
}
fun TextView.appendWithNewLine(text: String) {
    this.append("\n$text")
}

