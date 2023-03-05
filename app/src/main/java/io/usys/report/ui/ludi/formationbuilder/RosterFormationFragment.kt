package io.usys.report.ui.ludi.formationbuilder

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
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
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiTeamFragment
import io.usys.report.ui.fragments.unbundleTeamId
import io.usys.report.ui.ludi.player.popPlayerProfileDialog
import io.usys.report.ui.gestures.LudiFreeFormGestureDetector
import io.usys.report.utils.*
import io.usys.report.utils.views.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : LudiTeamFragment() {

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

    // Player Icons on the field
    var formationViewList = mutableListOf<View>()
//    var formationLayouts = mutableListOf(R.drawable.soccer_field)
    // Player Formation
    var onTap: (() -> Unit)? = null
    var onLongPress: (() -> Unit)? = null

    var dragListener: View.OnDragListener? = null
    var onItemDragged: ((start: Int, end: Int) -> Unit)? = null

    var constraintLayout: ConstraintLayout? = null
    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            this.teamSession = teamSession
            rosterId = teamSession.rosterId
        }
        setupRosterRealmListener()
        //Base Team Data
//        setupRosterList()
        //Hiding the action bar
        hideLudiActionBar()
        //Hiding the bottom navigation bar
        hideLudiNavView()
        if (container == null) {
            val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
            rootView = teamContainer?.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        } else {
            rootView = container.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        }
        constraintLayout = rootView.findViewById(R.id.TryoutTestFragment)
        setupDisplay()
        return rootView
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    /**
     * Base Functions
     *
     */

    private fun saveRosterIdToFormationSession(rosterId: String?) {
        if (rosterId == null) return
        teamSession?.let { formationSession ->
            formationSession.rosterId = rosterId
//            it.insertOrUpdate(formationSession)
        }
    }


    private fun resetFormationLayout() {
        adapter?.resetDeckToRoster()
        formationViewList.clear()
        formationRelativeLayout?.removeAllViews()
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
        realmInstance?.teamSessionByTeamId(teamId) { ts ->
            if (!ts.deckListIds.isNullOrEmpty()) {
                adapter = RosterFormationListAdapter(teamId!!, realmInstance, requireActivity())
                rosterListRecyclerView?.layoutManager = gridLayoutManager(requireContext())
                rosterListRecyclerView?.adapter = adapter
            }
            ts.formationListIds?.let { formationList ->
                formationRelativeLayout?.removeAllViews()
                formationList.forEach { itPlayerId ->
                    addPlayerToFormation(itPlayerId, loadingFromSession = true)
                }
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

    fun setupRosterRealmListener() {
        realmRosterCallBack = {
            log("Roster Realm Listener")
            setupDisplay()
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
        floatingMenuButton = rootView.findViewById(R.id.tryoutFormationFloatingActionButton)
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
                    realmInstance?.safeWrite {
                        teamSession?.let {
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
        tryCatch {
            formationRelativeLayout?.setOnDragListener(dragListener)
        }
    }

    // 1 Master
    private fun addPlayerToFormation(playerId: String, loadingFromSession: Boolean = false) {
        val playerRefViewItem = inflateView(requireContext(), R.layout.card_player_formation)
        val playerName = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtName)
        val playerTryOutTag = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtTryOutTag)

        //Prepare PlayerView from Drag/Drop
        safePlayerFromRoster(playerId) { newPlayerRef ->

            if (!loadingFromSession) {
                realmInstance?.safeWrite { itRealm ->
                    teamSession?.let { fs ->
                        fs.formationListIds?.let {
                            if (!it.contains(playerId)) {
                                it.add(playerId)
                            }
                        }
                    }
                }
            }

            formationViewList.find { it.tag == playerId }?.let {
                return@safePlayerFromRoster
            }

            val layoutParams = preparePlayerLayoutParamsForFormation(loadingFromSession)
            if (!newPlayerRef.pointX.isNullOrEmpty() || newPlayerRef.pointX != 0) {
                layoutParams.topMargin = newPlayerRef.pointX ?: 0
            }
            if (!newPlayerRef.pointY.isNullOrEmpty() || newPlayerRef.pointY != 0) {
                layoutParams.leftMargin = newPlayerRef.pointY ?: 0
            }
            playerRefViewItem.layoutParams = layoutParams
            playerRefViewItem.tag = newPlayerRef.id
            // Bind Data
            playerName.text = newPlayerRef.name
            playerTryOutTag.text = newPlayerRef.tryoutTag
            newPlayerRef.color?.let {
                playerRefViewItem.setPlayerTeamBackgroundColor(it)
            }
            playerRefViewItem.setupPlayerPopupMenu()
            // Gestures
            playerRefViewItem.onGestureDetectorRosterFormation(
                width = 300,
                height = 75,
                teamId= teamId!!,
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
        teamSession?.let {
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
    private fun preparePlayerLayoutParamsForFormation(loadingFromSession: Boolean=false): RelativeLayout.LayoutParams {
        val layoutParams = getRelativeLayoutParams()
        if (!loadingFromSession) layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        layoutParams.width = 300
        layoutParams.height = 75
        return layoutParams
    }

    private fun View?.setupPlayerPopupMenu() {
        val playerPopMenuView = this?.attachAndInflatePopMenu(R.menu.floating_player_menu) { menuItem, parentView ->
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
        onTap = {
            popPlayerProfileDialog(requireActivity(), this!!.tag.toString()).show()
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