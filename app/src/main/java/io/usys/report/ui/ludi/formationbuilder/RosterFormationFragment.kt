package io.usys.report.ui.ludi.formationbuilder

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.local.TeamSession
import io.usys.report.realm.local.saveRosterToFirebase
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.toFragmentWithIds
import io.usys.report.ui.gestures.LudiFreeFormGestureDetector
import io.usys.report.ui.views.listAdapters.linearLayoutManager
import io.usys.report.utils.*
import io.usys.report.utils.views.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : LudiStringIdsFragment() {

    companion object {
        const val TAG = "Formation"
        fun newInstance(): RosterFormationFragment {
            return RosterFormationFragment()
        }
    }

    var adapter: RosterFormationListAdapter? = null
    var adapterFiltered: RosterFormationListAdapter? = null
    var formationRelativeLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null
    var filteredPlayerListRecyclerView: RecyclerView? = null

    // Formation Session
    var floatingMenuButton: FloatingActionButton? = null
    var floatingPopMenu: PopupMenu? = null

    // Player Icons on the field
    var formationViewList = mutableListOf<View>()
//    var formationLayouts = mutableListOf(R.drawable.soccer_field)
    // Player Formation
    var onTap: ((String) -> Unit)? = null
    var onLongPress: (() -> Unit)? = null

    var dragListener: View.OnDragListener? = null
    var onItemDragged: ((start: Int, end: Int) -> Unit)? = null

    var motionConstraintLayout: MotionLayout? = null
    var motionIsUp: Boolean = false
    var soccerFieldImageView: ImageFilterView? = null
    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    var teamSession: TeamSession? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            this.teamSession = teamSession
        }
        //Base Team Data
        //Hiding the action bar
        hideLudiActionBar()
        //Hiding the bottom navigation bar
        hideLudiNavView()
        if (container == null) {
            val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
            rootView = teamContainer?.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        } else {
            rootView = container.inflateLayout(R.layout.fragment_list_formations_portrait)
        }

        soccerFieldImageView = rootView.findViewById(R.id.soccerfield)
        setSoccerFieldLight()
        motionConstraintLayout = rootView.findViewById(R.id.formationMotionRootLayout)

        motionConstraintLayout?.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionIsUp = !motionIsUp
                adapter?.notifyDataSetChanged()
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
        setupDisplay()
        return rootView
    }

    fun setSoccerFieldLight(){
        soccerFieldImageView?.setImageDrawable(getBackgroundDrawable(R.drawable.soccer_field))
        soccerFieldImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    fun setSoccerFieldDark(){
        soccerFieldImageView?.setImageDrawable(getBackgroundDrawable(R.drawable.dark_soccer_field))
        soccerFieldImageView?.scaleType = ImageView.ScaleType.FIT_XY
    }
    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    /**
     * Base Functions
     *
     */

    private fun resetFormationLayout() {
        adapter?.resetDeckToRoster()
        formationViewList.clear()
        formationRelativeLayout?.removeAllViews()
    }

    fun getBackgroundDrawable(drawableReference: Int): Drawable? {
        return getDrawable(requireContext(), drawableReference)
    }

    /**
     * Display Functions
     *
     */
    private fun setupDisplay() {
        activity?.window?.let {
            rosterListRecyclerView = rootView.findViewById(R.id.ysrTORecycler)
            filteredPlayerListRecyclerView = rootView.findViewById(R.id.ysrTORecyclerTwo)
            formationRelativeLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)
            setupFloatingActionMenu()
            setupRosterList()
            setupFilteredList()
            setupFormationList()
        }
    }
    private fun setupRosterList() {
        onItemDragged = { start, end ->
            log("onItemDragged: $start, $end")
        }
        realmInstance?.teamSessionByTeamId(teamId) { ts ->
            if (!ts.deckListIds.isNullOrEmpty()) {
                adapter = RosterFormationListAdapter(teamId!!, realmInstance, findNavController())
                rosterListRecyclerView?.layoutManager = linearLayoutManager(requireContext(), isHorizontal = true)
                rosterListRecyclerView?.adapter = adapter
            }
            ts.formationListIds?.let { formationList ->
                formationRelativeLayout?.removeAllViews()
                formationViewList.clear()
                formationList.forEach { itPlayerId ->
                    addPlayerToFormation(itPlayerId, loadingFromSession = true)
                }
            }
        }
    }

    private fun setupFilteredList() {
        onItemDragged = { start, end ->
            log("onItemDragged: $start, $end")
        }
        realmInstance?.teamSessionByTeamId(teamId) { ts ->
            adapterFiltered = RosterFormationListAdapter(teamId!!, realmInstance, findNavController(), mutableMapOf("foot" to "left"))
            filteredPlayerListRecyclerView?.layoutManager = linearLayoutManager(requireContext(), isHorizontal = true)
            filteredPlayerListRecyclerView?.adapter = adapterFiltered
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
     * Global Floating Action Menu Functions
     *
     */
    @SuppressLint("ClickableViewAccessibility")
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
                    realmInstance?.teamSessionByTeamId(teamId) { ts ->
                        ts.saveRosterToFirebase()
                    }
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
                        adapter?.movePlayerToFormation(playerId)
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
                    itRealm.teamSessionByTeamId(teamId) { ts ->
                        ts.formationListIds?.let {
                            if (!it.contains(playerId)) {
                                it.add(playerId)
                            }
                        }
                        ts.roster.setPlayerAsAccepted(playerId)
                    }
                }
                adapterFiltered?.reload()
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
        teamSession?.let { ts ->
            ts.roster?.let { roster ->
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
            val playerId = parentView.tag
            when (menuItem.itemId) {
                R.id.menu_player_accept -> {
                    // Do something
                    log("menu_player_accept")
                    safePlayerFromRoster(playerId as String) { playerRef ->
                        realmInstance?.safeWrite {
                            playerRef.status = PLAYER_STATUS_ACCEPTED
                        }
                    }
                }
                R.id.menu_player_change_teams -> {
                    // Do something
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
        onTap = { playerId ->
            //todo: use profile fragment now.
            toFragmentWithIds(R.id.navigation_player_profile, teamId = teamId, playerId = playerId)
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