package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.viewpager2.widget.ViewPager2
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.views.LudiViewGroupViewModel
import io.usys.report.ui.views.touchAdapters.RosterDragDropAction
import io.usys.report.ui.views.touchAdapters.RosterItemTouchListener
import io.usys.report.utils.capitalizeFirstChar
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    companion object {
        fun newRoster(): RosterBuilderFragment {
            val fragment = RosterBuilderFragment()
            return fragment
        }
    }
    private lateinit var ludiViewGroupViewModel: LudiViewGroupViewModel
    private var viewPager2: ViewPager2? = null

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var itemTouchListener: RosterDragDropAction? = null
    var itemTouchHelper:ItemTouchHelper? = null
    var touchListener: RosterItemTouchListener? = null

    var teamId: String = "unknown"

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
    var rosterIds = mutableMapOf<String,String>()
    private var rosterEntries = rosterIds.values.toList()
    var currentRosterId: String? = null
    var tryoutId: String? = null
    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RosterBuilderFragmentBinding.inflate(inflater, container, false)
        rootView = binding.root

        arguments?.let {
            teamId = it.getString("teamId") ?: "unknown"
        }
        ludiViewGroupViewModel = ViewModelProvider(requireActivity())[LudiViewGroupViewModel::class.java]

        ludiViewGroupViewModel?.ludiViewGroup?.value?.viewPager?.let {
            viewPager2 = it
        }


        setupRosterIds()
//        setupTeamRosterRealmListener()
        setupSpinner()
        return rootView
    }

    private fun setupSpinner() {
        rosterEntries = rosterIds.keys.toList()
        val spinnerAdapter = ArrayAdapter(
            requireContext(), // Your context (e.g., Activity, FragmentActivity, or use 'requireContext()' in a Fragment)
            android.R.layout.simple_spinner_item, // Layout for each item in the Spinner
            rosterEntries
        )

        _binding?.rosBuilderLudiSpinRosterType?.adapter = spinnerAdapter

        _binding?.rosBuilderLudiSpinRosterType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedEntry = parent.getItemAtPosition(position)
                rosterIds.forEach { (key, value) ->
                    if (key == selectedEntry) {
                        currentRosterId = value
                    }
                }
                rosterType = selectedEntry.toString()
                _binding?.reportBuilderLudiTxtRosterType?.text = rosterType
                setupCurrentRosterDisplay(withTouch = true)

                // Update the display with the new option
                // Your logic for updating the display with the new option goes here
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing or handle the case when no item is selected, if necessary
            }
        }
    }



    /** Master Roster Setup! **/
    private fun setupRosterTypeTitle() {
        val rosterTitle = "Roster: ${rosterType.capitalizeFirstChar()}"
        _binding?.reportBuilderLudiTxtRosterType?.text = rosterTitle
    }

    private fun setupRosterIds() {
        // Official Roster
        realmInstance?.findRosterIdByTeamId(teamId)?.let { rosterId ->
            // official roster
            rosterIds["official"] = rosterId
            currentRosterId = rosterId
            _binding?.reportBuilderLudiTxtRosterType?.text = "Official Roster"
        }
        // TryOut Roster
        realmInstance?.findTryOutIdByTeamId(teamId) { tryoutId ->
            realmInstance?.findTryOutById(tryoutId)?.let { to ->
                to.rosterId?.let {
                    // tryout roster
                    rosterIds["tryout"] = it
                }
                this.tryoutId = tryoutId
            }
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupCurrentRosterDisplay(withTouch: Boolean = true) {

        disableAndClearRosterList()
        setupRosterTypeTitle()

        onClickReturnViewRealmObject = { view, realmObject ->
            //TODO: setup the click listener for the player
            log("Clicked on player: ${realmObject}")
            toPlayerProfile(teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }

        if (withTouch) {
            setupRosterListWithTouch()
        } else {
            setupRosterListNoTouch()
        }

    }

    private fun setupRosterListWithTouch() {
        currentRosterId?.let {
            val roster = realm().findRosterById(it)
            val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
            players.let { itPlayers ->
                val adapter = RosterListAdapter(itPlayers, onClickReturnViewRealmObject, "medium_grid", it)
                // Drag and Drop
                itemTouchListener = RosterDragDropAction(adapter)
                itemTouchHelper = ItemTouchHelper(itemTouchListener!!)
                //Attachments
                itemTouchHelper?.attachToRecyclerView(_binding?.rosterBuilderLudiRosterView?.root)
                // RecyclerView
                _binding?.rosterBuilderLudiRosterView?.root?.layoutManager = GridLayoutManager(requireContext(), 2)
                _binding?.rosterBuilderLudiRosterView?.root?.adapter = adapter
            }
        }
    }

    private fun setupRosterListNoTouch() {
        currentRosterId?.let {
            val roster = realm().findRosterById(it)
            val players: RealmList<PlayerRef> = roster?.players?.sortByOrderIndex() ?: RealmList()
            players.let { itPlayers ->
                val adapter = RosterListAdapter(itPlayers, onClickReturnViewRealmObject, "medium_grid", it)
                _binding?.rosterBuilderLudiRosterView?.root?.layoutManager = GridLayoutManager(requireContext(), 2)
                _binding?.rosterBuilderLudiRosterView?.root?.adapter = adapter
            }
        }
    }

    private fun disableAndClearRosterList() {
        itemTouchHelper?.attachToRecyclerView(null)
        itemTouchListener = null
        itemTouchHelper = null
        _binding?.rosterBuilderLudiRosterView?.root?.adapter = null
    }

    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            var updateDisplay = false
            rosterIds.values.forEach { rosterId ->
                it.find { it.id == rosterId }?.let { _ ->
                    updateDisplay = true
                }
            }
            if (updateDisplay) {
                setupCurrentRosterDisplay()
                realmInstance?.removeAllChangeListeners()
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}
