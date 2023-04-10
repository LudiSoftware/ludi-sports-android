package io.usys.report.ui.views.listAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import io.realm.*
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.*
import io.usys.report.realm.model.Organization
import io.usys.report.realm.model.Team
import io.usys.report.ui.ludi.team.TeamProvider
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log
import io.usys.report.utils.main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Dynamic Master RecyclerView Adapter
 */
open class RealmListAdapterSync(): RecyclerView.Adapter<TeamSmallViewHolder>() {

    var realmInstance = realm()
    var listener: RealmChangeListener<Team>? = null
    var realmIds = mutableListOf<String>()
    var realmList: RealmList<RealmObject>? = RealmList()

    var teamListener: RealmChangeListener<RealmResults<Team>>? = null
    var realmMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    var parentFragment: Fragment? = null
    var layout: Int = R.layout.card_organization_medium2
    var type: String = FireTypes.ORGANIZATIONS
    var size: String = "small"

    init {
        realmInstance.isAutoRefresh = true
    }
    private fun loadTeamIds() {
        for (id in realmIds) {
            val results = realmInstance.where(Team::class.java).findAllAsync()
            results.asLiveData().observe(parentFragment!!.viewLifecycleOwner) { updatedResults ->
                // Update your UI with the updatedResults
                updatedResults.find { it.id == id }?.let {
                    log("Team results updated")
                    realmList?.safeAdd(it)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupTeamRealmListener() {
        teamListener = RealmChangeListener {
            // Handle changes to the Realm data here
            log("Team listener called")
            if (realmList.isNullOrEmpty()) {
                parentFragment?.lifecycleScope?.launch {
                    realmList?.clear()
                    realmList?.addAll(it)
                    notifyDataSetChanged()
                }
            }
        }
        teamListener?.let {
            realmInstance.where(Team::class.java)?.findAllAsync()?.addChangeListener(it)
        }
    }

    constructor(realmIds: MutableList<String>, fragment: Fragment) : this() {
        this.realmIds = realmIds
        this.parentFragment = fragment
        loadTeamIds()
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

class RealmLiveData<T : RealmObject>(private val realmResults: RealmResults<T>) :
    LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
}

fun <T : RealmObject> RealmResults<T>.asLiveData() = RealmLiveData(this)


suspend inline fun retryLoading(maxRetries: Int = 3, retryCondition: () -> Boolean, retryBlock: () -> Unit) {
    var retryCount = 0
    while (retryCount < maxRetries && retryCondition()) {
        retryBlock()
        retryCount++
        delay(5000L) // Wait for 5 seconds before the next retry
    }
}

