package io.usys.report.ui.views.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.usys.report.R

inline fun Context.createDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
    val builder = AlertDialog.Builder(this)
    builder.dialogBuilder()
    val dialog = builder.create()
    dialog.show()
}


class TeamSelectionDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.TransparentDialogTheme)
            val inflater: LayoutInflater = it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_selection_team, null)

            val dialogSelectionTeamTitle: TextView = dialogView.findViewById(R.id.dialogSelectionTeamTitle)
            val dialogSelectionTeamSpinner: Spinner = dialogView.findViewById(R.id.dialogSelectionTeamSpinner)

            // Populate the Spinner with the team list
            val teams = arrayOf("Team A", "Team B", "Team C") // Replace with your team list
            val spinnerAdapter = ArrayAdapter(it, android.R.layout.simple_spinner_item, teams)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogSelectionTeamSpinner.adapter = spinnerAdapter

//            // Set up the buttons
//            builder.setView(dialogView)
//                .setPositiveButton("OK") { _, _ ->
//                    // Handle positive button action
//                }
//                .setNegativeButton("Cancel") { _, _ ->
//                    // Handle negative button action
//                    dialog?.cancel()
//                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
