package io.usys.report.utils.views

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.usys.report.ui.views.spinners.LudiSpinnerAdapter

fun getSimpleSpinnerAdapter(context: Context, list:ArrayList<String?>) : ArrayAdapter<String?> {
    return ArrayAdapter(context, R.layout.simple_list_item_1, list)
}

fun Spinner.onItemSelected(onItemSelected: (parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            onItemSelected(parent, view, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            onNothingSelected(parent)
        }
    }
}


inline fun Spinner.setupRosterTypeSpinner(rosters: MutableMap<String, String>, crossinline onItemSelected: (Int, String) -> Unit) {
    val rosterEntries = rosters.keys.toMutableList()
    val spinnerAdapter = LudiSpinnerAdapter(this.context, rosterEntries)
    this.adapter = spinnerAdapter

    // ROSTER SELECTION
    this.onItemSelected { parent, _, position, _ ->
        val selectedEntry = parent.getItemAtPosition(position)
        onItemSelected(position, selectedEntry.toString())
    }

}