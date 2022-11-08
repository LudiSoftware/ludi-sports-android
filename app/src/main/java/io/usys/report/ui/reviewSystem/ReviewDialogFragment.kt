package io.usys.report.ui.reviewSystem

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import io.usys.report.R
import io.usys.report.model.Coach
import io.usys.report.utils.bind
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : November 2022.
 */

class ReviewDialogFragment(var currentCoach: Coach) : DialogFragment() {

    companion object {
        const val TAG = "PopFragmentDialog"
    }

    private var rootview: View? = null
    private var dialogFragment: AlertDialog.Builder? = null
//    private var layoutResource: Int = R.layout.fragment_pop
    var message: String = "YSR"
    var positiveButton: String = "Okay"
    var submitCallback: (() -> Unit)? = null
    var positiveButtonCallbackFunction: ((Any, Any) -> Unit)? = null
    var cancelButton: Button? = null

    private var ysrCoachReviewObj: YsrCoachReview? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())

        rootview = this.layoutInflater.inflate(R.layout.dialog_review_coach_layout, null)
        ysrCoachReviewObj = rootview?.findViewById(R.id.reviewCoachRootCard)
        ysrCoachReviewObj?.currentCoach = currentCoach
//        submitButton = rootview?.bind(R.id.reviewBtnSubmit)
        cancelButton = rootview?.bind(R.id.reviewBtnCancel)
        setOnClickListeners()
        ysrCoachReviewObj?.finishCallback = submitCallback
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