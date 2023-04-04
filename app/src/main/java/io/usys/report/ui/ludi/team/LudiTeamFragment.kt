package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.realm.findByField
import io.usys.report.realm.local.TeamSession
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.userOrLogout
import io.usys.report.realm.realm
import io.usys.report.realm.safeAdd
import io.usys.report.realm.safeWrite
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log
import io.usys.report.utils.views.wiggleOnce

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class LudiTeamFragment : Fragment() {

    var _MODE = YsrMode.TRYOUTS
    lateinit var rootView : View
    var menu: TeamMenuPopupProvider? = null
    var itemOnClick: ((View, RealmObject) -> Unit)? = null

    var realmTeamCallBack: ((Team) -> Unit)? = null
    var realmRosterCallBack: ((Roster) -> Unit)? = null

    var user: User? = null
    var realmInstance: Realm? = null
    var teamSession: TeamSession? = null
    var teamId: String? = null
    var rosterId: String? = null


    abstract fun setupOnClickListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (realmInstance == null) realmInstance = realm()
        realmInstance?.userOrLogout(requireActivity()) { user = it }
        if (teamId == null) teamId = unbundleStringId()
        log("TeamId: $teamId")
        setupTeamSession()
        setupTeamRealmListener()
        setupTeamRosterRealmListener()
        setupTryOutRealmListener()
    }
    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
        menu?.let {
            requireActivity().removeMenuProvider(it)
        }
    }

    private fun setupTeamSession() {
        teamId?.let { itTeamId ->
            teamSession = realmInstance?.findByField("id", itTeamId)
            if (teamSession == null) {
                realmInstance?.safeWrite { itRealm ->
                    teamSession = itRealm.createObject(TeamSession::class.java, itTeamId)
                    teamSession?.teamId = teamId
                    teamSession?.currentLayout = R.drawable.soccer_field
                    teamSession?.layoutList = RealmList(R.drawable.soccer_field)
                    teamSession?.playerIds = RealmList()
                    teamSession?.deckListIds = RealmList()
                    teamSession?.formationListIds = RealmList()
                    teamSession?.let {
                        itRealm.insertOrUpdate(it)
                    }
                }
            }
        }
    }

    private fun setupTeamRealmListener() {
        val teamListener = RealmChangeListener<RealmResults<Team>> {
            // Handle changes to the Realm data here
            log("Team listener called")
            it.find { it.id == teamId }?.let { team ->
                realmInstance?.safeWrite { itRealm ->
                    teamSession?.rosterId = team.rosterId
                }
                realmTeamCallBack?.invoke(team)
            }
        }
        realmInstance?.where(Team::class.java)?.findAllAsync()?.addChangeListener(teamListener)
    }

    private fun setupTryOutRealmListener() {
        val tryoutListener = RealmChangeListener<RealmResults<TryOut>> {
            // Handle changes to the Realm data here
            log("TryOut listener called")
            it.find { it.teamId == teamId }?.let { tryout ->
                realmInstance?.safeWrite { itRealm ->
                    teamSession?.tryout = tryout
                }
            }
        }
        realmInstance?.where(TryOut::class.java)?.findAllAsync()?.addChangeListener(tryoutListener)
    }

    private fun updateTeamSession(roster: Roster) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite { itRealm ->
                fs.roster = roster
                fs.playerIds?.clear()
                val loadDeck = fs.deckListIds.isNullOrEmpty()
                roster.players?.forEach {
                    fs.playerIds?.safeAdd(it.id)
                    if (loadDeck) {
                        fs.deckListIds?.safeAdd(it.id)
                    }
                }
            }
        }
    }
    private fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                it.find { it.id == rosterId }?.let { roster ->
                    updateTeamSession(roster)
                    realmRosterCallBack?.invoke(roster)
                }

            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}




class TeamMenuPopupProvider(private val fragment: Fragment, private val teamId: String) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_team_menu_dropdown, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_options -> {
                showCustomPopup(fragment.requireActivity().findViewById(R.id.menuitem_options))
                return true
            }
            else -> {
            }
        }
        return false
    }

    private fun showCustomPopup(anchorView: View) {
        val popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.team_menu_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Load animations
        val unfoldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.unfold)
        val foldAnimation = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.fold)

        // Set up click listeners for the custom menu items
        popupView.findViewById<LinearLayout>(R.id.option_formation).setOnClickListener {
            fragment.toFragmentWithIds(R.id.navigation_tryout_frag, teamId)
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.option_roster).setOnClickListener {
            fragment.toFragmentWithIds(R.id.navigation_roster_builder_frag, teamId)
            popupWindow.dismiss()
        }

        // If you want to dismiss the popup when clicking outside of it
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Set the unfold animation when showing the popup
        popupWindow.contentView.startAnimation(unfoldAnimation)

        // Set the fold animation when dismissing the popup
        popupWindow.setOnDismissListener {
            popupView.startAnimation(foldAnimation)
        }

        // Show the PopupWindow below the anchor view (menu item)
        popupWindow.showAsDropDown(anchorView)
    }
}
