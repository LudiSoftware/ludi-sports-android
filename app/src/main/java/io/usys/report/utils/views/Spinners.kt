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


class LudiSpinnerOnItemSelected(val itemSelected: (parent: AdapterView<*>?, view: View?, position: Int?, id: Long?) -> Unit) : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemSelected(parent, view, position, id)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        onNothingSelected(parent)
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        itemSelected(p0, p1, p2, p3)
    }
}
fun Spinner.onItemSelected(onItemSelected: (parent: AdapterView<*>?, view: View?, position: Int?, id: Long?) -> Unit) {
    this.onItemSelectedListener = LudiSpinnerOnItemSelected(onItemSelected)
}


inline fun Spinner.setupRosterTypeSpinner(rosters: MutableMap<String, String>, crossinline onItemSelected: (Int, String) -> Unit) {
    val rosterEntries = rosters.keys.toMutableList()
    val spinnerAdapter = LudiSpinnerAdapter(this.context, rosterEntries)
    this.adapter = spinnerAdapter

    val callback: (parent: AdapterView<*>?, view: View?, position: Int?, id: Long?) -> Unit = { _, _, _, _ ->
        val selectedEntry = this.selectedItem.toString()
        onItemSelected(this.selectedItemPosition, selectedEntry)
    }
    this.onItemSelectedListener = LudiSpinnerOnItemSelected(callback)
}