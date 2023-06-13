package io.usys.report.utils.views

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.usys.report.R

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */

fun EditText.getString() = this.text.toString()

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
})}
fun EditText.beforeTextChanged(beforeTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s.toString())
        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
})}
fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }
    })}
fun EditText.onDoneAction(action: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            action()
        }
        false
    }
}
fun EditText.errorIfEmpty(errorMessage: String): Boolean {
    if (this.text.isBlank()) {
        this.error = errorMessage
        return true
    }
    return false
}
fun EditText.limitLinesAndScroll(maxLines: Int) {
    this.maxLines = maxLines
    this.isVerticalScrollBarEnabled = true
    this.movementMethod = ScrollingMovementMethod.getInstance()
}



fun EditText.makeRed() {
    val defaultColor = ContextCompat.getColor(this.context, R.color.ysrFadedRed)
    this.backgroundTintList = ColorStateList.valueOf(defaultColor)
}

inline fun EditText.onEnterKeyPressed(crossinline onEnterPressed: () -> Unit) {
    setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
            onEnterPressed()
            true
        } else {
            false
        }
    }
}