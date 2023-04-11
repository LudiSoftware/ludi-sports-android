package io.usys.report.ui.ludi.formationbuilder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.*
import io.usys.report.realm.local.TeamSession
import io.usys.report.realm.local.rosterSessionById
import io.usys.report.realm.local.teamSessionByTeamId
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.fragments.bundleStringIds
import io.usys.report.ui.ludi.player.matchesLudiFilter
import io.usys.report.ui.ludi.roster.RosterConfig
import io.usys.report.ui.views.gestures.onDownUpListener
import io.usys.report.utils.bind
import io.usys.report.utils.inflateLayout
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
    var teamId: String? = null
    var rosterId: String? = null
    var realmInstance: Realm? = null
    var formationSessionId: String? = null
    var config: RosterConfig = RosterConfig("")
    // 2. OnDeck Players List (Players Not Saved to the Formation Screen)
    var onDeckPlayerIdList: RealmList<String> = RealmList()
    // 3/4. Filtered Players List (Players WHO DO match filters BUT NOT in the Formation Screen)
    private var filteredOutPlayerIds: RealmList<String> = RealmList()
    private var filters: MutableMap<String, String>? = null
    //
    var navController: NavController? = null

    fun reload(filters:MutableMap<String,String>?= null) {
        if (filters != null) {
            this.filters = filters
        }
        this.onDeckPlayerIdList = this.loadFormationSessionOnDeckList()
    }

//    private fun observeRosterPlayers() {
//        realmInstance?.observeRoster(config.parentFragment!!.viewLifecycleOwner) { results ->
//            results.find { it.id == config.rosterId }?.let {
//                log("Roster Live Updates")
//                loadRosterById()
//            }
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny_horizontal2)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val playerId = onDeckPlayerIdList[position] ?: "unknown"
        realmInstance?.findRosterById(rosterId ?: config.rosterId)?.let { itRoster ->
            // Filtering System
            itRoster.players?.find { it.id == playerId }?.let { player ->
                //Normal Display Setup
                holder.textView?.text = player.name
                filters?.let {
                    holder.filterTextView?.text = player.foot
                } ?: run {
                    holder.filterTextView?.visibility = View.INVISIBLE
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
                    navController?.navigate(R.id.navigation_player_profile, bundleStringIds(teamId, playerId, null))
                })
            }
        }
    }

    /** 1. Load in Roster
     *      - ALWAYS filter out players that are in the formation list.
     * **/
    private fun loadFormationSessionOnDeckList() : RealmList<String> {
        val tempList: RealmList<String> = RealmList()
        this.filteredOutPlayerIds.clear()
        this.onDeckPlayerIdList.clear()
        this.realmInstance?.rosterSessionById(rosterId) { fs ->
            this.formationSessionId = fs.id
            fs.formationListIds?.let { this.filteredOutPlayerIds.addAll(it) }
            //load from full roster
            realmInstance?.findRosterById(fs.rosterId)?.let { itRoster ->
                itRoster.players?.let { rosterPlayers ->
                    for (player in rosterPlayers) {
                        if (this.filteredOutPlayerIds.contains(player.id)) { continue }
                        if (filters != null) {
                            if (player.matchesLudiFilter(filters!!)) {
                                this.filteredOutPlayerIds.safeAdd(player.id)
                                continue
                            }
                        }
                        tempList.safeAdd(player.id)
                    }
                }
            }
        }
        return tempList
    }



    /** HELPER: Move Player  **/
    fun movePlayerToFormation(playerId: String) {
        onDeckPlayerIdList.indexOf(playerId).let { itIndex ->
            this.realmInstance?.safeWrite {
                it.rosterSessionById(rosterId ?: config.rosterId) { fs ->
                    fs.deckListIds?.remove(playerId)
                    fs.formationListIds?.safeAdd(playerId)
                }
            }
            onDeckPlayerIdList.remove(playerId)
            notifyItemRemoved(itIndex)
        }
    }

    /** Reset OnDeck to Original Roster **/
    fun resetDeckToRoster() {
        this.onDeckPlayerIdList.clear()
        this.realmInstance?.rosterSessionById(rosterId ?: config.rosterId) { fs ->
            this.formationSessionId = fs.id
            this.realmInstance?.safeWrite {
                fs.deckListIds?.clear()
                fs.formationListIds?.clear()
            }
            realmInstance?.findRosterById(fs.rosterId)?.let { itRoster ->
                itRoster.players?.let { rosterPlayers ->
                    for (playerId in rosterPlayers) {
                        this.addPlayer(playerId)
                    }
                }
            }
        }
        this.notifyDataSetChanged()
    }
    private fun addPlayer(player: PlayerRef) {
        this.realmInstance?.rosterSessionById(rosterId ?: config.rosterId) { fs ->
            this.realmInstance?.safeWrite {
                fs.deckListIds?.safeAdd(player.id)
            }
        }
        onDeckPlayerIdList.add(player.id)
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




