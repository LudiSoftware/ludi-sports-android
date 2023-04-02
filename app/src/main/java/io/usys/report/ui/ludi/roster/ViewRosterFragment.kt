package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.viewpager2.widget.ViewPager2
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.TeamRosterFragmentBinding
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.views.LudiViewGroupViewModel
import io.usys.report.ui.views.touchAdapters.RosterDragDropAction
import io.usys.report.ui.views.touchAdapters.RosterItemTouchListener
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ViewRosterFragment : LudiStringIdsFragment() {

    companion object {
        private const val ARG_ROSTER_TYPE = "roster_type"
        private const val ARG_ROSTER_ID = "roster_id"
        private const val ARG_ROSTER_TITLE = "roster_title"
        private const val ARG_ROSTER_TEAM_ID = "roster_team_id"

        fun newRoster(rosterId: String, title:String, teamId:String): ViewRosterFragment {
            val fragment = ViewRosterFragment()
            val args = Bundle()
            args.putString(ARG_ROSTER_ID, rosterId)
            args.putString(ARG_ROSTER_TITLE, title)
            args.putString(ARG_ROSTER_TEAM_ID, teamId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var ludiViewGroupViewModel: LudiViewGroupViewModel

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: TeamRosterFragmentBinding? = null
    private val binding get() = _binding!!
    private var viewPager2: ViewPager2? = null

    var itemTouchListener: RosterDragDropAction? = null
    var itemTouchHelper:ItemTouchHelper? = null
    var touchListener: RosterItemTouchListener? = null

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
    var rosterId: String? = null


    /**
     * TODO:
     *      1. Validate drag and drop works.
     *      2. Update list with new order in Realm.
     *      2. Sort List by Order.
     *      3. Create different Sort Functions like sort by Name.
     *
     * What Other Modifications or Tools would we need for this page?
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = TeamRosterFragmentBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root

        ludiViewGroupViewModel = ViewModelProvider(requireActivity())[LudiViewGroupViewModel::class.java]
        ludiViewGroupViewModel?.ludiViewGroup?.value?.viewPager?.let {
            viewPager2 = it
        }
        arguments?.let {
            rosterType = it.getString(ARG_ROSTER_TYPE) ?: "Official"
            rosterId = it.getString(ARG_ROSTER_ID) ?: "unknown"
            title = it.getString(ARG_ROSTER_TITLE) ?: "No Roster Found!"
            log("Roster type: $rosterType")
        }

        // if rosterId is null....
        //      get rosterId from teamId

        setupDisplay()
        setupTeamRosterRealmListener()
        return rootView
    }

    override fun onPause() {
        super.onPause()
        disable()
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
            toFragmentWithIds(R.id.navigation_player_profile, teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }
        rosterId?.let {
//            _binding?.includeTeamRosterLudiListViewTeams?.root?.setActivity(requireActivity())
//            _binding?.includeTeamRosterLudiListViewTeams?.root?.setupRoster(it, onClickReturnViewRealmObject)
            val roster = realm().findRosterById(it)
            val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
            players.let { itPlayers ->
                val adapter = RosterListAdapter(itPlayers, onClickReturnViewRealmObject, "medium_grid", it)
                // Drag and Drop
                itemTouchListener = RosterDragDropAction(adapter)
                itemTouchHelper = ItemTouchHelper(itemTouchListener!!)
                // Extra
                touchListener = RosterItemTouchListener(viewPager2!!)
                //Attachments
                itemTouchHelper?.attachToRecyclerView(_binding?.includeTeamRosterLudiListViewTeams?.root)
                // RecyclerView
                _binding?.includeTeamRosterLudiListViewTeams?.root?.addOnItemTouchListener(touchListener!!)
                _binding?.includeTeamRosterLudiListViewTeams?.root?.layoutManager = GridLayoutManager(requireContext(), 2)
                _binding?.includeTeamRosterLudiListViewTeams?.root?.adapter = adapter
            }
        }
    }

    fun disable() {
        itemTouchHelper?.attachToRecyclerView(null)
        itemTouchListener = null
        itemTouchHelper = null
        touchListener = null
        _binding?.includeTeamRosterLudiListViewTeams?.root?.adapter = null
    }

    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                it.findById(rosterId)?.let { roster ->
                    setupDisplay()
                }
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}

