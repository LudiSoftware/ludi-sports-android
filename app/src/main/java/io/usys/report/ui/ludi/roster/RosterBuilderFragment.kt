package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.viewpager2.widget.ViewPager2
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
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

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var adapter: RosterListAdapter? = null

    var teamId: String = "unknown"

    var rosterType: String = "null"
    var title: String = "No Roster Found!"
    var rosterIds = mutableMapOf<String,String>()
    private var rosterEntries = mutableListOf<String>()
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

        setupRosterIds()
//        setupTeamRosterRealmListener()
        setupSpinner()
        return rootView
    }

    private fun setupSpinner() {
        rosterEntries = rosterIds.keys.toMutableList()
        val spinnerAdapter = ArrayAdapter(
            requireContext(), // Your context (e.g., Activity, FragmentActivity, or use 'requireContext()' in a Fragment)
            android.R.layout.simple_spinner_item, // Layout for each item in the Spinner
            rosterEntries
        )

        _binding?.rosBuilderLudiSpinRosterType?.adapter = spinnerAdapter

        // ROSTER SELECTION
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
        val rosterTitle = "Roster: ${rosterType.capitalizeFirstChar()} (${adapter?.itemCount})"
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
                    rosterIds["selected"] = it
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

        adapter?.disableAndClearRosterList()

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
            when (rosterType) {
                "tryout" -> {
                    adapter = RosterListAdapter(it, _binding?.rosterBuilderLudiRosterView?.root!!, onClickReturnViewRealmObject, "medium_grid")
                }
                "selected" -> {
                    adapter = RosterListAdapter(it, _binding?.rosterBuilderLudiRosterView?.root!!, onClickReturnViewRealmObject, "medium_grid")
                    adapter?.filterByStatusSelected()
                }
                else -> {
                    adapter = RosterListAdapter(it, _binding?.rosterBuilderLudiRosterView?.root!!, onClickReturnViewRealmObject, "medium_grid")
                    adapter?.disableTouch()
                }
            }
        }
        setupRosterTypeTitle()
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

//    private fun disableAndClearRosterList() {
//        itemTouchHelper?.attachToRecyclerView(null)
//        itemTouchListener = null
//        itemTouchHelper = null
//        _binding?.rosterBuilderLudiRosterView?.root?.adapter = null
//    }

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

/**
 * TODO:
 *      1. Save Roster
 *      2. Submit/Finalize Roster
 */
class RosterBuilderMenuPopupProvider(private val fragment: Fragment, private val teamId: String) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_team_menu_dropdown, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_options -> {
                showCustomPopup(fragment.requireActivity().findViewById(R.id.menuitem_options))
                return true
            }
            else -> {
            }
        }
        return false
    }

    private fun showCustomPopup(anchorView: View) {
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.team_menu_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

        // Set up click listeners for the custom menu items
        popupView.findViewById<LinearLayout>(R.id.option_formation).setOnClickListener {
            fragment.toFragmentWithIds(R.id.navigation_tryout_frag, teamId)
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.option_roster).setOnClickListener {
            fragment.toFragmentWithIds(R.id.navigation_roster_builder_frag, teamId)
            popupWindow.dismiss()
        }

        // If you want to dismiss the popup when clicking outside of it
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Set the unfold animation when showing the popup
        popupWindow.contentView.startAnimation(unfoldAnimation)

        // Set the fold animation when dismissing the popup
        popupWindow.setOnDismissListener {
            popupView.startAnimation(foldAnimation)
        }

        // Show the PopupWindow below the anchor view (menu item)
        popupWindow.showAsDropDown(anchorView)
    }
}