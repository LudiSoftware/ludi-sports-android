package io.usys.report.ui.ludi.formationbuilder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.ui.ludi.player.popPlayerProfileDialog
import io.usys.report.utils.bind
import io.usys.report.utils.inflateLayout
import io.usys.report.utils.views.*

/**
 * RecyclerView Adapter
 */
class RosterFormationListAdapter() : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {
    var realmInstance: Realm? = null
    var formationSessionId: String? = null
    var playerIdList: RealmList<String>? = null
    var onItemMoved: ((start: Int, end: Int) -> Unit)? = null
    var activity: Activity? = null

    constructor(realmInstance: Realm?, activity: Activity) : this() {
        this.realmInstance = realmInstance ?: realm()
        this.playerIdList = this.loadFormationSessionData()
        this.activity = activity
    }

    constructor(itemList: RealmList<String>, activity: Activity) : this() {
        this.playerIdList = itemList
        this.activity = activity
    }
    constructor(itemList: RealmList<String>, activity: Activity, onItemMoved: (start: Int, end: Int) -> Unit) : this() {
        this.playerIdList = itemList
        this.activity = activity
        this.onItemMoved = onItemMoved
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = playerIdList?.get(position) ?: "unknown"

        realmInstance?.getUserFormationSession { fs ->
            fs.rosterList?.find { it.id == playerId }?.let { player ->
                holder.textView.text = player.name
                player.imgUrl?.let { itImgUrl ->
                    holder.imageView.loadUriIntoImgView(itImgUrl)
                }
            }
        }

        // On Click
        holder.itemView.setOnClickListener {
            activity?.let { it1 -> popPlayerProfileDialog(it1, playerId).show() }
        }
        // On Long Click
        holder.itemView.setOnLongClickListener {
            holder.itemView.wiggleLong()
            holder.startClipDataDragAndDrop(playerId)
        }
    }

    private fun loadFormationSessionData() : RealmList<String>? {
        val tempList: RealmList<String>? = RealmList()
        this.realmInstance?.getUserFormationSession { fs ->
            this.formationSessionId = fs.id
            fs.rosterList?.let { itRosterList ->
                for (player in itRosterList) {
                    tempList?.add(player.id)
                }
            }
        }
        return tempList
    }
    fun removePlayer(playerId: String) {
        realmInstance?.executeTransaction {
            val player = playerIdList?.find { it == playerId }
            player?.let {
                val index = playerIdList?.indexOf(it)
                if (index != null) {
                    playerIdList?.removeAt(index)

                }
                if (index != null) {
                    notifyItemRemoved(index)
                }
                this.notifyDataSetChanged()
            }
        }
    }

    fun addPlayer(player: PlayerRef) {
        realmInstance?.executeTransaction {
            playerIdList?.add(player.id)
        }
        notifyItemInserted(playerIdList!!.size - 1)
    }

    override fun getItemCount() = playerIdList?.size ?: 0

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is for movements within the recyclerView. Not for dragging to the soccer field.
        return true
    }

    override fun onItemDismiss(position: Int) {
        playerIdList?.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class RosterFormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.bind(R.id.cardPlayerTinyTxtName)
        val imageView: ImageView = itemView.bind(R.id.cardPlayerTinyImgProfile)
    }
}