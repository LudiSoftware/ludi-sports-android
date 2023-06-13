package io.usys.report.ui.popups

import android.app.Activity
import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import io.usys.report.R

inline fun Activity.selectATeam(title: String, body: String, crossinline block: () -> Unit): Dialog {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.ludi_dialog_yes_no)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val txtTitle = dialog.findViewById(R.id.dialogYesNoTxtTitle) as TextView
    val txtBody = dialog.findViewById(R.id.dialogYesNoTxtBody) as TextView
    txtTitle.text = title
    txtBody.text = body
    // On Clicks
    val okay = dialog.findViewById(R.id.dialogYesNoBtnOkay) as Button
    val cancel = dialog.findViewById(R.id.dialogYesNoBtnCancel) as Button
    okay.text = "Sign Out"
    cancel.text = "Cancel"
    okay.setOnClickListener {
        block()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
    return dialog
}