package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetTeamProfileInBackground
import io.usys.report.realm.*
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.TeamRealmSingleEventListener
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.log
import io.usys.report.utils.main

/**
 * Dynamic Master RecyclerView Adapter
 */


open class RealmListAdapterSync(): RecyclerView.Adapter<TeamSmallViewHolder>() {

    var realmInstance = realm()
    var realmIds = mutableListOf<String>()
    var realmList: RealmList<RealmObject>? = RealmList()

    var realmMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    var parentFragment: Fragment? = null
    var layout: Int = R.layout.card_organization_medium2
    var type: String = FireTypes.ORGANIZATIONS
    var size: String = "small"

    private fun runTeamIds() {
        realmIds.forEach { teamId ->
            realmInstance.findTeamById(teamId)?.let { team ->
                realmList?.add(team as? Team)
            } ?: kotlin.run {
                TeamRealmSingleEventListener(teamId = teamId, uiCallbackUpdater())
                realmInstance.fireGetTeamProfileInBackground(teamId)
            }

        }
    }

    // Updates From Team ID Come Here
    private fun uiCallbackUpdater() : ((obj:String) -> Unit) {
        return { obj ->
            log("Obj Updated")
            main {
                runTeamIds()
                notifyDataSetChanged()
            }

        }
    }

    private fun loadTeamsByIds() {
        realmIds.forEach { id ->
            realmInstance.findTeamById(id)?.let { team ->
                realmList?.add(team as? Team)
            }
        }
    }

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.realmIds = realmIds
        this.parentFragment = fragment
        runTeamIds()
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
                        holder.bind(it1, fragment = parentFragment)
                    }
                    is Organization -> {
                        log("Organization")
                    }
                }
            }
        }
    }

}





