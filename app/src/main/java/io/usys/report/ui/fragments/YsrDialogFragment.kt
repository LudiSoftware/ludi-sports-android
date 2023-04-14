package io.usys.report.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.usys.report.R


class YsrDialogFragment : DialogFragment() {

    private var rootview: View? = null
    private var dialogFragment: AlertDialog.Builder? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())
        rootview = this.layoutInflater.inflate(R.layout.ludi_chat, null)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        dialogFragment?.setView(rootview)?.setMessage("Team Chat")
        return dialogFragment!!.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ludi_chat, container, false)
        return view
    }
}