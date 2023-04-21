package io.usys.report.ui.views.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import io.usys.report.R
import io.usys.report.utils.views.onItemSelected

inline fun Spinner?.setupSpinner(items: List<String>, crossinline block: (String) -> Unit?) : LudiSpinnerAdapter? {
    if (this == null) return this
    val spinnerAdapter = LudiSpinnerAdapter(this.context, items)
    this.adapter = spinnerAdapter

    // POSITION SELECTION
    this.onItemSelected { parent, _, position, _ ->
        position?.let {
            val type = parent?.getItemAtPosition(it)
            block(type.toString())
        }
    }
    return spinnerAdapter
}

class LudiSpinnerAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, R.layout.ludi_roster_spinner, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.ludi_roster_spinner, parent, false)
        val textView = view.findViewById<TextView>(R.id.ludiRosterSpinTxt)
        textView.text = items[position]
        return view
    }
}
