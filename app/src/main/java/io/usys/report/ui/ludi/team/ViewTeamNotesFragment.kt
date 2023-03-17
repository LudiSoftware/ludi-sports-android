package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.DualNoteFragmentBinding
import io.usys.report.firebase.fireludi.fireGetTeamNotesInBackground
import io.usys.report.realm.model.Note
import io.usys.report.realm.safeAdd
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.note.DualNotesFragment
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ViewTeamNotesFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Notes"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: DualNoteFragmentBinding? = null
    private val binding get() = _binding!!

    private var teamNotes: RealmList<Note>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = DualNoteFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        fireGetTeamNotesInBackground(teamId)
        setupDisplay()
        setupTeamNoteRealmListener()
        return rootView
    }

    private fun setupTeamNoteRealmListener() {
        val noteListener = RealmChangeListener<RealmResults<Note>> { itResults ->
            // Handle changes to the Realm data here
            log("Note listener called")
            for (note in itResults) {
                log("Note: ${note}")
                teamNotes?.safeAdd(note)
            }
            if (!teamNotes.isNullOrEmpty()) setupDisplay()
        }
        realmInstance?.where(Note::class.java)?.findAllAsync()?.addChangeListener(noteListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
        }
//        _binding?.includeTeamNoteLudiListView?.root?.setupTeamNotesList(teamId, onClickReturnViewRealmObject)
    }


}
