package io.usys.report.ui.ysr.player

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.usys.report.R
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.onClickReturnAnyAny
import io.usys.report.ui.onClickReturnEmpty
import io.usys.report.utils.bind
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : February 2023.
 */

class PlayerProfileDialogFragment(var currentPlayer: PlayerRef) : DialogFragment() {

    companion object {
        const val TAG = "PopFragmentDialog"
    }

    private var rootview: View? = null
    private var dialogFragment: AlertDialog.Builder? = null
    var message: String = "YSR"
    var positiveButton: String = "Okay"
    private var submitCallback: (() -> Unit)? = onClickReturnEmpty()
    var positiveButtonCallbackFunction: ((Any, Any) -> Unit)? = onClickReturnAnyAny()
    var cancelButton: Button? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())
        rootview = this.layoutInflater.inflate(R.layout.dialog_player_profile_layout, null)
        cancelButton = rootview?.bind(R.id.reviewBtnCancel)
        setOnClickListeners()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogFragment?.setView(rootview)
            ?.setMessage(message)
            ?.fairSetPositiveButton(positiveButton, positiveButtonCallbackFunction)
        return dialogFragment!!.create()
    }

    private fun setOnClickListeners() {
        submitCallback = {
            this.dismiss()
        }
        cancelButton?.setOnClickListener {
            this.dismiss()
        }
    }

}

fun AlertDialog.Builder?.fairSetPositiveButton(positiveButton:String, callbackFunction: ((Any, Any) -> Unit)?) {
    this?.setPositiveButton(positiveButton) { _,_ ->
        log("Positive Button Pressed!")
        callbackFunction?.invoke(true, "success")
    }
}

//inline fun <reified T> AlertDialog.Builder?.fairSetPositiveButton(positiveButton:String, crossinline block: () -> Unit) {
//    this?.setPositiveButton(positiveButton) { _,_ ->
//        log("Positive Button Pressed!")
//        block()
//    }
//}