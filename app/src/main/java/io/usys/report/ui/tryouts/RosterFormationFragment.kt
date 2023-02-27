package io.usys.report.ui.tryouts

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.realm.RealmListAdapter
import io.usys.report.realm.findByField
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.model.*
import io.usys.report.realm.writeToRealmOnMain
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.ui.ysr.player.popPlayerProfileDialog
import io.usys.report.utils.*
import io.usys.report.utils.views.*
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : YsrMiddleFragment() {

    companion object {
        const val TAG = "Formation"
        fun newInstance(): RosterFormationFragment {
            return RosterFormationFragment()
        }
    }

    var adapter: RosterFormationListAdapter? = null
    var soccerFieldLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null

    var rosterList = mutableListOf<PlayerRef>()
    var formationList = mutableListOf<PlayerRef>()

    var dragListener: View.OnDragListener? = null
    var onItemDragged: ((start: Int, end: Int) -> Unit)? = null
    var team: Team? = null

    var container: ViewGroup? = null
    var inflater: LayoutInflater? = null

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.inflater = inflater
        this.container = container
        team = realmInstance?.findByField("id", "9374e9f6-53ce-4ca5-90c6-cd613ad52c6a")
        //Hiding the action bar
        hideLudiActionBar()

        if (container == null) {
            val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.teamViewPager)
            rootView = teamContainer?.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        } else {
            rootView = container.inflateLayout(R.layout.fragment_list_formations_portrait)!!
        }
        //Hiding the bottom navigation bar
        hideLudiNavView()
        setupDisplay()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideLudiActionBar()
        hideLudiNavView()
    }
    private fun setupDisplay() {

        activity?.window?.let {
            soccerFieldLayout = rootView.findViewById(R.id.tryoutsRootViewRosterFormation)
            rosterListRecyclerView = rootView.findViewById(R.id.ysrTORecycler)

            onItemDragged = { start, end ->
//                val item = formationList[start]
//                formationList.removeAt(start)
//                formationList.add(end, item)
            }


            val roster = team?.roster
            roster?.players?.forEach {
                val test = it.name
                log("Player: $test")
                rosterList.add(it)
            }

            adapter = RosterFormationListAdapter(rosterList, onItemDragged!!, requireActivity())
            rosterListRecyclerView?.layoutManager = linearLayoutManager(requireContext())
            rosterListRecyclerView?.adapter = adapter
            val itemTouchHelperCallback = RosterFormationTouchHelperCallback(adapter!!)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(rosterListRecyclerView)
            createOnDragListener()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.inflater?.inflate(R.layout.fragment_list_formations_landscape, container, false)
            setupDisplay()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.inflater?.inflate(R.layout.fragment_list_formations_portrait, container, false)
            setupDisplay()
        }
    }


    private fun createOnDragListener() {
        /**
         * Drag Listener for when a player is dragged onto the soccer field
         */
        dragListener = View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Do nothing
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        //TODO: Fix this to get the correct realm object
                        val playerId = clipData.getItemAt(0).text.toString()
                        adapter?.removePlayer(playerId)
                        var tempPlayer = PlayerRef()
                        team?.getPlayerFromRosterNoThread(playerId)?.let {
                            tempPlayer = it
                            formationList.add(it)
                        }
                        val layoutParams = getRelativeLayoutParams()
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        val tempView = inflateView(requireContext(), R.layout.card_player_tiny)
                        tempView.tag = tempPlayer.id
                        val playerName = tempView.findViewById<TextView>(R.id.cardPlayerTinyTxtName)
                        val playerIcon = tempView.findViewById<ImageView>(R.id.cardPlayerTinyImgProfile)
                        playerName.text = tempPlayer.name
                        layoutParams.width = 300
                        layoutParams.height = 75
                        tempView.layoutParams = layoutParams
                        playerIcon.setImageDrawable(getDrawable(context, R.drawable.usysr_logo))
//                        tempView.enablePinchToZoom()
                        tempView.onMoveListenerRosterFormation(width = 300, height = 75)
                        soccerFieldLayout?.addView(tempView)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.background = getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                else -> false
            }
        }
        soccerFieldLayout?.setOnDragListener(dragListener)
    }


}

/**
 * RecyclerView Adapter
 */
class RosterFormationListAdapter(private val itemList: MutableList<PlayerRef>,
                                 private val onItemMoved: (start: Int, end: Int) -> Unit,
                                 private val activity: Activity)
    : RecyclerView.Adapter<RosterFormationListAdapter.RosterFormationViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterFormationViewHolder {
        val view = parent.inflateLayout(R.layout.card_player_tiny)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val currentPlayerRef = itemList[position] as PlayerRef
        holder.textView.text = itemList[position].name
        // On Click
        holder.itemView.setOnClickListener {
            popPlayerProfileDialog(activity, "9374e9f6-53ce-4ca5-90c6-cd613ad52c6a", itemList[position].playerId!!).show()
        }
        // On Long Click
        holder.itemView.setOnLongClickListener {
            val tempID = itemList[position].id
            holder.startClipDataDragAndDrop(tempID ?: "Unknown")
        }
    }

    fun removePlayer(playerId: String) {
        val player = itemList.find { it.id == playerId }
        player?.let {
            val index = itemList.indexOf(it)
            itemList.removeAt(index)
            notifyItemRemoved(index)
            this.notifyDataSetChanged()
        }
    }

    fun addPlayer(player: PlayerRef) {
        itemList.add(player)
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
    }
}


/**
 * ItemTouchHelperCallback
 * -> This is for movements within the recyclerView. Not for dragging to the soccer field.
 */
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}

class RosterFormationTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }
}