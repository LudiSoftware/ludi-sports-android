package io.usys.report.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.usys.report.R

/**
 * NOT WORKING YET!
 */
fun Fragment.popup(fragment: Fragment) {
    val genericDialogFragment = LudiDialogFragmentHolder()
    genericDialogFragment.setChildFragment(fragment)
    genericDialogFragment.show(this.childFragmentManager, "LudiDialogFragmentHolder")
}

class LudiDialogFragmentHolder : DialogFragment() {

    private var rootview: View? = null
    private var dialogFragment: AlertDialog.Builder? = null
    private lateinit var childFragment: Fragment

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())
        rootview = this.layoutInflater.inflate(R.layout.dialog_fragment_holder, null)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogFragment?.setView(rootview)?.setMessage("Ludi Dialog")
        return dialogFragment!!.create()
    }

    fun setChildFragment(fragment: Fragment) {
        childFragment = fragment
    }
}