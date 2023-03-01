package io.usys.report.ui.ludi.formationbuilder

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.usys.report.R
import io.usys.report.realm.findByField
import io.usys.report.realm.gridLayoutManager
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdFragment
import io.usys.report.ui.ludi.player.popPlayerProfileDialog
import io.usys.report.ui.gestures.LudiFreeFormGestureDetector
import io.usys.report.utils.*
import io.usys.report.utils.views.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : LudiStringIdFragment() {

    companion object {
        const val TAG = "Formation"
        fun newInstance(): RosterFormationFragment {
            return RosterFormationFragment()
        }
    }

    var adapter: RosterFormationListAdapter? = null
    var formationLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null

    // List of players
    var rosterList = mutableListOf<PlayerRef>()
    // Player Icons on the field
    var formationPlayerList = mutableListOf<PlayerRef>()
    var formationViewList = mutableListOf<View>()
    var formationLayouts = mutableListOf(R.drawable.soccer_field)

    var dragListener: View.OnDragListener? = null
    var onItemDragged: ((start: Int, end: Int) -> Unit)? = null
    var team: Team? = null
    var teamId: String? = null

    var constraintLayout: ConstraintLayout? = null
    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        //Base Team Data
        teamId = realmIdArg
        loadTeamFromRealm()
        //Hiding the action bar
        hideLudiActionBar()
        //Hiding the bottom navigation bar
        hideLudiNavView()
        if (container == null) {
            val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.teamViewPager)
            rootView = teamContainer?.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        } else {
            rootView = container.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        }
        constraintLayout = rootView.findViewById(R.id.TryoutTestFragment)
        setupDisplay()
        return rootView
    }

    /**
     * Base Functions
     *
     */
    private fun loadTeamFromRealm() {
        team = realmInstance?.findByField("id", teamId!!)
    }

    private fun reloadRoster() {
        val roster = team?.roster
        roster?.players?.forEach { rosterList.add(it) }
    }

    private fun resetFormationLayout() {
        formationLayout?.removeAllViews()
    }

    private fun getBackgroundDrawable(@RawRes drawableReference: Int): Drawable? {
        return getDrawable(requireContext(), drawableReference)
    }

    /**
     * Display Functions
     *
     */
    private fun setupDisplay() {

        setupFloatingActionMenu()
        activity?.window?.let {
            formationLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)

            rosterListRecyclerView = rootView.findViewById(R.id.ysrTORecycler)

            onItemDragged = { start, end ->
//                val item = formationList[start]
//                formationList.removeAt(start)
//                formationList.add(end, item)
            }

            reloadRoster()

            adapter = RosterFormationListAdapter(rosterList, onItemDragged!!, requireActivity())
            rosterListRecyclerView?.layoutManager = gridLayoutManager(requireContext())
            rosterListRecyclerView?.adapter = adapter

            adapter?.let { itAdapter ->
                val itemTouchHelperCallback = RosterFormationTouchHelperCallback(itAdapter)
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(rosterListRecyclerView)
                createOnDragListener()
            }

        }

    }

    /**
     * Orientation Change Functions
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.inflater?.inflate(R.layout.fragment_list_formations_landscape, container, false)
            setupDisplay()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.inflater?.inflate(R.layout.fragment_list_formations_portrait, container, false)
            setupDisplay()
        }
    }

    /**
     * Floating Action Menu Functions
     *
     */
    private fun setupFloatingActionMenu() {
        val fabMenu = rootView.findViewById<FloatingActionButton>(R.id.tryoutFormationFloatingActionButton)
        val popupMenu = fabMenu.attachAndInflatePopMenu(R.menu.floating_formation_menu) { menuItem ->
            // Handle menu item click events
            // todo: events to handle:
            //  - save formation -> order and (x,y) coordinates
            //  - if in tryout mode, submit formation as roster.
            //  - Change background image.
            //  - Reset Formation.
            when (menuItem.itemId) {
                R.id.menu_save_formation -> {
                    // Do something
                    log("Save Formation")
                }
                R.id.menu_submit_roster -> {
                    // Do something
                }
                R.id.menu_reset -> {
                    // Do something
                }
                else -> {
                    log("Unknown Touch")
                }
            }
        }

        val gestureDetector = LudiFreeFormGestureDetector(requireContext()) { event ->
            // Show the PopupMenu when the FloatingActionButton is single-tapped
            popupMenu.show()
        }
        fabMenu.setOnTouchListener(gestureDetector)
    }

    /**
     * Drag Listener for when a player is dragged onto the soccer field
     */
    private fun createOnDragListener() {
        dragListener = View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Do nothing
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    /**
                     * TODO:
                     *      - add long press listener for a player specific menu
                     *          - menu options: remove player.
                     *
                     *     - show:
                     *          - player name, tryout tag, position number.
                     */
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        val playerId = clipData.getItemAt(0).text.toString()
                        adapter?.removePlayer(playerId)
                        var tempPlayer = PlayerRef()
                        team?.getPlayerFromRosterNoThread(playerId)?.let {
                            tempPlayer = it
                            formationPlayerList.add(it)
                        }

                        val layoutParams = getRelativeLayoutParams()
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        layoutParams.width = 300
                        layoutParams.height = 75

                        val tempView = inflateView(requireContext(), R.layout.card_player_tiny)
                        val playerName = tempView.findViewById<TextView>(R.id.cardPlayerTinyTxtName)
                        val playerIcon = tempView.findViewById<ImageView>(R.id.cardPlayerTinyImgProfile)
                        tempView.layoutParams = layoutParams
                        tempView.tag = tempPlayer.id
                        // Bind Data
                        playerName.text = tempPlayer.name
                        tempPlayer.imgUrl?.let {
                            playerIcon.loadUriIntoImgView(it)
                        }
                        //On Click
                        val onTap: () -> Unit = {
                            popPlayerProfileDialog(requireActivity(), playerId).show()
                        }
                        // Gestures
                        tempView.onGestureDetectorRosterFormation(width = 300, height = 75, onSingleTapUp = onTap)
                        // Add to FormationLayout
                        formationViewList.add(tempView)
                        formationLayout?.addView(tempView)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                else -> false
            }
        }
        formationLayout?.setOnDragListener(dragListener)
    }


}

