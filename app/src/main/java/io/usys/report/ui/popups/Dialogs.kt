package io.usys.report.utils

import android.app.Activity
import android.app.Dialog
import android.widget.*
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.realm.model.Session


/**
 * Created by ChazzCoin : Feb 2021.
 */

inline fun Activity.popupYesNo(title: String, body: String, crossinline block: () -> Unit): Dialog {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.dialog_yes_no)
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

fun createFieldErrorDialog(activity: Activity) : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_field_error)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    // On Clicks
    val okay = dialog.findViewById(R.id.btnOkayFieldError) as Button
    okay.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun Fragment.popAskUserPickImageGallery(block: () -> Unit) : Dialog {
    val dialog = Dialog(requireContext())
    dialog.setContentView(R.layout.dialog_ask_user_generic)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val txtTitle= dialog.bind<TextView>(R.id.txtAskUserTitle)
    val txtBody = dialog.bind<TextView>(R.id.txtAskUserBody)
    txtTitle.text = "Upload a Photo?"
    txtBody.text = "Please pick a photo to upload to Firebase!"
    // On Clicks
    val yes = dialog.bind<Button>(R.id.btnYesAskUser)
    val cancel = dialog.bind<Button>(R.id.btnCancelAskUser)
    yes.setOnClickListener {
        dialog.dismiss()
        block()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

inline fun Activity.popAskUserGeneric(title:String, body:String, crossinline block: () -> Unit) : Dialog {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.dialog_ask_user_generic)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val txtTitle= dialog.bind<TextView>(R.id.txtAskUserTitle)
    val txtBody = dialog.bind<TextView>(R.id.txtAskUserBody)
    txtTitle.text = title
    txtBody.text = body
    // On Clicks
    val yes = dialog.bind<Button>(R.id.btnYesAskUser)
    val cancel = dialog.bind<Button>(R.id.btnCancelAskUser)
    yes.setOnClickListener {
        block()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun popAskUserLogoutDialog(activity: Activity) : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_ask_user_generic)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val txtTitle= dialog.bind<TextView>(R.id.txtAskUserTitle)
    val txtBody = dialog.bind<TextView>(R.id.txtAskUserBody)
    txtBody.text = "Are you sure you want to logout?"
    // On Clicks
    val yes = dialog.bind<Button>(R.id.btnYesAskUser)
    val cancel = dialog.bind<Button>(R.id.btnCancelAskUser)
    yes.setOnClickListener {
        Session.logoutAndRestartApplication(activity)
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}
