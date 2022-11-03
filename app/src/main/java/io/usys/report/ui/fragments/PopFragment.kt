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

class PopFragment : DialogFragment() {

    companion object {
        const val TAG = "PopFragmentDialog"
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogFragment =  AlertDialog.Builder(requireContext())

        dialogFragment.setView(R.layout.fragment_pop)
            .setMessage("Testing")
            .setPositiveButton("Okay") { _,_ -> log("Okay Pressed!") }

        return dialogFragment.create()
    }


}