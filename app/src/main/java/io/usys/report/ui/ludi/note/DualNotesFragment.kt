package io.usys.report.ui.ludi.note

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
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
import io.usys.report.utils.log

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

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: NoteDualFragmentBinding? = null
    private val binding get() = _binding!!
    var notesTitle: String = "Notes"
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
            _binding?.includeNoteList?.root?.setupPlayerNotesList(coachId ?: "", playerId, onClickReturnViewRealmObject)
        } else {
            notesTitle = "Team Notes"
            _binding?.includeNoteList?.root?.hideTitle()
            _binding?.includeNoteList?.root?.setupTeamNotesList(coachId ?: "", teamId, onClickReturnViewRealmObject)
        }
    }

    private fun setupDisplay() {
        _binding?.createNoteRootCard?.cardElevation = 0F
        setupDisplayNotes()
        submitButton()
    }

    private fun submitButton() {
        // Submit Button
        _binding?.noteSubmitButton?.setOnClickListener {
            // Submit Note
            addNoteToFirebase()
        }
    }


    /** Create Note **/
    private fun addNoteToFirebase() {
        val noteMessage = _binding?.noteEditText?.text.toString()
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
    }

    // clear screen after submit
    private fun clearScreen() {
        _binding?.noteEditText?.text?.clear()
        requireActivity().dismissKeyboardShortcutsHelper()
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
