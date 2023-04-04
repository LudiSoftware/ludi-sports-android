package io.usys.report.utils.views

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

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
