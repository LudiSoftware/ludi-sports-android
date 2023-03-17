package io.usys.report.ui.ludi.note

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.DualNoteFragmentBinding
import io.usys.report.firebase.fireAddUpdateCoachReviewDBAsync
import io.usys.report.firebase.fireludi.fireAddNote
import io.usys.report.firebase.fireludi.fireGetTeamNotesInBackground
import io.usys.report.realm.model.Note
import io.usys.report.realm.safeAdd
import io.usys.report.ui.fragments.*
import io.usys.report.utils.isKeyboardVisible
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class DualNotesFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Notes"
    }

    var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: DualNoteFragmentBinding? = null
    private val binding get() = _binding!!
    var spinnerType: Spinner? = null
    var spinnerSubtype: Spinner? = null
    val selectedItems1 = mutableMapOf<Int, String>()
    val selectedItems2 = mutableMapOf<Int, String>()
    private var submitButton: Button? = null
    private var cancelButton: Button? = null

    private var caochId: String? = null
    private var newNode: Note? = Note()

    private var switch: SwitchCompat? = null

    private var teamNotes: RealmList<Note>? = RealmList()
    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = DualNoteFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        spinnerType = _binding?.typeSpinner
        spinnerSubtype = _binding?.subtypeSpinner
        setupTeamNoteRealmListener()
        fireGetTeamNotesInBackground(teamId)
        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {

        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardVisible = requireActivity().isKeyboardVisible()

            if (isKeyboardVisible) {
                // Adjust layout when the keyboard is shown.
                moveLayoutUpwards()
            } else {
                // Reset layout when the keyboard is hidden.
                resetLayoutPosition()
            }
        }
        _binding?.createNoteMainLayout?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
        _binding?.createNoteRootCard?.cardElevation = 0F
        switch = _binding?.noteSwitch

        val items = listOf("Player", "Team", "TryOut")
        val adapter = CustomSpinnerAdapter(requireContext(), items)
        spinnerType?.adapter = adapter
        val adapter2 = CustomSpinnerAdapter(requireContext(), items)
        spinnerSubtype?.adapter = adapter2

        toggleViewMode()
        _binding?.includeNoteList?.root?.setupTeamNotesList(teamId, onClickReturnViewRealmObject)
        switch?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Create Note Mode
                toggleCreateMode()
            } else {
                // View Note List Mode
                toggleViewMode()
            }
        }


        _binding?.createNoteBtnSubmit?.setOnClickListener {
            // Submit Note
            addNoteToFirebase()
        }


    }

    private fun moveLayoutUpwards() {
        // Adjust the layout as needed when the keyboard is shown.
        val translateY = -resources.getDimensionPixelSize(R.dimen.translate_y_value)
        _binding?.createNoteMainLayout?.animate()?.translationY(translateY.toFloat())?.setDuration(250)?.start()
    }

    private fun resetLayoutPosition() {
        // Reset the layout position when the keyboard is hidden.
        _binding?.createNoteMainLayout?.animate()?.translationY(0f)?.setDuration(250)?.start()
    }

    private fun addNoteToFirebase() {
        val noteMessage = _binding?.createNoteEditComment?.text.toString()
        val type = spinnerType?.selectedItem.toString()
        val subtype = spinnerSubtype?.selectedItem.toString()
        val noteObject = Note()
        noteObject.type = type
        noteObject.subtype = subtype
        noteObject.aboutTeamId = teamId
        noteObject.coachId = user?.id
        noteObject.message = noteMessage
        fireAddNote(noteObject)
        toggleViewMode()
    }

    private fun toggleCreateMode() {
        _binding?.createNoteMainLayout?.visibility = View.VISIBLE
        _binding?.includeNoteList?.root?.visibility = View.GONE
    }
    private fun toggleViewMode() {
        switch?.isChecked = false
        _binding?.createNoteMainLayout?.visibility = View.GONE
        _binding?.includeNoteList?.root?.visibility = View.VISIBLE

    }

    private fun setupTeamNoteRealmListener() {
        val noteListener = RealmChangeListener<RealmResults<Note>> { itResults ->
            // Handle changes to the Realm data here
            log("Note listener called")
            for (note in itResults) {
                log("Note: ${note}")
                teamNotes?.safeAdd(note)
            }
            if (!teamNotes.isNullOrEmpty()) {
                _binding?.includeNoteList?.root?.setupTeamNotesList(teamId, onClickReturnViewRealmObject)
            }
        }
        realmInstance?.where(Note::class.java)?.findAllAsync()?.addChangeListener(noteListener)
    }

    override fun onPause() {
        super.onPause()
        _binding?.createNoteMainLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
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
