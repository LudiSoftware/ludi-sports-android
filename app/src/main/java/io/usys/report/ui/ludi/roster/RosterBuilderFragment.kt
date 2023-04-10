package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import io.usys.report.R
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.ui.fragments.*
import io.usys.report.ui.ludi.team.TeamProvider
import io.usys.report.ui.views.LudiPopupMenu
import io.usys.report.ui.views.listAdapters.RosterListLiveAdapter
import io.usys.report.utils.*
import io.usys.report.utils.views.onItemSelected
import io.usys.report.utils.views.wiggleOnce

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    private var ludiPopupMenu: LudiPopupMenu? = null
    var ludiMenuIsAttached = false
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    var rosterConfig = RosterConfig()
    var adapter: RosterListLiveAdapter? = null

    var teamProvider: TeamProvider? = null
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

        ludiPopupMenu = LudiPopupMenu(this, R.layout.menu_roster_builder_popup, action = { view, _ ->

            val layoutOne = view.findViewById<LinearLayout>(R.id.menuRosterBuilderBtnOneLayout)
            val imgBtnOne = view.findViewById<ImageButton>(R.id.menuRosterBuilderBtnOneImgBtn)


            adapter?.let {
                if (it.areTooManySelected()) {
                    imgBtnOne.setBackgroundResource(android.R.drawable.ic_delete)
                    layoutOne.attachViewsToOnClickListener(imgBtnOne) {
                        layoutOne.wiggleOnce()
                    }
                } else {
                    imgBtnOne.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp)
                    layoutOne.attachViewsToOnClickListener(imgBtnOne) {
                        realmInstance?.findRosterById(currentRosterId)?.let { roster ->
                            realmInstance?.safeWrite {
                                roster.status = RosterStatus.PENDING.status
                            }
                        }
                        realmInstance?.findTeamById(teamId)?.let { team ->
                            realmInstance?.safeWrite {
                                team.mode = TeamStatus.PENDING.status
                            }
                        }
                        currentRosterId?.let { it1 ->
                            teamProvider?.pushOfficialRosterToFirebase()
                        }
                    }
                }
            }
        })

        teamProvider = TeamProvider(teamId)

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
        _binding?.rosterBuilderLudiSpinRosterType?.onItemSelected { parent, _, position, _ ->
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
            adapter?.config?.updateRosterSizeLimit(realmInstance, teamId, selectedEntry.toString().toInt())
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
            this.parentFragment = this@RosterBuilderFragment
            this.layout = R.layout.card_player_medium_grid
            this.type = FireTypes.PLAYERS
            this.size = "medium_grid"
            this.setRosterSizeLimit(realmInstance, teamId)
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
        detachLudiMenu()
    }

    private fun attachLudiMenu() {
        if (ludiMenuIsAttached) return
        ludiPopupMenu?.let {
            requireActivity().addMenuProvider(it)
            ludiMenuIsAttached = true
        }
    }
    private fun detachLudiMenu() {
        ludiPopupMenu?.let {
            requireActivity().removeMenuProvider(it)
            ludiMenuIsAttached = false
        }
    }

    private fun setupRosterList() {
        adapter?.disableAndClearRosterList()
        rosterConfig.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
        currentRosterId?.let { rosterId ->
            when (rosterType) {
                RosterType.TRYOUT.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.TRYOUT.type
                    rosterConfig.touchEnabled = true
                    rosterConfig.playerFilters.clear()
                    adapter = RosterListLiveAdapter(rosterConfig)
                }
                RosterType.SELECTED.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.SELECTED.type
                    rosterConfig.touchEnabled = true
                    adapter = RosterListLiveAdapter(rosterConfig)
                    adapter?.filterByStatusSelected()
                }
                else -> {
                    toggleTryoutTools()
                    detachLudiMenu()
                    rosterConfig.rosterId = rosterId
                    rosterConfig.mode = RosterType.OFFICIAL.type
                    rosterConfig.touchEnabled = false
                    rosterConfig.playerFilters.clear()
                    adapter = RosterListLiveAdapter(rosterConfig)
                }
            }
        }
        setupRosterTypeTitle()
    }

}


fun generateNumbers(): List<Int> {
    return (1..100).toList()
}
fun generateNumberStrings(): List<String> {
    return (1..100).map { it.toString() }
}