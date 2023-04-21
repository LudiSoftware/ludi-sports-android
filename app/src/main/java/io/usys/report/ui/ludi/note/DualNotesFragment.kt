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
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.NoteDualFragmentBinding
import io.usys.report.firebase.fireludi.doubleId
import io.usys.report.firebase.fireludi.fireAddNote
import io.usys.report.firebase.fireludi.fireGetNotesByDoubleId
import io.usys.report.providers.liveData.NoteLiveData
import io.usys.report.realm.model.Note
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.ui.fragments.*
import io.usys.report.ui.views.spinners.LudiSpinnerAdapter
import io.usys.report.ui.views.spinners.setupSpinner
import io.usys.report.utils.isKeyboardVisible
import io.usys.report.utils.log
import io.usys.report.utils.views.onItemSelected

/**
 * Created by ChazzCoin : October 2022.
 */

class DualNotesFragment : LudiStringIdsFragment() {

    companion object {
        const val NOTE_PLAYER = "Player"
        const val NOTE_TEAM = "Team"
        const val TAB = "Notes"
    }

    private var noteLiveData: NoteLiveData? = null
    private val noteTypes = listOf("Player", "Team", "TryOut", "General")

    var notesType: String? = "General"
    var notesSubtype: String? = "General"

    var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: NoteDualFragmentBinding? = null
    private val binding get() = _binding!!
    var notesTitle: String = "Notes"
    // Spinners
    var spinnerType: Spinner? = null
    var adapterType: CustomSpinnerAdapter? = null
    var spinnerSubtype: Spinner? = null
    var adapterSubtype: CustomSpinnerAdapter? = null
    // User Options
    private var switch: SwitchCompat? = null
    val selectedItems1 = mutableMapOf<Int, String>()
    val selectedItems2 = mutableMapOf<Int, String>()
    // User Buttons
    private var submitButton: Button? = null
    private var cancelButton: Button? = null

    // Note Setup
    private var coachId: String? = null
    private var newNote: Note? = Note()
    private var notes: RealmList<Note>? = RealmList()

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = NoteDualFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        realmInstance?.safeUserId {
            coachId = it
        }

        bindViews()
        loadNotes()
        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun loadNotes() {
        if (!playerId.isNullOrEmpty()) {
            noteLiveData = NoteLiveData(user!!.id, playerId!!, realmInstance!!, viewLifecycleOwner).apply {
                enable()
            }
            notesType = NOTE_PLAYER
            realmInstance?.fireGetNotesByDoubleId(user!!.id, playerId)
        } else {
            noteLiveData = NoteLiveData(user!!.id, teamId!!, realmInstance!!, viewLifecycleOwner).apply {
                enable()
            }
            notesType = NOTE_TEAM
            realmInstance?.fireGetNotesByDoubleId(user!!.id, teamId)
        }
        noteLiveData?.observe(viewLifecycleOwner) { _ ->
            log("Notes results updated")
            setupDisplayNotes()
        }

    }

    private fun setupDisplayNotes() {
        if (notesType == NOTE_PLAYER) {
            notesTitle = "Player Notes"
            _binding?.includeNoteList?.root?.hideTitle()
            _binding?.includeNoteList?.root?.setupPlayerNotesList(playerId, onClickReturnViewRealmObject)
        } else {
            notesTitle = "Team Notes"
            _binding?.includeNoteList?.root?.hideTitle()
            _binding?.includeNoteList?.root?.setupTeamNotesList(teamId, onClickReturnViewRealmObject)
        }
    }

    private fun bindViews() {
        switch = _binding?.noteSwitch
        spinnerType = _binding?.typeSpinner
        spinnerSubtype = _binding?.subtypeSpinner
    }

    private fun setupSpinners() {
        // Spinners
        spinnerType?.setupSpinner(noteTypes) {
            notesType = it
            log("Notes Type: $notesType")
        }
        spinnerSubtype.setupSpinner(noteTypes) {
            notesSubtype = it
            log("Notes Type: $notesType")
        }
    }

    private fun setupDisplay() {

        // Keyboard Listener
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

        setupSpinners()
        toggleViewMode()
        setupDisplayNotes()

        // Toggle Switch between View/Create Note
        switch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Create Note Mode
                toggleCreateMode()
            } else {
                // View Note List Mode
                toggleViewMode()
            }
        }
        submitButton()
    }

    private fun submitButton() {
        // Submit Button
        _binding?.createNoteBtnSubmit?.setOnClickListener {
            // Submit Note
            addNoteToFirebase()
        }
    }

    private fun toggleCreateMode() {
        _binding?.dualNotesHeaderTitle?.text = "Leave a Note!"
        _binding?.createNoteMainLayout?.visibility = View.VISIBLE
        _binding?.includeNoteList?.root?.visibility = View.GONE
    }
    private fun toggleViewMode() {
        _binding?.dualNotesHeaderTitle?.text = notesTitle
        switch?.isChecked = false
        _binding?.createNoteMainLayout?.visibility = View.GONE
        _binding?.includeNoteList?.root?.visibility = View.VISIBLE

    }

    /** Create Note **/
    private fun addNoteToFirebase() {
        val noteMessage = _binding?.createNoteEditComment?.text.toString()
        val noteObject = Note()
        noteObject.type = notesType
        noteObject.subtype = notesSubtype
        noteObject.coachId = coachId
        noteObject.ownerId = user?.id
        noteObject.doubleId = doubleId(coachId!!, teamId ?: playerId ?: "private")
        noteObject.ownerName = user?.name
        noteObject.aboutTeamId = teamId
        noteObject.aboutCoachId = "unassigned"
        noteObject.aboutPlayerId = playerId
        noteObject.message = noteMessage
        fireAddNote(noteObject)
        clearScreen()
        toggleViewMode()
    }

    // clear screen after submit
    private fun clearScreen() {
        _binding?.createNoteEditComment?.text?.clear()
        requireActivity().dismissKeyboardShortcutsHelper()
    }

    private fun moveLayoutUpwards() {
        // Adjust the layout as needed when the keyboard is shown.
        val translateY = -resources.getDimensionPixelSize(R.dimen.translate_y_value)
        _binding?.createNoteEditComment?.animate()?.translationY(translateY.toFloat())?.setDuration(0)?.start()
    }

    private fun resetLayoutPosition() {
        // Reset the layout position when the keyboard is hidden.
        _binding?.createNoteEditComment?.animate()?.translationY(0f)?.setDuration(0)?.start()
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
