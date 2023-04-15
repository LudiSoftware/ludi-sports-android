package io.usys.report.ui.ludi.roster

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import io.usys.report.R
import io.usys.report.databinding.RosterBuilderFragmentBinding
import io.usys.report.providers.*
import io.usys.report.realm.*
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.ui.fragments.*
import io.usys.report.ui.views.menus.LudiPopupMenu
import io.usys.report.ui.views.listAdapters.rosterLiveList.RosterListLiveAdapter
import io.usys.report.ui.views.spinners.LudiSpinnerAdapter
import io.usys.report.utils.*
import io.usys.report.utils.views.onItemSelected
import io.usys.report.utils.views.setupRosterTypeSpinner
import io.usys.report.utils.views.wiggleOnce

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterBuilderFragment : YsrFragment() {

    private var ludiPopupMenu: LudiPopupMenu? = null
    private var ludiMenuIsAttached = false
    private var _binding: RosterBuilderFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var rosterConfig: RosterConfig
    var adapter: RosterListLiveAdapter? = null

    var teamId: String = "unknown"

    var rosterType: String = RosterType.OFFICIAL.type
    var title: String = "No Roster Found!"
    var rosterIds = mutableMapOf<String,String>()
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
            rosterConfig = RosterConfig(teamId)
        }

        ludiPopupMenu = LudiPopupMenu(this, R.layout.menu_roster_builder, action = { view, _ ->
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
                        realmInstance?.tryoutChangeModeToPendingRoster(teamId, syncFire = true)
                    }
                }
            }
        })

        realmInstance?.syncTeamDataFromFirebase(teamId)

        setupRosterIds()
        setupRosterTypeSpinner()
        setupRosterSizeSpinner()
        return rootView
    }



    private fun setupRosterTypeSpinner() {
        _binding?.rosterBuilderLudiSpinRosterType?.setupRosterTypeSpinner(rosterIds) { _, item ->
            rosterIds.forEach { (key, value) ->
                if (key == item) {
                    currentRosterId = value
                }
            }
            rosterType = item
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
        val rosterSize = realmInstance?.rosterSessionById(currentRosterId)?.rosterSizeLimit ?: 20
        _binding?.rosterBuilderLudiSpinRosterLimit?.setSelection(rosterSize - 1)
        // ROSTER SELECTION
        _binding?.rosterBuilderLudiSpinRosterLimit?.onItemSelected { parent, _, position, _ ->
            position?.let {
                val selectedEntry = parent?.getItemAtPosition(it)
                adapter?.updateRosterSizeLimit(selectedEntry.toString().toInt())
            }
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
        }
        adapter = RosterListLiveAdapter(rosterConfig)
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
        rosterConfig.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
        currentRosterId?.let { rosterId ->
            when (rosterType) {
                RosterType.TRYOUT.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    adapter?.setupTryoutRoster()
                }
                RosterType.SELECTED.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    adapter?.setupSelectionRoster()
                }
                else -> {
                    setupRosterSizeSpinner()
                    detachLudiMenu()
                    adapter?.setupOfficialRoster()
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