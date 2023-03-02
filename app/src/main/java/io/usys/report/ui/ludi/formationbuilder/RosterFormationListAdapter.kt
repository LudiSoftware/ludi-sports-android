package io.usys.report.ui.ludi.formationbuilder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
class RosterFormationListAdapter(private val itemList: RealmList<PlayerRef>,
                                 private val onItemMoved: (start: Int, end: Int) -> Unit,
                                 private val activity: Activity
)
    : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = itemList[position]?.id ?: "unknown"
        holder.textView.text = itemList[position]?.name
        itemList[position]?.imgUrl?.let {
            holder.imageView.loadUriIntoImgView(it)
        }
        // On Click
        holder.itemView.setOnClickListener {
            popPlayerProfileDialog(activity, playerId).show()
        }
        // On Long Click
        holder.itemView.setOnLongClickListener {
            holder.itemView.wiggleLong()
            val tempID = itemList[position]?.id
            holder.startClipDataDragAndDrop(tempID ?: "Unknown")
        }
    }

    fun removePlayer(playerId: String) {
        realm().executeTransaction {
            val player = itemList.find { it.id == playerId }
            player?.let {
                val index = itemList.indexOf(it)
                itemList.removeAt(index)
                notifyItemRemoved(index)
                this.notifyDataSetChanged()
            }
        }
    }

    fun addPlayer(player: PlayerRef) {
        realm().executeTransaction {
            itemList.add(player)
        }
        notifyItemInserted(itemList.size - 1)
    }

    override fun getItemCount() = itemList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is for movements within the recyclerView. Not for dragging to the soccer field.
        return true
    }

    override fun onItemDismiss(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class RosterFormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.bind(R.id.cardPlayerTinyTxtName)
        val imageView: ImageView = itemView.bind(R.id.cardPlayerTinyImgProfile)
    }
}