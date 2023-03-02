package io.usys.report.ui.ludi.formationbuilder

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmResults
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
    var floatingMenuButton: FloatingActionButton? = null
    var floatingPopMenu: PopupMenu? = null
    var formationSession: FormationSession? = null
    // Player Icons on the field
//    var formationPlayerList = mutableListOf<PlayerRef>()
    var formationViewList = mutableListOf<View>()
//    var formationLayouts = mutableListOf(R.drawable.soccer_field)
    // Player Formation
    var playerPopMenuView: PopupMenu? = null
    var onTap: (() -> Unit)? = null
    var onLongPress: (() -> Unit)? = null

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
                    formationSession?.currentLayout = R.drawable.soccer_field
                    formationSession?.layoutList = RealmList(R.drawable.soccer_field)
                    formationSession?.deckListIds = RealmList()
                    formationSession?.formationListIds = RealmList()
                    formationSession?.let {
                        itRealm.insertOrUpdate(it)
                    }
                }
            }
        }
        setupRosterRealmListener()
        //Base Team Data
        loadRoster(reload = false)
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

    private fun setupRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> { itResults ->
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                itResults.find { it.id == rosterId }?.let { roster ->
                    safeUpdateRoster(roster)
                    setupRosterList()
                }
            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }

    private fun saveRosterIdToFormationSession(rosterId: String?) {
        if (rosterId == null) return
        formationSession?.let { formationSession ->
            realmInstance?.executeTransaction {
                formationSession.rosterId = rosterId
                it.insertOrUpdate(formationSession)
            }
        }
    }

    private fun safeUpdateRoster(newRoster: Roster?) {
        if (newRoster == null) return
        formationSession?.let { fs ->

            if (fs.roster.isNullOrEmpty()) {
                realmInstance?.executeTransaction {
                    fs.roster = newRoster
                    for (player in newRoster.players!!) {
                        fs.deckListIds?.add(player.id)
                    }
                }
                return
            }

            newRoster.players?.forEach { newPlayer ->
                val tempPlayers = fs.roster?.players
                tempPlayers?.let {
                    if (tempPlayers.contains(newPlayer)) {
                        return@forEach
                    }
                    realmInstance?.safeWrite { itRealm ->
                        tempPlayers.add(newPlayer)
                        fs.deckListIds?.add(newPlayer.id)
                        itRealm.insertOrUpdate(fs)
                    }
                }
            }
        }
    }

    private fun loadRoster(reload:Boolean=false) {
        val roster = formationSession?.roster
        if (roster.isNullOrEmpty()) {
            teamId?.let { teamId ->
                rosterId = realmInstance?.getRosterIdForTeamId(teamId)
                saveRosterIdToFormationSession(rosterId)
                rosterId?.let { rosterId ->
                    realmInstance?.findRosterById(rosterId)?.let { roster ->
                        safeUpdateRoster(roster)
                        setupRosterList()
                    }
                }
            }
            return
        }
        if (reload) {
            teamId?.let { teamId ->
                rosterId = realmInstance?.getRosterIdForTeamId(teamId)
                saveRosterIdToFormationSession(rosterId)
                rosterId?.let { rosterId ->
                    realmInstance?.findRosterById(rosterId)?.let { roster ->
                        safeUpdateRoster(roster)
                        setupRosterList()
                    }
                }
            }
        }
    }

    private fun resetFormationLayout() {
        //todo: put this in the adapter and call it there.
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
        onItemDragged = { start, end ->
            log("onItemDragged: $start, $end")
        }
        formationSession?.let {
            it.deckListIds?.let { rosterList ->
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
        floatingMenuButton = rootView.findViewById<FloatingActionButton>(R.id.tryoutFormationFloatingActionButton)
        floatingPopMenu = floatingMenuButton?.attachAndInflatePopMenu(R.menu.floating_formation_menu) { menuItem, parentView ->
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
                R.id.menu_formation_team_color_toggle -> {
                    realmInstance?.executeTransaction {
                        formationSession?.let {
                            if (it.teamColorsAreOn) {
                                menuItem.title = "Turn ON Team Colors"
                                it.teamColorsAreOn = false
                            } else {
                                menuItem.title = "Turn OFF Team Colors"
                                it.teamColorsAreOn = true
                            }
                        }
                    }
                }
                else -> {
                    log("Unknown Touch")
                }
            }
        }

        val gestureDetector = LudiFreeFormGestureDetector(requireContext()) { event ->
            // Show the PopupMenu when the FloatingActionButton is single-tapped
            floatingMenuButton?.wiggleShort()
            floatingPopMenu?.show()
        }
        floatingMenuButton?.setOnTouchListener(gestureDetector)
    }

    /**
     * Drag Listener for when a player is dragged onto the soccer field
     */
    private fun createOnDragListener() {
        dragListener = View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        val playerId = clipData.getItemAt(0).text.toString()
                        adapter?.movePlayerToField(playerId)
                        addPlayerToFormation(playerId)
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

    // 1 Master
    private fun addPlayerToFormation(playerId: String, loadingFromSession: Boolean = false) {
        val playerRefViewItem = inflateView(requireContext(), R.layout.card_player_tiny)
        val playerName = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerTinyTxtName)
        val playerIcon = playerRefViewItem.findViewById<ImageView>(R.id.cardPlayerTinyImgProfile)

        //Prepare PlayerView from Drag/Drop
        safePlayerFromRoster(playerId) { newPlayerRef ->

            if (!loadingFromSession) {
                realmInstance?.safeWrite {
                    formationSession?.let {
                        it.formationListIds?.add(playerId)
                    }
                }
            }

            val layoutParams = preparePlayerLayoutParamsForFormation(newPlayerRef)
            playerRefViewItem.layoutParams = layoutParams
            playerRefViewItem.tag = newPlayerRef.id
            // Bind Data
            playerName.text = newPlayerRef.name
            newPlayerRef.imgUrl?.let {
                playerIcon.loadUriIntoImgView(it)
            }
            newPlayerRef.color?.let {
                playerRefViewItem.setPlayerTeamBackgroundColor(it)
            }
            playerRefViewItem.setupOnTapListeners(playerId)
            playerRefViewItem.setupPlayerPopupMenu()
            // Gestures
            playerRefViewItem.onGestureDetectorRosterFormation(
                width = 300,
                height = 75,
                playerId=playerId,
                onSingleTapUp = onTap,
                onLongPress = onLongPress
            )
            // Add to FormationLayout
            formationViewList.add(playerRefViewItem)
            formationRelativeLayout?.addView(playerRefViewItem)
        }

    }

    private inline fun findPlayerViewInFormation(playerId: String, block: (View) -> Unit) {
        formationViewList.forEach { playerView ->
            if (playerView.tag == playerId) {
                block(playerView)
            }
        }
    }

    private inline fun safePlayerFromRoster(playerId: String, block: (PlayerRef) -> Unit) {
        formationSession?.let {
            it.roster?.let { roster ->
                roster.players?.forEach { playerRef ->
                    if (playerRef.id == playerId) {
                        block(playerRef)
                    }
                }
            }
        }
    }

    // 1A
    private fun preparePlayerLayoutParamsForFormation(playerRef: PlayerRef): RelativeLayout.LayoutParams {
        val layoutParams = getRelativeLayoutParams()
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        layoutParams.width = 300
        layoutParams.height = 75
        if (!playerRef.pointX.isNullOrEmpty() || playerRef.pointX != 0) {
            layoutParams.leftMargin = playerRef.pointX!!
        }
        if (!playerRef.pointY.isNullOrEmpty() || playerRef.pointY != 0) {
            layoutParams.topMargin = playerRef.pointY!!
        }
        return layoutParams
    }


    private fun View?.setupPlayerPopupMenu() {
        playerPopMenuView = this?.attachAndInflatePopMenu(R.menu.floating_player_menu) { menuItem, parentView ->
            when (menuItem.itemId) {
                R.id.menu_player_change_teams -> {
                    // Do something
                    val playerId = parentView.tag
                    safePlayerFromRoster(playerId as String) { playerRef ->
                        when (playerRef.color) {
                            "red" -> {
                                realmInstance?.safeWrite {
                                    playerRef.color = "blue"
                                }
                            }
                            "blue" -> {
                                realmInstance?.safeWrite {
                                    playerRef.color = "red"
                                }
                            }
                        }
                        findPlayerViewInFormation(playerId) { playerView ->
                            playerView.setPlayerTeamBackgroundColor(playerRef.color)
                        }
                    }

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
    }
    private fun View?.setupOnTapListeners(playerId: String) {
        onTap = {
            popPlayerProfileDialog(requireActivity(), playerId).show()
        }
        onLongPress = {
            log("Double Tap")
            this?.wiggleOnce()
            playerPopMenuView?.show()
        }
    }
}

fun View.setPlayerTeamBackgroundColor(colorName: String?) {
    val color = when (colorName) {
        "red" -> ContextCompat.getColor(this.context, R.color.ysrFadedRed)
        "blue" -> ContextCompat.getColor(this.context, R.color.ysrFadedBlue)
        else -> Color.TRANSPARENT
    }
    setBackgroundColor(color)
}

fun String.parseColor(): Int {
    return Color.parseColor(this)
}