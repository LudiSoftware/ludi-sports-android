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
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.realm
import io.usys.report.realm.safeAdd
import io.usys.report.realm.safeWrite
import io.usys.report.utils.bind
import io.usys.report.utils.inflateLayout
import io.usys.report.utils.views.*

/**
 * RecyclerView Adapter
 */
class RosterFormationListAdapter() : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {
    var teamId: String? = null
    var realmInstance: Realm? = null
    var formationSessionId: String? = null
    var onDeckPlayerIdList: RealmList<String> = RealmList()
    var onItemMoved: ((start: Int, end: Int) -> Unit)? = null
    var activity: Activity? = null

    constructor(teamId: String, realmInstance: Realm?, activity: Activity) : this() {
        this.teamId = teamId
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
        realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            fs.roster?.players?.find { it.id == playerId }?.let { player ->
                holder.textView.text = player.name
                player.imgUrl?.let { itImgUrl ->
                    holder.imageView.loadUriIntoImgView(itImgUrl)
                }
                holder.itemView.setPlayerTeamBackgroundColor(player.color)
            }
        }

        // On Click
        holder.itemView.setOnClickListener {
            activity?.let { it1 ->
                //todo: use profile fragment now.
//                popPlayerProfileDialog(it1, playerId).show()
            }
        }
        // On Long Click
        holder.itemView.setOnLongClickListener {
            holder.itemView.wiggleOnce()
            holder.startClipDataDragAndDrop(playerId)
        }
    }

    fun resetDeckToRoster() {
        this.onDeckPlayerIdList.clear()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            this.realmInstance?.safeWrite {
                fs.deckListIds?.clear()
                fs.formationListIds?.clear()
            }
            fs.roster?.players?.let { rosterPlayers ->
                for (playerId in rosterPlayers) {
                    this.addPlayer(playerId)
                }
            }
        }
        this.notifyDataSetChanged()
    }

    private fun loadFormationSessionData() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.formationSessionId = fs.id
            fs.deckListIds?.let { deckList ->
                for (playerId in deckList) {
                    if (tempList.contains(playerId)) { continue }
                    tempList.safeAdd(playerId)
                }
            }
        }
        return tempList
    }
    fun movePlayerToField(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance?.safeWrite {
                it.teamSessionByTeamId(teamId!!) { fs ->
                    fs.deckListIds?.remove(playerId)
                    fs.formationListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }
    fun movePlayerToDeck(playerId: String) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(playerId)
                fs.formationListIds?.remove(playerId)
            }
        }
        onDeckPlayerIdList.add(playerId)
        notifyItemInserted(onDeckPlayerIdList.size - 1)
    }
    fun removePlayer(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
                this.realmInstance?.safeWrite {
                    fs.deckListIds?.remove(playerId)
                    fs.blackListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }

    fun addPlayer(player: PlayerRef) {
        this.realmInstance?.teamSessionByTeamId(teamId!!) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(player.id)
            }
        }
        onDeckPlayerIdList.add(player.id)
//        notifyItemInserted(onDeckPlayerIdList.size - 1)
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