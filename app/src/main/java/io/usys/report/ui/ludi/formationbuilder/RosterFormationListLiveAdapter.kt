package io.usys.report.ui.ludi.formationbuilder

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.local.RosterSession
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.setupRosterSession
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.ludi.roster.config.RosterConfig
import io.usys.report.ui.ludi.roster.config.RosterType
import io.usys.report.ui.views.gestures.onDownUpListener
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.inflateLayout
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log
import io.usys.report.utils.views.*

/**
 * RecyclerView Adapter
 *
 * 1. Original Full Roster of Players
 * 2. OnDeck Players List (Players Not Saved to the Formation Screen)
 * 3. Formation Players List (Players Currently Saved to the Formation Screen)
 * 4. Filtered Players List (Players WHO DO match filters BUT NOT in the Formation Screen)
 */
class RosterFormationListLiveAdapter() : RecyclerView.Adapter<RosterFormationListLiveAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {

    lateinit var realmInstance: Realm
    lateinit var config: RosterConfig
    // 1. OnDeck Players List (Players Not Saved to the Formation Screen)
    private var onDeckPlayerIdList: RealmList<String> = RealmList()
    // 2. Filtered Players List (Players WHO DO match filters BUT NOT in the Formation Screen)
    private var filteredOutPlayerIds: RealmList<String> = RealmList()


    constructor(rosterLayoutConfig: RosterConfig) : this() {
        this.config = rosterLayoutConfig
        this.realmInstance = rosterLayoutConfig.realmInstance
        (config.currentRosterId)?.let { realmInstance.setupRosterSession(it) }
        this.init()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.roster_player_card_substitute)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = onDeckPlayerIdList[position] ?: "unknown"
        realmInstance.findRosterById(config.currentRosterId)?.let { itRoster ->
            // Filtering System
            itRoster.players?.find { it.id == playerId }?.let { player ->
                //Normal Display Setup
                holder.textView?.text = player.name
                if (config.filters.isNullOrEmpty()) {
                    holder.filterTextView?.visibility = View.INVISIBLE
                } else {
                    holder.filterTextView?.text = player.foot
                }
                player.imgUrl?.let { itImgUrl ->
                    holder.profileImageView?.loadUriIntoImgView(itImgUrl)
                }
                holder.bannerNameImageView?.setPlayerTeamBackgroundBanner(player.color)

                holder.itemView.onDownUpListener({
                    println("onDown")
                    holder.itemView.wiggleOnce()
                    holder.startClipDataDragAndDrop(playerId)
                }, {
                    println("onSingleTapUp")
                    config.toPlayerProfile(playerId, config.currentRosterId!!)
                })
            }
        }
    }

    private fun init() {
        loadFromRosterSession()
        observeRosterSession()
    }

    /** Observe Roster Changes and Update List */
    private fun observeRosterSession() {
        realmInstance.observe<RosterSession>(config.parentFragment!!.viewLifecycleOwner) { results ->
            results.find { it.id == config.currentRosterId }?.let {
                log("Roster Session Live Updates")
                loadFromRosterSession()
            }
        }
    }

    fun switchRosterTo(rosterTypeStr:String) {
        val rawRosType = RosterType.values().find { it.type == rosterTypeStr }
        when (RosterType.valueOf(rawRosType.toString()).type) {
            RosterType.TRYOUT.type -> switchRosterToTryout()
            RosterType.OFFICIAL.type -> switchRosterToOfficial()
        }
    }

    fun switchRosterToTryout() {
        this.config.switchToTryoutRoster()
        config.currentRosterId?.let { this.realmInstance.setupRosterSession(it) }

        this.init()
    }

    fun switchRosterToOfficial() {
        this.config.switchToOfficialRoster()
        config.currentRosterId?.let { this.realmInstance.setupRosterSession(it) }
        this.init()
    }

    /** 1. Load in Deck List and Formation List
     *      - ALWAYS filter out players that are in the formation list.
     * **/
    @SuppressLint("NotifyDataSetChanged")
    private fun loadFromRosterSession() {
        this.filteredOutPlayerIds.clear()
        this.onDeckPlayerIdList.clear()
        this.realmInstance.rosterSessionById(config.currentRosterId) { fs ->
            fs.formationListIds?.let { this.filteredOutPlayerIds.safeAddAll(it) }
            fs.deckListIds?.let { this.onDeckPlayerIdList.safeAddAll(it) }
        }
        if (this.onDeckPlayerIdList.isEmpty()) {
            loadFromRoster()
        }
        notifyDataSetChanged()
    }
    private fun loadFromRoster() {
        this.filteredOutPlayerIds.clear()
        this.onDeckPlayerIdList.clear()
        val rosterPlayers = realmInstance.findPlayersInRosterById(config.currentRosterId) ?: return
        for (player in rosterPlayers) {
            this.onDeckPlayerIdList.safeAdd(player.id)
        }
    }




    /** Reset OnDeck to Original Roster **/
    @SuppressLint("NotifyDataSetChanged")
    fun resetDeckToRoster() {
        this.onDeckPlayerIdList.clear()
        this.realmInstance.rosterSessionById(config.currentRosterId) { fs ->
            this.realmInstance.safeWrite {
                fs.deckListIds?.clear()
                fs.formationListIds?.clear()
            }
            realmInstance.findRosterById(config.currentRosterId)?.let { itRoster ->
                itRoster.players?.let { rosterPlayers ->
                    for (player in rosterPlayers) {
                        this.addPlayerToDeck(player)
                    }
                }
            }
        }
        this.notifyDataSetChanged()
    }

    /** HELPER: Move Player  **/
    fun movePlayerToFormation(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance.safeWrite {
                it.rosterSessionById(config.currentRosterId) { fs ->
                    fs.deckListIds?.remove(playerId)
                    fs.formationListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }

    fun movePlayerToDeck(playerId: String) {
        this.realmInstance.safeWrite {
            it.rosterSessionById(config.currentRosterId) { fs ->
                fs.formationListIds?.remove(playerId)
                fs.deckListIds?.safeAdd(playerId)
            }
        }
        onDeckPlayerIdList.safeAdd(playerId)
    }
    private fun addPlayerToDeck(player: PlayerRef?, playerId: String? = null) {
        this.realmInstance.rosterSessionById(config.currentRosterId) { fs ->
            this.realmInstance.safeWrite {
                fs.deckListIds?.safeAdd(player?.id ?: playerId ?: "unknown")
            }
        }
        onDeckPlayerIdList.add(player?.id ?: playerId ?: "unknown")
    }


    override fun getItemCount() = onDeckPlayerIdList.size
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // This is for movements within the recyclerView. Not for dragging to the soccer field.
        return true
    }
    override fun onItemDismiss(position: Int) {
        onDeckPlayerIdList.removeAt(position)
        notifyItemRemoved(position)
    }
    /** ViewHolder **/
    inner class RosterFormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView? = itemView.bind(R.id.cardPlayerHor2TxtName)
        val filterTextView: TextView? = itemView.bind(R.id.cardPlayerHor2TxtFilter)
        val profileImageView: ImageView? = itemView.bind(R.id.cardPlayerHor2ImgProfile)
        val badgeImageView: ImageView? = itemView.bind(R.id.cardPlayerHor2ImgBadge)
        val bannerNameImageView: ImageView? = itemView.bind(R.id.cardPlayerHor2TxtImageBanner)
    }
}




