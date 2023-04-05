package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.model.PLAYER_STATUS_ACCEPTED
import io.usys.report.realm.model.Roster
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.formationbuilder.setPlayerFormationBackgroundColor
import io.usys.report.ui.ludi.player.positionMap
import io.usys.report.ui.ludi.player.setupPlayerPositionSpinner
import io.usys.report.utils.capitalizeFirstChar
import io.usys.report.utils.log
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible
import io.usys.report.utils.views.attachAndInflatePopMenu
import io.usys.report.utils.views.onItemSelected
import io.usys.report.utils.views.wiggleOnce

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var rosterConfig = RosterConfig()
    var adapter: RosterListAdapter? = null

    var teamId: String = "unknown"

    var rosterType: String = RosterType.OFFICIAL.type
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
        setupRosterSizeSpinner()
        return rootView
    }



    private fun setupRosterTypeSpinner() {
        rosterEntries = rosterIds.keys.toMutableList()
        val spinnerAdapter = LudiSpinnerAdapter(requireContext(), rosterEntries)
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
            setupRosterList()
        }

    }

    private fun toggleTryoutTools() {
        if (rosterType != RosterType.OFFICIAL.type) {
            _binding?.rosterBuilderLudiSubTxt?.makeVisible()
            _binding?.rosterBuilderLudiSpinRosterSizeText?.makeVisible()
            _binding?.rosterBuilderLudiSpinRosterLimit?.makeVisible()
        } else {
            _binding?.rosterBuilderLudiSubTxt?.makeGone()
            _binding?.rosterBuilderLudiSpinRosterSizeText?.makeGone()
            _binding?.rosterBuilderLudiSpinRosterLimit?.makeGone()
        }
    }

    private fun setupRosterSizeSpinner() {
        toggleTryoutTools()
        val selectedCounts = generateNumberStrings()
        val spinnerAdapter = LudiSpinnerAdapter(requireContext(), selectedCounts)
        _binding?.rosterBuilderLudiSpinRosterLimit?.adapter = spinnerAdapter
        _binding?.rosterBuilderLudiSpinRosterLimit?.setSelection(rosterConfig.rosterSizeLimit - 1)
        // ROSTER SELECTION
        _binding?.rosterBuilderLudiSpinRosterLimit?.onItemSelected { parent, _, position, _ ->
            val selectedEntry = parent.getItemAtPosition(position)
            adapter?.config?.rosterSizeLimit = selectedEntry.toString().toInt()
            adapter?.refresh()
        }
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
            rosterIds[RosterType.OFFICIAL.type] = rosterId
            currentRosterId = rosterId
            _binding?.rosterBuilderLudiTxtRosterType?.text = "Official Roster"
        }
        // TryOut Roster
        realmInstance?.findTryOutIdByTeamId(teamId) { tryoutId ->
            realmInstance?.findTryOutById(tryoutId)?.let { to ->
                to.rosterId?.let {
                    // tryout roster
                    rosterIds[RosterType.TRYOUT.type] = it
                    rosterIds[RosterType.SELECTED.type] = it
                }
                this.tryoutId = tryoutId
            }
        }

        rosterConfig.apply {
            this.rosterId = currentRosterId
            this.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
            this.layout = R.layout.card_player_medium_grid
            this.type = FireTypes.PLAYERS
            this.size = "medium_grid"
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun setupRosterList() {
        adapter?.disableAndClearRosterList()
        currentRosterId?.let { rosterId ->
            rosterConfig.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
            when (rosterType) {
                RosterType.TRYOUT.type -> {
                    setupRosterSizeSpinner()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.TRYOUT.type
                    rosterConfig.touchEnabled = true
                    rosterConfig.playerFilters.clear()
                    adapter = RosterListAdapter(rosterConfig)
                }
                RosterType.SELECTED.type -> {
                    setupRosterSizeSpinner()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.SELECTED.type
                    rosterConfig.touchEnabled = true
                    adapter = RosterListAdapter(rosterConfig)
                    adapter?.filterByStatusSelected()
                }
                else -> {
                    toggleTryoutTools()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.OFFICIAL.type
                    rosterConfig.touchEnabled = false
                    rosterConfig.playerFilters.clear()
                    adapter = RosterListAdapter(rosterConfig)
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
                setupRosterList()
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
class PositionPickerMenuPopupProvider(private val fragment: Fragment) : MenuProvider {
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
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.menu_player_position_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Find and set up the Spinner
        val positionSpinner = popupView.findViewById<Spinner>(R.id.menuPlayerPositionSpinner)
        positionSpinner.setupPlayerPositionSpinner(fragment.requireContext())
        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

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