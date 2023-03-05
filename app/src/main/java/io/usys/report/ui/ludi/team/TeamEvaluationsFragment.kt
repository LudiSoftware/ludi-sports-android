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
import io.usys.report.databinding.TeamNotesFragmentBinding
import io.usys.report.firebase.fireludi.fireGetTeamNotesInBackground
import io.usys.report.realm.model.Evaluation
import io.usys.report.realm.safeAdd
import io.usys.report.ui.fragments.*
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamEvaluationsFragment : LudiTeamFragment() {

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: TeamNotesFragmentBinding? = null
    private val binding get() = _binding!!

    private var teamEvaluations: RealmList<Evaluation>? = RealmList()
//    var teamId: String? = null
//    var rosterId: String? = null
    /**
     * Get all notes based on a Team from All Coaches.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (teamId == null) teamId = unbundleTeamId()
        log("TeamId: $teamId")
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = TeamNotesFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        fireGetTeamNotesInBackground(teamId)
        setupDisplay()
        setupTeamEvalRealmListener()
        setupOnClickListeners()
        return rootView
    }

    private fun setupTeamEvalRealmListener() {
        val noteListener = RealmChangeListener<RealmResults<Evaluation>> { itResults ->
            // Handle changes to the Realm data here
            log("Note listener called")
            for (eval in itResults) {
                log("Note: ${eval}")
                teamEvaluations?.safeAdd(eval)
            }
            setupDisplay()
        }
        realmInstance?.where(Evaluation::class.java)?.findAllAsync()?.addChangeListener(noteListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
        }
        _binding?.includeTeamNoteLudiListView?.root?.setupTeamNotesList(teamId!!, onClickReturnViewRealmObject)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
//            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }
    }

}
