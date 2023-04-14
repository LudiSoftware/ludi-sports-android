package io.usys.report.ui.views.listAdapters.teamLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.ui.views.listAdapters.LudiBaseListAdapter
import io.usys.report.ui.views.listAdapters.linearLayoutManager
import io.usys.report.utils.log


fun RecyclerView.loadInTeamIds(realmIds: MutableList<String>?, fragment: Fragment) : TeamListLiveAdapter? {
    val adapter = realmIds?.let { TeamListLiveAdapter(it, fragment) }
    this.layoutManager = linearLayoutManager(this.context)
    this.adapter = adapter
    return adapter
}

/**
 * Dynamic Master RecyclerView Adapter
 *  - Pass in
 *    - RealmIds
 *    - Parent Fragment
 */
open class TeamListLiveAdapter(): LudiBaseListAdapter<Team, Team, TeamSmallViewHolder>() {

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.realmIds = realmIds
        this.parentFragment = fragment
        this.observeRealmIds()
    }

    /** Team/Object Observer **/
    @SuppressLint("NotifyDataSetChanged")
    override fun observeRealmIds() {
        for (id in realmIds) {
            realmInstance.observeTeam(parentFragment!!.viewLifecycleOwner) { results ->
                results.find { it.id == id }?.let {
                    log("Team results updated")
                    itemList?.safeAdd(it)
                    notifyDataSetChanged()
                }
            }
        }
    }

    /** Setup ViewHolder **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSmallViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.team_card_list_medium, parent, false)
        return TeamSmallViewHolder(itemView)
    }

    /** Bind ViewHolder **/
    override fun onBind(holder: TeamSmallViewHolder, position: Int) {
        println("binding realm object")
        itemList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1, fragment = parentFragment)
            }
        }
    }

}



