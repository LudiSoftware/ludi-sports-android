package io.usys.report.ui.ludi.roster

import android.annotation.SuppressLint
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
import io.usys.report.ui.ludi.roster.config.RosterConfig
import io.usys.report.ui.ludi.roster.config.RosterType
import io.usys.report.ui.ludi.roster.config.rosterViewFactory
import io.usys.report.ui.views.hideLudiActionBar
import io.usys.report.ui.views.menus.LudiPopupMenu
import io.usys.report.ui.views.listAdapters.rosterLiveList.RosterListLiveAdapter
import io.usys.report.ui.views.showLudiActionBar
import io.usys.report.ui.views.spinners.LudiSpinnerAdapter
import io.usys.report.utils.*
import io.usys.report.utils.views.attachViewsToOnClickListener
import io.usys.report.utils.views.makeGone
import io.usys.report.utils.views.makeVisible
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

    @SuppressLint("ClickableViewAccessibility")
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
                if (it.config.selectedTooMany()) {
                    imgBtnOne.setBackgroundResource(android.R.drawable.ic_delete)
                    layoutOne.attachViewsToOnClickListener(imgBtnOne) {
                        layoutOne.wiggleOnce()
                    }
                } else {
                    if (it.config.selectedNotEnough()) {
                        // todo: show a message that to verify user is okay with a short roster
                    } else {
                        imgBtnOne.setBackgroundResource(R.drawable.fui_ic_check_circle_black_128dp)
                        layoutOne.attachViewsToOnClickListener(imgBtnOne) {
                            realmInstance?.tryoutChangeModeToPendingRoster(teamId, syncFire = true)
                        }
                    }
                }
            }
        })

        realmInstance?.syncTeamDataFromFirebase(teamId)

        setupRosterIds()
        setupRosterTypeSpinner()
        setupRosterSizeSpinner()

        _binding?.rosterBuilderLudiRosterView?.ludiRosterView?.onFlingListener = {
            toFormationBuilder(teamId)
        }
        return rootView
    }

    /** Spinners **/
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

    private fun toggleTryoutTools() {
        if (rosterType != RosterType.OFFICIAL.type) {
            _binding?.rosterBuilderLudiSubTxt?.makeVisible()
            _binding?.rosterBuilderLudiSpinRosterSizeText?.makeVisible()
            _binding?.rosterBuilderLudiSpinRosterLimit?.makeVisible()
            showLudiActionBar()
        } else {
            _binding?.rosterBuilderLudiSubTxt?.makeGone()
            _binding?.rosterBuilderLudiSpinRosterSizeText?.makeGone()
            _binding?.rosterBuilderLudiSpinRosterLimit?.makeGone()
            hideLudiActionBar()
        }
    }


    /** Master Roster Setup! **/
    private fun setupRosterTypeTitle() {
        val rosterTitle = "Roster: ${rosterType.capitalizeFirstChar()} (${adapter?.itemCount})"
        _binding?.rosterBuilderLudiTxtRosterType?.text = rosterTitle
    }

    private fun setupRosterIds() {
        realmInstance?.rosterViewFactory(teamId)?.let {
            // Roster Holder
            rosterIds = it.first
            // Roster Config
            rosterConfig = it.second
            rosterConfig.apply {
                this.rosterId = rosterIds[RosterType.OFFICIAL.type] ?: "unknown"
                this.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
                this.parentFragment = this@RosterBuilderFragment
                this.textViewOne = _binding?.rosterBuilderLudiSubTxt
            }
            adapter = RosterListLiveAdapter(rosterConfig)
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
        rosterConfig.recyclerView = _binding?.rosterBuilderLudiRosterView?.root!!
        currentRosterId?.let {
            when (rosterType) {
                RosterType.OFFICIAL.type -> {
                    setupRosterSizeSpinner()
                    detachLudiMenu()
                    adapter?.setupOfficialRoster()
                }
                RosterType.TRYOUT.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    adapter?.setupTryoutRoster()
                }
                RosterType.SELECTED.type -> {
                    setupRosterSizeSpinner()
                    attachLudiMenu()
                    adapter?.setupSelectionRoster(0)
                }
                else -> {
                    if (rosterType.startsWith(RosterType.SELECTED.type)) {
                        val splitNumber = rosterType.extractSelectionSplitNumber() ?: 0
                        setupRosterSizeSpinner()
                        attachLudiMenu()
                        adapter?.setupSelectionRoster(splitNumber)
                        log("setupRosterList: $rosterType")
                    } else
                        log("No Roster Found For: $rosterType")

                }
            }
        }
        setupRosterTypeTitle()
    }

}
fun generateNumberStrings(): List<String> {
    return (1..100).map { it.toString() }
}