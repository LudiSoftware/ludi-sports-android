package io.usys.report.ui.ludi.formationbuilder

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.providers.fireRosterUpdateRoster
import io.usys.report.providers.fireRosterUpdatePlayers
import io.usys.report.realm.*
import io.usys.report.realm.local.*
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.toPlayerProfile
import io.usys.report.ui.ludi.formationbuilder.menus.showPlayerMenuPopup
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.setupPlayerPositionSpinner
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.views.gestures.LudiFreeFormGestureDetector
import io.usys.report.ui.views.hideLudiActionBar
import io.usys.report.ui.views.listAdapters.linearLayoutManager
import io.usys.report.utils.*
import io.usys.report.utils.views.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : LudiStringIdsFragment() {

    var adapterSubstitutes: RosterFormationListLiveAdapter? = null
    var adapterFiltered: RosterFormationListLiveAdapter? = null
    var formationRelativeLayout: RelativeLayout? = null
    var deckLinearLayout: LinearLayoutCompat? = null
    var deckSubsRecyclerView: RecyclerView? = null
    var deckFilteredRecyclerView: RecyclerView? = null
    var rosterTypeSpinner: Spinner? = null

    // Formation Session
    private var floatingMenuButton: FloatingActionButton? = null
    var floatingPopMenu: PopupMenu? = null

    // Player Icons on the field
    private var playerObserver: RealmResults<PlayerRef>? = null
    private var formationPlayerItemViewList = mutableListOf<View>()
    private var formationPlayerItemPositionTextViewList = mutableListOf<TextView>()
    var formationPlayerColoredBackgroundViews = mutableListOf<CardView>()
    // Player Formation
    var onTap: ((String) -> Unit)? = null
    var onLongPress: ((String) -> Unit)? = null
    // Global Menu
    private var globalFloatingMenuView: View? = null
    var onMenuTap: ((String) -> Unit)? = null

    var motionConstraintLayout: MotionLayout? = null
    var motionIsUp: Boolean = false
    var soccerFieldImageView: ImageFilterView? = null
    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    var officialRoster: String? = null
    var tryoutRoster: String? = null

    lateinit var rosterConfig: RosterConfig

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        if (container == null) {
            val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
            rootView = teamContainer?.inflateLayout(R.layout.formation_builder_fragment_portrait) ?: View(requireContext())
        } else {
            rootView = container.inflateLayout(R.layout.formation_builder_fragment_portrait)
        }

        teamId?.let {
            rosterConfig = RosterConfig(it).apply {
                parentFragment = this@RosterFormationFragment
            }
        }

        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            officialRoster = teamSession.rosterId
            tryoutRoster = teamSession.tryoutRosterId
        }

        bindViews()
        configureDisplay()
        setSoccerFieldLight()
        setupMotionLayoutListener()
        setupDisplay()
        setupRosterTypeSpinner()
        return rootView
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
    }

    private fun configureDisplay() {
        //Hiding the action bar
        hideLudiActionBar()
        //Hiding the bottom navigation bar
        hideLudiNavView()
    }

    /** GLOBAL/FRAGMENT DISPLAY SETUP - onCreate process function **/
    private fun bindViews() {
        floatingMenuButton = rootView.findViewById(R.id.formationBuilderFloatingActionButton)
        soccerFieldImageView = rootView.findViewById(R.id.soccerfield)
        motionConstraintLayout = rootView.findViewById(R.id.formationMotionRootLayout)
        deckLinearLayout = rootView.findViewById(R.id.formationRosterListsLinearLayout)
        deckSubsRecyclerView = rootView.findViewById(R.id.ysrTORecycler)
        deckFilteredRecyclerView = rootView.findViewById(R.id.ysrTORecyclerTwo)
        formationRelativeLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)
        rosterTypeSpinner = rootView.findViewById(R.id.formationBuilderRosterTypeSpinner)
    }

    /** MOTION LAYOUT: Motion/Transition Listener **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setupMotionLayoutListener() {
        motionConstraintLayout?.let {
            val formationGestureDetector = FormationBuilderGestureHandler(requireContext(), it)
            it.setOnTouchListener(formationGestureDetector)
            formationRelativeLayout?.setOnTouchListener(formationGestureDetector)
        }
    }

    /** SETUP: Roster Type Spinner **/
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRosterTypeSpinner() {
        rosterTypeSpinner?.setupRosterTypeSpinner(rosterConfig.rosterIds) { _, item ->
            rosterConfig.rosterIds.forEach { (key, _) ->
                if (key == item) {
                    switchToRoster(key)
                }
            }
            log("RosterTypeSpinner: $item")
        }
    }

    /** Master Roster Type Switch **/
    private fun switchToRoster(rosterType:String) {
        rosterConfig.switchRosterTo(rosterType)
        adapterSubstitutes?.switchRosterTo(rosterType)
        adapterFiltered?.switchRosterTo(rosterType)
        reloadFormation()
    }

    /** SETUP: Formation Soccer Field / Background **/
    private fun setSoccerFieldLight(){
        soccerFieldImageView?.setImageDrawable(getBackgroundDrawable(requireContext(), R.drawable.soccer_field))
        soccerFieldImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    fun setSoccerFieldDark(){
        soccerFieldImageView?.setImageDrawable(getBackgroundDrawable(requireContext(), R.drawable.dark_soccer_field))
        soccerFieldImageView?.scaleType = ImageView.ScaleType.FIT_XY
    }

    /**
     * FORMATION HELPERS: Functions
     */
    private fun resetFormationLayout() {
        adapterSubstitutes?.resetDeckToRoster()
        formationPlayerItemViewList.clear()
        formationRelativeLayout?.removeAllViews()
        formationRelativeLayout?.addView(globalFloatingMenuView)

    }

    fun movePlayerToDeck(playerId: String) {
        formationPlayerItemViewList.find { it.tag == playerId }?.let { view ->
            formationPlayerItemViewList.remove(view)
            formationRelativeLayout?.removeView(view)
            adapterSubstitutes?.movePlayerToDeck(playerId)
        }
    }


    /** GLOBAL/FRAGMENT DISPLAY ORDER/PROCESS HANDLING
     *      Display Process Functions
     */
    private fun setupDisplay() {
        activity?.window?.let {
            createGlobalMenuButton()
            setupFullDisplay()
            setupFilteredList()
            setupFormationList()
        }
    }

    /** GLOBAL/FRAGMENT DISPLAY SETUP - display process function **/
    private fun setupFullDisplay() {
        reloadDeck()
        reloadFormation()
    }

    /** ON-DECK LAYOUT: SETUP A FILTERED LIST OF PLAYERS ON-DECK - display process function **/
    private fun setupFilteredList() {
        realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { ts ->
            rosterConfig.filters = ludiFilters("foot" to "left")
            adapterFiltered = RosterFormationListLiveAdapter(rosterConfig)
            adapterSubstitutes?.switchRosterToTryout()
            deckFilteredRecyclerView?.layoutManager = linearLayoutManager(requireContext(), isHorizontal = true)
            deckFilteredRecyclerView?.adapter = adapterFiltered
        }
    }

    /** FORMATION LAYOUT: SETUP LIST OF PLAYERS ON SOCCER FIELD - display process function **/
    private fun setupFormationList() {
        createFormationDragListener()
        adapterSubstitutes?.let { itAdapter ->
            val itemTouchHelperCallback = RosterFormationTouchHelperCallback(itAdapter)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(deckSubsRecyclerView)
        }
    }

    /**
     * todo:
     * LIFECYCLE: Orientation Change Functions
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.inflater?.inflate(R.layout.formation_builder_fragment_landscape, container, false)
            setupDisplay()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.inflater?.inflate(R.layout.formation_builder_fragment_portrait, container, false)
            setupDisplay()
        }
    }

    /**
     * FORMATION LAYOUT: DRAG LISTENER for when a player is dragged onto the soccer field
     */
    private fun createFormationDragListener() {
        tryCatch {
            formationRelativeLayout?.setFormationDropListener { itPlayerId, x, y ->
                // stop observer
                playerObserver?.removeAllChangeListeners()
                adapterSubstitutes?.movePlayerToFormation(itPlayerId)
                addPlayerToFormation(itPlayerId, x = x, y = y)
            }
        }
    }
    /**
     * ON-DECK LAYOUT: DRAG LISTENER for when a player is dragged off the deck.
     */
    private fun createDeckDragListener() {
        deckSubsRecyclerView?.setDeckDragListener(motionConstraintLayout, deckLinearLayout)
    }

    /** FORMATION LAYOUT: SETTING UP A PLAYER ON THE SOCCER FIELD **/
    @SuppressLint("SetTextI18n")
    private fun addPlayerToFormation(playerId: String, x:Float?=null, y:Float?=null, loadingFromSession: Boolean = false) {
        // Create New PlayerView for Formation
        val playerRefViewItem = inflateView(requireContext(), R.layout.roster_player_card_formation)
        // Bind PlayerView's
        val vPlayerName = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtName)
        val vPlayerTryOutTag = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtTag)
        val vPlayerCircleLayout = playerRefViewItem.findViewById<CardView>(R.id.formationCardViewLayout)
        val vPlayerPosition = playerRefViewItem.findViewById<TextView>(R.id.cardPlayerFormationTxtPosition)

        //Prepare PlayerView from Drag/Drop
        safePlayerFromRoster(playerId) { newPlayerRef ->
            if (!loadingFromSession) {
                realmInstance?.safeWrite { itRealm ->
                    itRealm.rosterSessionById(rosterConfig.currentRosterId) { ts ->
                        ts.formationListIds?.let {
                            if (!it.contains(playerId)) {
                                it.add(playerId)
                            }
                        }
                    }
                }
            }

            formationPlayerItemViewList.removeIf { it.tag == playerId }

            val layoutParams = preparePlayerLayoutParamsForFormation(loadingFromSession)
            // X
            if (x != null) {
                log("X: $x")
                layoutParams.leftMargin =  x.toInt() - 75
            } else if (!newPlayerRef.pointX.isNullOrEmpty() || newPlayerRef.pointX != 0) {
                layoutParams.topMargin = newPlayerRef.pointX ?: 0
            }
            // Y
            if (y != null) {
                log("Y: $y")
                layoutParams.topMargin = y.toInt() - 75
            } else if (!newPlayerRef.pointY.isNullOrEmpty() || newPlayerRef.pointY != 0) {
                layoutParams.leftMargin = newPlayerRef.pointY ?: 0
            }
            // Save New X,Y Position in Formation
            if (x != null && y != null) {
                realmInstance?.findPlayerRefById(playerId) { playerRef ->
                    realmInstance?.safeWrite {
                        playerRef?.pointX = y.toInt() - 75
                        playerRef?.pointY = x.toInt() - 75
                        if (playerRef != null) {
                            it.copyToRealmOrUpdate(playerRef)
                        }
                    }
                }
            }
            playerRefViewItem.layoutParams = layoutParams
            playerRefViewItem.tag = newPlayerRef.id
            vPlayerCircleLayout.tag = newPlayerRef.id
            vPlayerPosition.tag = newPlayerRef.id
            // Bind Data
            vPlayerName.text = newPlayerRef.name
            // Debugging player positions on the screen
//            vPlayerName.text = "X: [${newPlayerRef.pointX}] Y: [${newPlayerRef.pointY}]"
            vPlayerTryOutTag.text = "Tag: ${newPlayerRef.tryoutTag.toString()}"
            vPlayerPosition.text = newPlayerRef.position
            newPlayerRef.color?.let {
                vPlayerCircleLayout.setPlayerFormationBackgroundColor(it)
            }
            onTap = { _ ->
                log("Single Tap")
                this@RosterFormationFragment.showPlayerMenuPopup(playerRefViewItem)
            }
            // Gestures
            playerRefViewItem.onGestureDetectorRosterFormation(
                rosterId = rosterConfig.currentRosterId ?: "",
                playerId=playerId,
                onSingleTapUp = onTap,
                onLongPress = onLongPress
            )

            // Add to reference lists
            formationPlayerItemViewList.add(playerRefViewItem)
            formationPlayerColoredBackgroundViews.add(vPlayerCircleLayout)
            formationPlayerItemPositionTextViewList.add(vPlayerPosition)
            // Add to Layout
            formationRelativeLayout?.addView(playerRefViewItem)
        }

    }

    private fun createGlobalMenuButton() {
        globalFloatingMenuView = inflateView(requireContext(), R.layout.roster_menu_button_formation)
        val layoutParams = preparePlayerLayoutParamsForFormation()
        val viewTag = 999
        globalFloatingMenuView?.tag = viewTag
        globalFloatingMenuView?.layoutParams = layoutParams
        onMenuTap = { _ ->
            log("Single Tap")
            this@RosterFormationFragment.showPlayerMenuPopup(globalFloatingMenuView!!)
        }
        // Gestures
        globalFloatingMenuView.onGestureDetectorRosterFormation(
            rosterId=null,
            playerId=viewTag.toString(),
            onSingleTapUp=onMenuTap
        )

        // Add to Layout
        formationRelativeLayout?.addView(globalFloatingMenuView)
    }

    /**
     * Formation / Deck UI Loaders
     */
    private fun reloadDeck() {
        realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { ts ->
            // -> Setup Substitution List
            if (!ts.deckListIds.isNullOrEmpty()) {
                adapterSubstitutes = RosterFormationListLiveAdapter(rosterConfig)
                adapterSubstitutes?.switchRosterToTryout()
                deckSubsRecyclerView?.layoutManager = linearLayoutManager(requireContext(), isHorizontal = true)
                deckSubsRecyclerView?.adapter = adapterSubstitutes
                createDeckDragListener()
            }
        }
    }
    private fun reloadFormation() {
        // We want to do this all in one instance of getting the session from realm.
        realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { ts ->
            // -> Setup Formation Of Players
            ts.formationListIds?.let { formationList ->
                formationRelativeLayout?.removeAllViews()
                formationRelativeLayout?.addView(globalFloatingMenuView)
                formationPlayerItemViewList.clear()
                formationList.forEach { itPlayerId ->
                    addPlayerToFormation(itPlayerId, loadingFromSession = true)
                }
            }
        }
    }
    fun getFormationPlayerPositionTextView(playerId: String): TextView? {
        return formationPlayerItemPositionTextViewList.find { it.tag == playerId }
    }
    private fun getFormationPlayerView(playerId: String): View? {
        return formationPlayerItemViewList.find { it.tag == playerId }
    }

    /**
     * GLOBAL MENU: Floating Action Menu Functions - display process function
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingActionMenu() {
        floatingPopMenu = floatingMenuButton?.attachAndInflatePopMenu(R.menu.floating_formation_menu) { menuItem, _ ->
            // Handle menu item click events
            // todo: events to handle:
            //  - if in tryout mode, submit formation as roster.
            //  - Change background image.
            when (menuItem.itemId) {
                R.id.menu_save_roster -> {
                    // Do something
                    log("Save Formation")
                    rosterConfig.currentRosterId?.let {
                        realmInstance?.fireRosterUpdatePlayers(it)
                    }
                }
                R.id.menu_submit_roster -> {
                    // Do something
                    realmInstance?.fireRosterUpdateRoster(teamId)
                }
                R.id.menu_reset -> {
                    resetFormationLayout()
                }
                R.id.menu_formation_team_color_toggle -> {
                    realmInstance?.rosterSessionById(rosterConfig.currentRosterId) { rosterSession ->
                        realmInstance?.safeWrite {
                            if (rosterSession.teamColorsAreOn) {
                                menuItem.title = "Turn ON Team Colors"
                                rosterSession.teamColorsAreOn = false
                            } else {
                                menuItem.title = "Turn OFF Team Colors"
                                rosterSession.teamColorsAreOn = true
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

    /** FORMATION LAYOUT: Class Helpers Below **/
    inline fun findPlayerViewInFormation(playerId: String, block: (CardView) -> Unit) {
        formationPlayerColoredBackgroundViews.forEach { playerView ->
            if (playerView.tag == playerId) {
                block(playerView)
            }
        }
    }

    inline fun safePlayerFromRoster(playerId: String, crossinline block: (PlayerRef) -> Unit) {
        realmInstance?.findRosterById(rosterConfig.currentRosterId)?.let { roster ->
            roster.players?.forEach { playerRef ->
                if (playerRef.id == playerId) {
                    block(playerRef)
                }
            }
        }
    }
}



