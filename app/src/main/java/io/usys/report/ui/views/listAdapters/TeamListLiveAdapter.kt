package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.log

/**
 * Dynamic Master RecyclerView Adapter
 */
open class TeamListLiveAdapter(): RecyclerView.Adapter<TeamSmallViewHolder>() {

    var realmInstance = realm()
    var parentFragment: Fragment? = null
    var teamIds = mutableListOf<String>()
    var teamList: RealmList<Team>? = RealmList()

    init {
        realmInstance.isAutoRefresh = true
    }

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.teamIds = realmIds
        this.parentFragment = fragment
        loadTeamIds()
    }

    private fun loadTeamIds() {
        for (id in teamIds) {
            realmInstance.observeTeam(parentFragment!!.viewLifecycleOwner) { results ->
                results.find { it.id == id }?.let {
                    log("Team results updated")
                    teamList?.safeAdd(it)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSmallViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_team_small, parent, false)
        return TeamSmallViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return teamList?.size ?: 0
    }

    override fun onBindViewHolder(holder: TeamSmallViewHolder, position: Int) {
        println("binding realmlist")
        teamList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1, fragment = parentFragment)
            }
        }
    }

}



