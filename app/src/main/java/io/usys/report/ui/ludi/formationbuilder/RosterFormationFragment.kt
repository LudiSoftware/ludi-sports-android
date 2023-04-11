package io.usys.report.ui.ludi.formationbuilder

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.local.*
import io.usys.report.realm.model.*
import io.usys.report.ui.fragments.LudiStringIdsFragment
import io.usys.report.ui.fragments.toPlayerProfile
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.setupPlayerPositionSpinner
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.ludi.team.pushPlayersToRosterInFirebase
import io.usys.report.ui.views.gestures.LudiFreeFormGestureDetector
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

    var adapterSubstitutes: RosterFormationListLiveAdapter? = null
    var adapterFiltered: RosterFormationListLiveAdapter? = null
    var formationRelativeLayout: RelativeLayout? = null
    var deckLinearLayout: LinearLayoutCompat? = null
    var deckSubsRecyclerView: RecyclerView? = null
    var deckFilteredRecyclerView: RecyclerView? = null

    // Formation Session
    var floatingMenuButton: FloatingActionButton? = null
    var floatingPopMenu: PopupMenu? = null

    // Player Icons on the field
    var playerObserver: RealmResults<PlayerRef>? = null
    var formationPlayerItemViewList = mutableListOf<View>()
    var formationPlayerItemPositionTextViewList = mutableListOf<TextView>()
    var formationPlayerColoredBackgroundViews = mutableListOf<CardView>()
    // Player Formation
    var onTap: ((String) -> Unit)? = null
    var onLongPress: ((String) -> Unit)? = null

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
            rootView = teamContainer?.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        } else {
            rootView = container.inflateLayout(R.layout.fragment_list_formations_portrait)
        }

        rosterConfig = RosterConfig(teamId!!).apply {
            parentFragment = this@RosterFormationFragment
        }

        realmInstance?.teamSessionByTeamId(teamId) { teamSession ->
            officialRoster = teamSession.rosterId
            tryoutRoster = teamSession.tryoutRosterId
        }

        bindViews()
        configureDisplay()
//        setupRosterSession()
        setSoccerFieldLight()
        setupMotionLayoutListener()
        setupDisplay()
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
        floatingMenuButton = rootView.findViewById(R.id.tryoutFormationFloatingActionButton)
        soccerFieldImageView = rootView.findViewById(R.id.soccerfield)
        motionConstraintLayout = rootView.findViewById(R.id.formationMotionRootLayout)
        deckLinearLayout = rootView.findViewById(R.id.formationRosterListsLinearLayout)
        deckSubsRecyclerView = rootView.findViewById(R.id.ysrTORecycler)
        deckFilteredRecyclerView = rootView.findViewById(R.id.ysrTORecyclerTwo)
        formationRelativeLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)
    }

    /** MOTION LAYOUT: Motion/Transition Listener **/
    private fun setupMotionLayoutListener() {
        motionConstraintLayout?.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionIsUp = !motionIsUp

                if (motionIsUp) {
                    formationRelativeLayout?.setOnClickListener {
                        motionConstraintLayout?.transitionToStart()
                    }
                }
                adapterSubstitutes?.notifyDataSetChanged()
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
    }

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
    }


    /** GLOBAL/FRAGMENT DISPLAY ORDER/PROCESS HANDLING
     *      Display Process Functions
     */
    private fun setupDisplay() {
        activity?.window?.let {
            setupFullDisplay()
            setupFilteredList()
            setupFormationList()
            setupFloatingActionMenu()
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
            this.inflater?.inflate(R.layout.fragment_list_formations_landscape, container, false)
            setupDisplay()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.inflater?.inflate(R.layout.fragment_list_formations_portrait, container, false)
            setupDisplay()
        }
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
                R.id.menu_save_formation -> {
                    // Do something
                    log("Save Formation")
//                    realmInstance?.pushPlayersToRosterInFirebase()
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
        formationRelativeLayout?.let { deckSubsRecyclerView?.setDeckDragListener(it) }
    }

    /** FORMATION LAYOUT: SETTING UP A PLAYER ON THE SOCCER FIELD **/
    private fun addPlayerToFormation(playerId: String, x:Float?=null, y:Float?=null, loadingFromSession: Boolean = false) {
        // Create New PlayerView for Formation
        val playerRefViewItem = inflateView(requireContext(), R.layout.card_player_formation)
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
//                        ts.roster.setPlayerAsAccepted(playerId)
                    }
                }
//                adapterFiltered?.reload()
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
                realmInstance?.teamSessionByTeamId(teamId) { fs ->
                    playerId.let { itId ->
                        realmInstance?.findPlayerRefById(itId) { playerRef ->
                            realmInstance?.safeWrite {
                                playerRef?.pointX = y.toInt() - 75
                                playerRef?.pointY = x.toInt() - 75
                                it.copyToRealmOrUpdate(playerRef)
                            }
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
                log("Double Tap")
                showPlayerMenuPopup(playerRefViewItem, this)
            }
            // Gestures
            playerRefViewItem.onGestureDetectorRosterFormation(
                teamId= teamId!!,
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
                formationPlayerItemViewList.clear()
                formationList.forEach { itPlayerId ->
                    addPlayerToFormation(itPlayerId, loadingFromSession = true)
                }
            }
        }
    }
    private fun getFormationPlayerPositionTextView(playerId: String): TextView? {
        return formationPlayerItemPositionTextViewList.find { it.tag == playerId }
    }
    private fun getFormationPlayerView(playerId: String): View? {
        return formationPlayerItemViewList.find { it.tag == playerId }
    }

    /** FORMATION LAYOUT: PLAYER POPUP MENU **/
    private fun showPlayerMenuPopup(anchorView: View, fragment: Fragment) {
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.menu_player_position_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val playerId = anchorView.tag
        val layoutPlayerProfile = popupView.findViewById<LinearLayout>(R.id.menuPlayerPlayerProfileLayout)
        val layoutChangeTeams = popupView.findViewById<LinearLayout>(R.id.menuPlayerChangeTeamsLayout)
        val layoutIsSelected = popupView.findViewById<LinearLayout>(R.id.menuPlayerIsSelectedLayout)
        val checkBoxIsSelected = popupView.findViewById<CheckBox>(R.id.menuPlayerCheckIsSelected)
        val btnProfile = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnProfile)
        val btnChangeTeams = popupView.findViewById<ImageButton>(R.id.menuPlayerBtnChangeTeams)

        // Find and set up the Spinner
        val positionSpinner = popupView.findViewById<Spinner>(R.id.menuPlayerPositionSpinner)
        positionSpinner.setupPlayerPositionSpinner(playerId as String, getFormationPlayerPositionTextView(playerId))
        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

        // If you want to dismiss the popup when clicking outside of it
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        // Set the unfold animation when showing the popup
        popupWindow.contentView.startAnimation(unfoldAnimation)

        layoutPlayerProfile.attachViewsToOnClickListener(btnProfile) {
            log("menu_player_profile")
            fragment.toPlayerProfile(playerId.toString())
            popupWindow.dismiss()
        }

        layoutChangeTeams.attachViewsToOnClickListener(btnChangeTeams) {
            log("menu_player_change_teams")
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
                    playerView.setPlayerFormationBackgroundColor(playerRef.color)
                }
            }
            popupWindow.dismiss()
        }

        realmInstance?.findPlayerRefById(playerId)?.let { itPlayer ->
            checkBoxIsSelected.isChecked = itPlayer.status == PLAYER_STATUS_SELECTED
        }

        checkBoxIsSelected.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                safePlayerFromRoster(playerId) { playerRef ->
                    realmInstance?.safeWrite {
                        playerRef.status = PLAYER_STATUS_SELECTED
                    }
                }
            } else {
                safePlayerFromRoster(playerId) { playerRef ->
                    realmInstance?.safeWrite {
                        playerRef.status = PLAYER_STATUS_OPEN
                    }
                }
            }
        }

        layoutIsSelected.setOnClickListener {
            log("menu_player_is_selected")
            checkBoxIsSelected.toggle()
        }

        // Set the fold animation when dismissing the popup
        popupWindow.setOnDismissListener {
            popupView.startAnimation(foldAnimation)
        }

        // Show the PopupWindow below the anchor view (menu item)
        popupWindow.showAsDropDown(anchorView)
    }

    /** FORMATION LAYOUT: Class Helpers Below **/
    private inline fun findPlayerViewInFormation(playerId: String, block: (CardView) -> Unit) {
        formationPlayerColoredBackgroundViews.forEach { playerView ->
            if (playerView.tag == playerId) {
                block(playerView)
            }
        }
    }

    private inline fun safePlayerFromRoster(playerId: String, crossinline block: (PlayerRef) -> Unit) {
        realmInstance?.findRosterById(rosterConfig.currentRosterId)?.let { roster ->
            roster.players?.forEach { playerRef ->
                if (playerRef.id == playerId) {
                    block(playerRef)
                }
            }
        }
    }
}



