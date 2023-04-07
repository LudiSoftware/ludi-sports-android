package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetTeamProfileInBackground
import io.usys.report.realm.*
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.log

/**
 * Dynamic Master RecyclerView Adapter
 */


open class RealmListAdapter2(): RecyclerView.Adapter<TeamSmallViewHolder>() {

    var realmInstance = realm()
    var realmIds = mutableListOf<String>()
    var realmList: RealmList<RealmObject>? = RealmList()

    var layout: Int = R.layout.card_organization_medium2
    var type: String = FireTypes.ORGANIZATIONS
    var size: String = "small"

    fun runTeamIds(teamIds: MutableList<String>?) {
        realmIds = teamIds ?: mutableListOf()
        realmIds.forEach { teamId ->
            TeamRealmSingleEventListener(teamId = teamId, createNewTeamClicker())
            realmInstance.fireGetTeamProfileInBackground(teamId)
        }
    }

    // Updates From Team ID Come Here
    private fun <T:RealmObject> createNewTeamClicker() : ((obj:T) -> Unit) {
        return { obj ->
            log("Obj Updated")
            loadTeamsByIds()
        }
    }

    constructor(realmIds: MutableList<String>) : this() {
        this.realmIds = realmIds
        runTeamIds(realmIds)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSmallViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_team_small, parent, false)
        return TeamSmallViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: TeamSmallViewHolder, position: Int) {
        println("binding realmlist")
        realmList?.let {
            it[position]?.let { it1 ->

                when (it1) {
                    is Team -> {
                        log("Team")
                        holder.bind(it1)
                    }
                    is Organization -> {
                        log("Organization")
                    }
                }

            }
        }
    }

    private fun loadTeamsByIds() {
        realmIds.forEach { id ->
            realmInstance.findTeamById(id)?.let { team ->
                realmList?.add(team as? Team)
            }
        }
        notifyDataSetChanged()
    }

}

class TeamRealmSingleEventListener(val teamId: String, private val onRealmChange: (teamId: Team) -> Unit) : RealmChangeListener<Team> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var teamResult: Team

    init {
        registerListener()
    }

    override fun onChange(t: Team) {
        log("Team listener called")
        val r = t as? Team
        if (teamId != r?.id) return
        unregisterListener()
        onRealmChange(t)
    }

    private fun registerListener() {
        teamResult = realm.where(Team::class.java).equalTo("id", teamId).findFirstAsync()
        teamResult.addChangeListener(this)
    }

    private fun unregisterListener() {
        teamResult.removeChangeListener(this)
        realm.close()
    }
}



