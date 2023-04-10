package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.log
import kotlinx.coroutines.delay

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RealmListAdapterSync(): RecyclerView.Adapter<TeamSmallViewHolder>() {

    var realmInstance = realm()
    var listener: RealmChangeListener<Team>? = null
    var realmIds = mutableListOf<String>()
    var realmList: RealmList<RealmObject>? = RealmList()

    var parentFragment: Fragment? = null
    var layout: Int = R.layout.card_organization_medium2
    var type: String = FireTypes.ORGANIZATIONS
    var size: String = "small"

    init {
        realmInstance.isAutoRefresh = true
    }

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.realmIds = realmIds
        this.parentFragment = fragment
        loadTeamIds()
    }

    private fun loadTeamIds() {
        for (id in realmIds) {
            realmInstance.observe<Team>(parentFragment!!.viewLifecycleOwner) { results ->
                results.find { it.id == id }?.let {
                    log("Team results updated")
                    realmList?.safeAdd(it)
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

suspend inline fun retryLoading(maxRetries: Int = 3, retryCondition: () -> Boolean, retryBlock: () -> Unit) {
    var retryCount = 0
    while (retryCount < maxRetries && retryCondition()) {
        retryBlock()
        retryCount++
        delay(5000L) // Wait for 5 seconds before the next retry
    }
}

