package io.usys.report.ui.ludi.roster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.usys.report.R

class RosterSpinnerAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, R.layout.ludi_roster_spinner, items) {

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
