package io.usys.report.ui.views.listAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.realm.*
import io.usys.report.realm.*
import io.usys.report.realm.model.PLAYER_STATUS_SELECTED
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.ui.ludi.player.ludiFilters
import io.usys.report.ui.ludi.player.sortByOrderIndex
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.ludi.roster.RosterPlayerViewHolder
import io.usys.report.ui.ludi.roster.RosterType
import io.usys.report.ui.views.touchAdapters.RosterLiveDragDropAction
import io.usys.report.utils.log


abstract class LudiBaseListAdapter<R,L,T : ViewHolder> : RecyclerView.Adapter<T>() {

    protected var recyclerView: RecyclerView? = null
    protected var context: Context? = null
    protected val realmInstance = realm()
    var results: RealmResults<R>? = null
    var itemList: RealmList<L>? = RealmList()

    init {
        realmInstance.isAutoRefresh = true
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        binder(holder, position)
    }

    abstract fun binder(holder: T, position: Int)

//    inline fun binder(holder: T, position: Int, block: (item: T, position: Int) -> Unit) {
//        return block(holder, position)
//    }
    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    protected fun onDestroy() {
        realmInstance.removeAllChangeListeners()
    }

    protected fun destroyObserver() {
        realmInstance.removeAllChangeListeners()
    }

//    protected fun attach() {
//        recyclerView?.layoutManager = GridLayoutManager(recyclerView?.context, 2)
//        recyclerView?.adapter = this
//    }

}
/**
 * Dynamic Master RecyclerView Adapter
 */
open class RosterListLiveAdapter(): LudiBaseListAdapter<Roster, PlayerRef, RosterPlayerViewHolder>() {

    var rosterId:String? = null
    var config: RosterConfig = RosterConfig()

    init {
        realmInstance.isAutoRefresh = true
    }

    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        this.setup()
    }

    private fun observeRosterPlayers() {
        results = realmInstance.observeRoster(config.parentFragment!!.viewLifecycleOwner) { results ->
            results.find { it.id == config.rosterId }?.let {
                log("Roster Live Updates")
                loadRosterById()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(config.layout, parent, false)
        return RosterPlayerViewHolder(itemView)
    }

    override fun binder(holder: RosterPlayerViewHolder, position: Int) {
        println("binding realmlist")
        itemList?.let {
            it[position]?.let { it1 ->
                if (config.mode == RosterType.TRYOUT.type) {
                    val result = holder.bindTryout(it1, adapter=this, counter=config.selectionCounter)
                    if (result) config.selectionCounter++
                } else if (config.mode == RosterType.SELECTED.type) {
                    holder.bindSelection(it1, position=position, rosterLimit=config.rosterSizeLimit)
                } else {
                    holder.bind(it1, position=position)
                }
            }
        }
    }

    /** Load Roster by ID */
    private fun loadRosterById() {
        realmInstance.findRosterById(config.rosterId)?.let { roster ->
            this.itemList = roster.players?.ludiFilters(config.filters)?.sortByOrderIndex()
            notifyDataSetChanged()
        }
    }

    protected fun attach() {
        config.recyclerView?.layoutManager = GridLayoutManager(recyclerView?.context, 2)
        config.recyclerView?.adapter = this
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setup() {
        destroyObserver()
        observeRosterPlayers()
        if (config.mode == RosterType.TRYOUT.type || config.mode == RosterType.SELECTED.type) getPlayersSelectedCount()
        if (config.touchEnabled) addTouchAdapters()
        attach()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        config.selectionCounter = 0
        notifyDataSetChanged()
    }

    private fun addTouchAdapters() {
        config.itemLiveTouchListener = RosterLiveDragDropAction(this)
        config.itemTouchHelper = ItemTouchHelper(config.itemLiveTouchListener!!)
        config.itemTouchHelper?.attachToRecyclerView(recyclerView)
    }


    /** Disable Functions */
    fun disableAndClearRosterList() {
        config.destroy()
        config = RosterConfig()
        this.itemList?.clear()
    }

    /** Filter Functions */
    fun filterByStatusSelected() {
        this.config.filters = ludiFilters("status" to PLAYER_STATUS_SELECTED)
        this.itemList = this.itemList?.ludiFilters(this.config.filters)
        notifyDataSetChanged()
    }

    /** Add Functions */
    private fun getPlayersSelectedCount() {
        realmInstance.findRosterById(config.rosterId)?.let { roster ->
            config.playersSelectedCount = roster.players?.ludiFilters(ludiFilters("status" to PLAYER_STATUS_SELECTED))?.count() ?: 0
        }
    }

    /** Update Functions */
    fun updateOrderIndexes() {
        itemList?.let { list ->
            // Perform updates in a Realm transaction
            realmInstance.safeWrite { _ ->
                list.forEachIndexed { index, playerRef ->
                    playerRef.orderIndex = index + 1
                }
            }
        }
    }

    /** Helper Functions */
    fun areTooManySelected() : Boolean {
        return config.playersSelectedCount > config.rosterSizeLimit
    }

}



