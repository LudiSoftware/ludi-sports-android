package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.realm.findByField
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut
import io.usys.report.realm.model.users.User
import io.usys.report.realm.model.users.userOrLogout
import io.usys.report.realm.realm
import io.usys.report.realm.safeAdd
import io.usys.report.realm.safeWrite
import io.usys.report.ui.ludi.formationbuilder.TeamSession
import io.usys.report.ui.ludi.formationbuilder.teamSessionByTeamId
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 * * Make sure to add any new fragments to Navigation XML! * *
 */

abstract class LudiTeamFragment : Fragment() {

    companion object {
        const val TEAMID = "teamId"
    }
    var _MODE = YsrMode.TRYOUTS
    lateinit var rootView : View
    var menu: TeamMenuProvider? = null
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
//
    fun setupTeamRealmListener() {
        val teamListener = RealmChangeListener<RealmResults<Team>> {
            // Handle changes to the Realm data here
            log("Team listener called")
            it.find { it.id == teamId }?.let { team ->
                realmInstance?.safeWrite { itRealm ->
                    teamSession?.team = team
                    teamSession?.rosterId = team.rosterId
                }
                realmTeamCallBack?.invoke(team)
            }
        }
        realmInstance?.where(Team::class.java)?.findAllAsync()?.addChangeListener(teamListener)
    }
//
//
    fun setupTryOutRealmListener() {
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
//
    fun setupTeamRosterRealmListener() {
        val rosterListener = RealmChangeListener<RealmResults<Roster>> {
            // Handle changes to the Realm data here
            log("Roster listener called")
            rosterId?.let { rosterId ->
                it.find { it.id == rosterId }?.let { roster ->
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
                    realmRosterCallBack?.invoke(roster)
                }

            }
        }
        realmInstance?.where(Roster::class.java)?.findAllAsync()?.addChangeListener(rosterListener)
    }
}

class TeamMenuProvider(val fragment:Fragment, val teamId: String) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_team_menu, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuitem_formation -> {
                fragment.toFragmentWithId(R.id.navigation_tryout_frag, teamId)
                return true
            }
            else -> {}
        }
        return true
    }
}