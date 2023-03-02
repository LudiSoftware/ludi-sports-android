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
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUserId
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
    var formationRelativeLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null

    // Formation Session
    var formationSession: FormationSession? = null
    // List of players
//    var players: RealmList<PlayerRef>? = null
//    var rosterList = mutableListOf<PlayerRef>()
    // Player Icons on the field
    var formationPlayerList = mutableListOf<PlayerRef>()
    var formationViewList = mutableListOf<View>()
    var formationLayouts = mutableListOf(R.drawable.soccer_field)

    var dragListener: View.OnDragListener? = null
    var onItemDragged: ((start: Int, end: Int) -> Unit)? = null
    var team: Team? = null
    var teamId: String? = null
    var rosterId: String? = null

    var constraintLayout: ConstraintLayout? = null
    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        teamId = realmIdArg
        realmInstance?.safeUserId { itUserId ->
            formationSession = realmInstance?.findByField("id", itUserId)
            if (formationSession == null) {
                realmInstance?.executeTransaction { itRealm ->
                    formationSession = itRealm.createObject(FormationSession::class.java, itUserId)
                    formationSession?.teamId = teamId
                    formationSession?.let {
                        itRealm.insertOrUpdate(it)
                    }
                }
            }
        }

        //Base Team Data
        loadRoster()
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

    private fun saveRosterIdToFormationSession(rosterId: String?) {
        if (rosterId == null) return
        formationSession?.let { formationSession ->
            realmInstance?.executeTransaction {
                formationSession.rosterId = rosterId
                it.insertOrUpdate(formationSession)
            }
        }
    }
    private fun saveRosterListToFormationSession(rosterList: RealmList<PlayerRef>?) {
        if (rosterList == null) return
        formationSession?.let { formationSession ->
            realmInstance?.executeTransaction {
                formationSession.rosterList = rosterList
                it.insertOrUpdate(formationSession)
            }
        }
    }
    private fun loadRoster(reload:Boolean=false) {

        val rosterList = formationSession?.rosterList
        if (rosterList.isNullOrEmpty()) {
            teamId?.let { teamId ->
                val rosterId = realmInstance?.getRosterIdForTeamId(teamId)
                saveRosterIdToFormationSession(rosterId)
                rosterId?.let { rosterId ->
                    realmInstance?.findRosterById(rosterId)?.let { roster ->
                        saveRosterListToFormationSession(roster.players)
                        setupRosterList()
                    }
                }
            }
            return
        }
        if (reload) {
            teamId?.let { teamId ->
                val rosterId = realmInstance?.getRosterIdForTeamId(teamId)
                saveRosterIdToFormationSession(rosterId)
                rosterId?.let { rosterId ->
                    realmInstance?.findRosterById(rosterId)?.let { roster ->
                        saveRosterListToFormationSession(roster.players)
                        setupRosterList()
                    }
                }
            }
        }
    }

    private fun resetFormationLayout() {
        realmInstance?.executeTransaction { itRealm ->
            formationSession?.let { fs ->
                fs.formationList?.forEach { pf ->
                    fs.rosterList?.add(pf)
                }
                fs.formationList?.clear()
                itRealm.insertOrUpdate(fs)
            }
        }
        formationRelativeLayout?.removeAllViews()
        adapter?.notifyDataSetChanged()
    }

    private fun getBackgroundDrawable(@RawRes drawableReference: Int): Drawable? {
        return getDrawable(requireContext(), drawableReference)
    }

    /**
     * Display Functions
     *
     */
    private fun setupDisplay() {
        activity?.window?.let {
            rosterListRecyclerView = rootView.findViewById(R.id.ysrTORecycler)
            formationRelativeLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)
            setupFloatingActionMenu()
            setupRosterList()
            setupFormationList()
        }
    }
    private fun setupRosterList() {
        loadRoster(reload = false)
        onItemDragged = { start, end ->
            log("onItemDragged: $start, $end")
        }
        formationSession?.let {
            it.rosterList?.let { rosterList ->
                adapter = RosterFormationListAdapter(realmInstance, requireActivity())
                rosterListRecyclerView?.layoutManager = gridLayoutManager(requireContext())
                rosterListRecyclerView?.adapter = adapter
            }
        }
    }
    private fun setupFormationList() {
        createOnDragListener()
        adapter?.let { itAdapter ->
            val itemTouchHelperCallback = RosterFormationTouchHelperCallback(itAdapter)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(rosterListRecyclerView)
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
                    resetFormationLayout()
                }
                else -> {
                    log("Unknown Touch")
                }
            }
        }

        val gestureDetector = LudiFreeFormGestureDetector(requireContext()) { event ->
            // Show the PopupMenu when the FloatingActionButton is single-tapped
            fabMenu.wiggleShort()
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
                        realmInstance?.findPlayerRefById(playerId)?.let {
                            tempPlayer = it
                            formationPlayerList.add(it)
                        }
                        val layoutParams = getRelativeLayoutParams()
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        layoutParams.width = 300
                        layoutParams.height = 75
                        if (!tempPlayer.pointX.isNullOrEmpty() || tempPlayer.pointX != 0) {
                            layoutParams.leftMargin = tempPlayer.pointX!!
                        }
                        if (!tempPlayer.pointY.isNullOrEmpty() || tempPlayer.pointY != 0) {
                            layoutParams.topMargin = tempPlayer.pointY!!
                        }

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
                        // PopMenu
                        val playerPopMenu = tempView.attachAndInflatePopMenu(R.menu.floating_player_menu) { menuItem ->
                            // Handle menu item click events
                            // todo: events to handle:
                            //  - save formation -> order and (x,y) coordinates
                            //  - if in tryout mode, submit formation as roster.
                            //  - Change background image.
                            //  - Reset Formation.
                            when (menuItem.itemId) {
                                R.id.menu_player_change_teams -> {
                                    // Do something
                                    log("menu_player_change_teams")
                                }
                                R.id.menu_player_add_note -> {
                                    // Do something
                                    log("menu_player_add_note")
                                }
                                else -> {
                                    log("Unknown Touch")
                                }
                            }
                        }
                        //On Click
                        val onTap: () -> Unit = {
                            popPlayerProfileDialog(requireActivity(), playerId).show()
                        }
                        val onLongPress: () -> Unit = {
                            log("Double Tap")
                            tempView.wiggleOnce()
                            playerPopMenu.show()
                        }

                        // Gestures
                        tempView.onGestureDetectorRosterFormation(
                            width = 300,
                            height = 75,
                            playerId=playerId,
                            onSingleTapUp = onTap,
                            onLongPress = onLongPress
                        )

                        // Add to FormationLayout
                        formationViewList.add(tempView)
                        formationRelativeLayout?.addView(tempView)
                    }
                    true
                }
                else -> {
                    true
                }
            }
        }
        formationRelativeLayout?.setOnDragListener(dragListener)
    }


}

