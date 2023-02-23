package io.usys.report.ui.tryouts

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.usys.report.R
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.getPlayerFromRoster
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.utils.bind
import io.usys.report.utils.log
import io.usys.report.utils.views.*
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */

class RosterFormationFragment : YsrMiddleFragment() {

    var adapter: RosterFormationListAdapter? = null
    var soccerFieldLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null

    var rosterList = mutableListOf<PlayerRef>()
    var formationList = mutableListOf<PlayerRef>()

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
        team = realmObjectArg as? Team
        //Hiding the action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        rootView = inflater.inflate(R.layout.fragment_list_formations_portrait, container, false)
        //Hiding the bottom navigation bar
        val item = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        item?.visibility = View.GONE
        setupDisplay()
        return rootView
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
            roster?.forEach {
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
         *
         */
        val dragListener = View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Do nothing
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.background = ContextCompat.getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.background = ContextCompat.getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        //TODO: Fix this to get the correct realm object
                        val playerId = clipData.getItemAt(0).text.toString()
                        var tempPlayer = PlayerRef()
                        team?.getPlayerFromRoster(playerId)?.let {
                            tempPlayer = it
                            formationList.add(it)
                            this.adapter?.removeItem(it.id!!)
                        }
                        val layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        //TODO: What exactly do we put here, the entire item from the list?
                        val imgv = ImageView(context)
                        layoutParams.width = 200
                        layoutParams.height = 200
                        imgv.layoutParams = layoutParams
                        imgv.setImageDrawable(getDrawable(context, R.drawable.usysr_logo))
                        imgv.onMoveListenerRosterFormation(soccerFieldLayout!!)
                        soccerFieldLayout?.addView(imgv)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.background = ContextCompat.getDrawable(requireContext(), R.drawable.soccer_field)
                    true
                }
                else -> false
            }
        }
        soccerFieldLayout?.setOnDragListener(dragListener)
    }


}

class RosterFormationTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

//    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//        val fromPosition = viewHolder.adapterPosition
//        val toPosition = target.adapterPosition
//        log(toPosition)
//        return true
//    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_player_tiny, parent, false)
        return RosterFormationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RosterFormationViewHolder, position: Int) {
        val currentPlayerRef: PlayerRef = itemList[position]
        holder.textView.text = itemList[position].name
        holder.itemView.setOnClickListener {
            currentPlayerRef.showPlayerProfile(activity)
        }
        holder.itemView.setOnLongClickListener {
            val dragShadow = View.DragShadowBuilder(holder.itemView)
            val clipData = ClipData.newPlainText("playerId", itemList[position].id)
            holder.itemView.startDragAndDrop(clipData, dragShadow, holder, 0)
        }
    }

    fun removeItem(playerId: String) {
        val player = itemList.find { it.id == playerId }
        player?.let {
            val index = itemList.indexOf(it)
            itemList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    override fun getItemCount() = itemList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(itemList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(itemList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        onItemMoved(fromPosition, toPosition)
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

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}



