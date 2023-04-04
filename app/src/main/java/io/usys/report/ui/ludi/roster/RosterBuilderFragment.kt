package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.TEAM_MODE_IN_SEASON
import io.usys.report.realm.model.TEAM_MODE_TRYOUT
import io.usys.report.ui.fragments.*
import io.usys.report.utils.capitalizeFirstChar
import io.usys.report.utils.log
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible
import io.usys.report.utils.views.onItemSelected

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    var isFirst = true
    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var rosterConfig = RosterLayoutConfig()
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
        setupRosterTypeSpinner()
        setupSelectedCountSpinner()
        return rootView
    }

    private fun setupRosterTypeSpinner() {
        rosterEntries = rosterIds.keys.toMutableList()
        val spinnerAdapter = RosterSpinnerAdapter(requireContext(), rosterEntries)
        _binding?.rosterBuilderLudiSpinRosterType?.adapter = spinnerAdapter

        // ROSTER SELECTION
        _binding?.rosterBuilderLudiSpinRosterType?.onItemSelected { parent, view, position, id ->
            val selectedEntry = parent.getItemAtPosition(position)
            rosterIds.forEach { (key, value) ->
                if (key == selectedEntry) {
                    currentRosterId = value
                }
            }
            rosterType = selectedEntry.toString()
            _binding?.rosterBuilderLudiTxtRosterType?.text = rosterType
            setupCurrentRosterDisplay()
        }

    }

    private fun setupSelectedCountSpinner() {
        _binding?.rosterBuilderLudiSpinRosterLimit?.makeVisible()
        val selectedCounts = generateNumberStrings()
        val spinnerAdapter = RosterSpinnerAdapter(requireContext(), selectedCounts)
        _binding?.rosterBuilderLudiSpinRosterLimit?.adapter = spinnerAdapter
        _binding?.rosterBuilderLudiSpinRosterLimit?.setSelection(19)
        // ROSTER SELECTION
        _binding?.rosterBuilderLudiSpinRosterLimit?.onItemSelected { parent, _, position, _ ->
            val selectedEntry = parent.getItemAtPosition(position)
            adapter?.config?.selectedCount = selectedEntry.toString().toInt()
            adapter?.reload()
        }
    }
    private fun hideSelectedCountSpinner() {
        _binding?.rosterBuilderLudiSpinRosterLimit?.makeGone()
    }

    /** Master Roster Setup! **/
    private fun setupRosterTypeTitle() {
        val rosterTitle = "Roster: ${rosterType.capitalizeFirstChar()} (${adapter?.itemCount})"
        _binding?.rosterBuilderLudiTxtRosterType?.text = rosterTitle
    }

    private fun setupRosterIds() {
        // Official Roster
        realmInstance?.findRosterIdByTeamId(teamId)?.let { rosterId ->
            // official roster
            rosterIds["official"] = rosterId
            currentRosterId = rosterId
            _binding?.rosterBuilderLudiTxtRosterType?.text = "Official Roster"
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

    private fun setupCurrentRosterDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            //TODO: setup the click listener for the player
            log("Clicked on player: ${realmObject}")
            toPlayerProfile(teamId = teamId, playerId = (realmObject as PlayerRef).id ?: "unknown")
        }
        setupRosterListWithTouch()
    }

    private fun setupRosterListWithTouch() {
        adapter?.disableAndClearRosterList()
        currentRosterId?.let { rosterId ->
            rosterConfig.apply {
                this.rosterId = currentRosterId
                this.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
                this.itemClickListener = onClickReturnViewRealmObject
                this.layout = R.layout.card_player_medium_grid
                this.type = FireTypes.PLAYERS
                this.size = "medium_grid"
            }
            when (rosterType) {
                "tryout" -> {
                    rosterConfig.mode = TEAM_MODE_TRYOUT
                    adapter = RosterListAdapter(rosterConfig)
                }
                "selected" -> {
                    rosterConfig.mode = TEAM_MODE_TRYOUT
                    adapter = RosterListAdapter(rosterConfig)
                    adapter?.filterByStatusSelected()
                }
                else -> {
                    rosterConfig.mode = TEAM_MODE_IN_SEASON
                    adapter = RosterListAdapter(rosterConfig)
                    adapter?.disableTouch()
                }
            }
        }
        setupRosterTypeTitle()
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


fun generateNumbers(): List<Int> {
    return (1..100).toList()
}
fun generateNumberStrings(): List<String> {
    return (1..100).map { it.toString() }
}