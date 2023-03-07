package io.usys.report.ui.ludi.note

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import io.usys.report.R
import io.usys.report.databinding.DialogCreateNoteLayoutBinding
import io.usys.report.realm.model.Note
import io.usys.report.ui.fragments.*

/**
 * Created by ChazzCoin : October 2022.
 */

class CreateNoteFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Notes"
    }

    private var _binding: DialogCreateNoteLayoutBinding? = null
    private val binding get() = _binding!!
    var spinnerType: Spinner? = null
    var spinnerSubtype: Spinner? = null
    val selectedItems1 = mutableMapOf<Int, String>()
    val selectedItems2 = mutableMapOf<Int, String>()
    private var submitButton: Button? = null
    private var cancelButton: Button? = null

    private var caochId: String? = null
    private var newNode: Note? = Note()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = DialogCreateNoteLayoutBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        spinnerType = _binding?.typeSpinner
        spinnerSubtype = _binding?.subtypeSpinner
        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {

        val items = listOf("Player", "Team", "TryOut")
        val adapter = CustomSpinnerAdapter(requireContext(), items)
        spinnerType?.adapter = adapter
        val adapter2 = CustomSpinnerAdapter(requireContext(), items)
        spinnerSubtype?.adapter = adapter2

    }

}


class CustomSpinnerAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ludi_item_spinner, parent, false)
        }
        val item = items[position]
        val textView = view!!.findViewById<TextView>(R.id.txtItemSpinner)
        textView.text = item
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ludi_item_spinner, parent, false)
        }

        val item = items[position]

        val textView = view!!.findViewById<TextView>(R.id.txtItemSpinner)
        textView.text = item

        return view
    }
}
