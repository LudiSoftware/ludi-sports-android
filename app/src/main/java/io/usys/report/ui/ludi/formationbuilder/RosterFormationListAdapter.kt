package io.usys.report.ui.ludi.formationbuilder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    var onDeckPlayerIdList: RealmList<String> = RealmList()
    var removedPlayerIdList: RealmList<String> = RealmList()
    var onFieldPlayerIdList: RealmList<String> = RealmList()
    var onItemMoved: ((start: Int, end: Int) -> Unit)? = null
    var activity: Activity? = null

    constructor(realmInstance: Realm?, activity: Activity) : this() {
        this.realmInstance = realmInstance ?: realm()
        this.onDeckPlayerIdList = this.loadFormationSessionData()
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = onDeckPlayerIdList[position] ?: "unknown"

        realmInstance?.getUserFormationSession { fs ->
            fs.playerList?.find { it.id == playerId }?.let { player ->
                holder.textView.text = player.name
                player.imgUrl?.let { itImgUrl ->
                    holder.imageView.loadUriIntoImgView(itImgUrl)
                }
                if (player.color == "red") {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.imageView.context, R.color.ysrFadedRed))
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.imageView.context, R.color.ysrFadedBlue))
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

    private fun loadFormationSessionData() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.realmInstance?.getUserFormationSession { fs ->
            this.formationSessionId = fs.id
            fs.playerList?.let { itRosterList ->
                for (player in itRosterList) {
                    tempList.add(player.id)
                }
            }
        }
        return tempList
    }
    fun movePlayerToField(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            onDeckPlayerIdList.remove(playerId)
            onFieldPlayerIdList.add(playerId)
            notifyItemRemoved(itIndex)
        }
    }
    fun addPlayerFromField(playerId: String) {
        onDeckPlayerIdList.add(playerId)
        onFieldPlayerIdList.remove(playerId)
        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }
    fun removePlayer(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            onDeckPlayerIdList.remove(playerId)
            removedPlayerIdList.add(playerId)
            notifyItemRemoved(itIndex)
        }
    }

    fun addPlayer(player: PlayerRef) {
        onDeckPlayerIdList.add(player.id)
        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }

    override fun getItemCount() = onDeckPlayerIdList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is for movements within the recyclerView. Not for dragging to the soccer field.
        return true
    }

    override fun onItemDismiss(position: Int) {
        onDeckPlayerIdList?.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class RosterFormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.bind(R.id.cardPlayerTinyTxtName)
        val imageView: ImageView = itemView.bind(R.id.cardPlayerTinyImgProfile)
    }
}