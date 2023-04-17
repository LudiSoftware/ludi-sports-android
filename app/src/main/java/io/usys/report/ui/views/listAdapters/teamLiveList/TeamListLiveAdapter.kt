package io.usys.report.ui.views.listAdapters.teamLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.providers.liveData.TeamLiveData
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

    private var teamLiveData: TeamLiveData? = null

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.realmIds = realmIds
        this.parentFragment = fragment
        setupLiveData()
    }

    /** Firebase/Realm: Team LiveData **/
    private fun setupLiveData() {
        parentFragment?.let { fragment ->
            teamLiveData = TeamLiveData(realmIds, realmInstance, fragment.viewLifecycleOwner).apply {
                enable()
            }
        }
        observeFirebaseTeams()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeFirebaseTeams() {
        parentFragment?.let { fragment ->
            teamLiveData?.observe(fragment.viewLifecycleOwner) { teams ->
                log("Team results updated")
                teams.forEach {
                    itemList?.safeAdd(it)
                }
                notifyDataSetChanged()
            }
        }
    }

    /** Realm: Team Observer **/
    override fun observeRealmIds() {
        log("observeRealmIds")
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



