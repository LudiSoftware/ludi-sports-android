package io.usys.report.ui.tryouts

import android.content.ClipData
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarMenu
import io.usys.report.R
import io.usys.report.databinding.FragmentListFormationsLandscapeBinding
import io.usys.report.databinding.FragmentListFormationsPortraitBinding
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.Team
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.utils.bind
import io.usys.report.utils.views.*
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */

class TryoutTestFragment : YsrMiddleFragment() {

    var soccerFieldLayout: RelativeLayout? = null
    var rosterListRecyclerView: RecyclerView? = null

    private var _binding: FragmentListFormationsLandscapeBinding? = null
    private val binding get() = _binding!!
    private var _binding2: FragmentListFormationsPortraitBinding? = null
    private val binding2 get() = _binding2!!
    var isLandscape = false
    var rosterList = mutableListOf<String>()
    var formationList = mutableListOf<String>()

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

//        val imgv = ImageView(context)
//        imgv.setImageDrawable(getDrawable(context, R.drawable.usysr_logo))
        team = realmObjectArg as? Team
        // Show the navigation bar
        // Get a reference to the action bar and hide it
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        rootView = inflater.inflate(R.layout.fragment_list_formations_portrait, container, false)
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
                val item = formationList[start]
                formationList.removeAt(start)
                formationList.add(end, item)
            }

            val roster = team?.roster
            roster?.forEach {
                rosterList.add(it.name ?: "Anonymous")
            }

            val adapter = RosterListAdapter(rosterList, onItemDragged!!)
            rosterListRecyclerView?.layoutManager = linearLayoutManager(requireContext())
            rosterListRecyclerView?.adapter = adapter
//            val itemTouchHelperCallback = MyItemTouchHelperCallback(adapter)
//            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//            itemTouchHelper.attachToRecyclerView(rosterListRecyclerView)
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


    fun createOnDragListener() {

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
                        val itemName = clipData.getItemAt(0).text.toString()
                        val layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        val imgv = ImageView(context)
                        layoutParams.width = 200
                        layoutParams.height = 200
                        imgv.layoutParams = layoutParams
                        imgv.setImageDrawable(getDrawable(context, R.drawable.usysr_logo))
                        imgv.onMoveListener4(soccerFieldLayout!!)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

fun ImageView.resizeBy(width: Int, height: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    layoutParams.height = height
    this.layoutParams = layoutParams
}
fun Drawable.resize(width: Int, height: Int, resources:Resources): Drawable {
    val bitmap = Bitmap.createScaledBitmap(
        (this as BitmapDrawable).bitmap, width, height, false
    )
    return BitmapDrawable(resources, bitmap)
}

class MyItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

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

/**
 * RecyclerView Adapter
 */
class RosterListAdapter(private val itemList: MutableList<String>, private val onItemMoved: (start: Int, end: Int) -> Unit)
    : RecyclerView.Adapter<RosterListAdapter.MyViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_player_tiny, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = itemList[position]
        holder.itemView.setOnLongClickListener {
            val dragShadow = View.DragShadowBuilder(holder.itemView)
            val clipData = ClipData.newPlainText("item_name", itemList[position])
            holder.itemView.startDragAndDrop(clipData, dragShadow, holder, 0)
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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.bind(R.id.cardPlayerTinyTxtName)
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}



