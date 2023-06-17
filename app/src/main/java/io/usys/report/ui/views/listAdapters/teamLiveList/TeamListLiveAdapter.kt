package io.usys.report.ui.views.listAdapters.teamLiveList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import io.realm.*
import io.usys.report.R
import io.usys.report.providers.liveData.TeamLiveData
import io.usys.report.providers.liveData.safeObserve
import io.usys.report.providers.liveData.start
import io.usys.report.realm.*
import io.usys.report.realm.model.Team
import io.usys.report.ui.views.listAdapters.LudiBaseListAdapter
import io.usys.report.ui.views.listAdapters.realmList.gridLayoutManager
import io.usys.report.utils.log


fun RecyclerView.teamLiveAdapter(realmIds: MutableList<String>?, fragment: Fragment) : TeamListLiveAdapter? {
    val adapter = realmIds?.let { TeamListLiveAdapter(it, fragment) }
    this.layoutManager = gridLayoutManager(this.context, 2)
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
        teamLiveData = TeamLiveData(realmIds, realmInstance).start()
        observeRealmIds()
    }

    /** Realm: Team Observer **/
    @SuppressLint("NotifyDataSetChanged")
    override fun observeRealmIds() {
        log("observeRealmIds")
        teamLiveData?.safeObserve(parentFragment?.viewLifecycleOwner) { teams ->
            log("Team results updated")
            teams.forEach {
                itemList?.safeAdd(it)
            }
            notifyDataSetChanged()
        }
    }

    /** Setup ViewHolder **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSmallViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.team_card_list_medium, parent, false)
        return TeamSmallViewHolder(itemView!!)
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

    fun destroy() {
        teamLiveData?.disable()
    }

}



