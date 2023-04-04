package io.usys.report.utils.views

import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox

inline fun AppCompatCheckBox.onCheckListener(crossinline onCheckedChanged: (Boolean) -> Unit) {
    setOnCheckedChangeListener { _, isChecked ->
        onCheckedChanged(isChecked)
    }
}
inline fun CheckBox.onCheckListener(crossinline onCheckedChanged: (Boolean) -> Unit) {
    setOnCheckedChangeListener { _, isChecked ->
        onCheckedChanged(isChecked)
    }
}