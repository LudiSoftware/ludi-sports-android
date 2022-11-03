package io.usys.report.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.usys.report.R
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : November 2022.
 */

class PopFragment(layoutResource:Int=R.layout.fragment_pop) : DialogFragment() {

    companion object {
        const val TAG = "PopFragmentDialog"
    }

    private var dialogFragment: AlertDialog.Builder? = null
    private var layoutResource: Int = R.layout.fragment_pop
    var message: String = "YSR"
    var positiveButton: String = "Okay"
    var positiveButtonCallbackFunction: ((Any, Any) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment =  AlertDialog.Builder(requireContext())

        dialogFragment?.setView(R.layout.fragment_pop)
            ?.setMessage(message)
            ?.fairSetPositiveButton(positiveButton, positiveButtonCallbackFunction)

        return dialogFragment!!.create()
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