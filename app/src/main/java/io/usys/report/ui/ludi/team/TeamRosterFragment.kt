package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.TeamRosterFragmentBinding
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.findRosterById
import io.usys.report.realm.findTeamById
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamRosterFragment : LudiTeamFragment() {

    companion object {
        const val TAB = "Roster"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: TeamRosterFragmentBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = TeamRosterFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        setupDisplay()

        setupTeamRosterRealmCallBack()
        setupOnClickListeners()
        return rootView
    }

    private fun setupTeamRosterRealmCallBack() {
        realmRosterCallBack = {
            setupDisplay()
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
        }
        _binding?.includeTeamRosterLudiListViewTeams?.root?.setTitle("Roster")
        _binding?.includeTeamRosterLudiListViewTeams?.root?.setupPlayerListTeamSession(teamId, onClickReturnViewRealmObject)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
//            popPlayerProfileDialog(requireActivity(), (obj as PlayerRef)).show()
        }
    }

}
