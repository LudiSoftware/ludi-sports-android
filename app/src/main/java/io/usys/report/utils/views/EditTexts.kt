package io.usys.report.utils.views

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.usys.report.R

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
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