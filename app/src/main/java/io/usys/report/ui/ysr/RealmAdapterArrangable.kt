package io.usys.report.ui.ysr
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import io.realm.RealmList
//import io.realm.RealmObject
//import io.usys.report.R
//import io.usys.report.firebase.FireTypes
//import io.usys.report.ui.RouterViewHolder
//import io.usys.report.utils.executeRealm
//import io.usys.report.utils.isNullOrEmpty
//
//
//
///**
// * Dynamic Master RecyclerView Adapter
// */
//
//open class RealmListAdapterArrangable<T>(): RecyclerView.Adapter<RouterViewHolder>(), ItemTouchHelperAdapter {
//
//    var itemClickListener: ((View, T) -> Unit)? = null
//    var updateCallback: ((String, String) -> Unit)? = null
//    var realmList: RealmList<T>? = null
//    var layout: Int = R.layout.card_organization
//    var type: String = FireTypes.ORGANIZATIONS
//
//    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?) : this() {
//        this.realmList = realmList
//        this.type = type
//        this.itemClickListener = itemClickListener
//        this.layout = RouterViewHolder.getLayout(type)
//    }
//
//    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?, updateCallback: ((String, String) -> Unit)?) : this() {
//        this.realmList = realmList
//        this.type = type
//        this.itemClickListener = itemClickListener
//        this.updateCallback = updateCallback
//        this.layout = RouterViewHolder.getLayout(type)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
//        return RouterViewHolder(itemView, type, updateCallback)
//    }
//
//    override fun getItemCount(): Int {
//        return realmList?.size ?: 0
//    }
//
//    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
//        println("binding realmlist")
//        realmList?.let {
//            it[position]?.let { it1 ->
//                holder.bind(it1 as RealmObject)
//                holder.itemView.setOnRealmListener(itemClickListener, it1)
//            }
//        }
//    }
//
//    override fun onItemMove(fromPosition: Int, toPosition: Int) {
//        realmList?.let { list ->
//            val from = list[fromPosition]
//            val to = list[toPosition]
//            executeRealm {
//                list[fromPosition] = to
//                list[toPosition] = from
//            }
//            notifyItemMoved(fromPosition, toPosition)
//        }
//    }
//
//    override fun onItemDismiss(position: Int) {
//        // Not needed for drag and drop
//    }
//
//    interface OnStartDragListener {
//        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
//    }
//
//    private val startDragListener: OnStartDragListener
//    fun setStartDragListener(listener: OnStartDragListener) {
//        startDragListener = listener
//    }
//
//}
//
///**
// * Pass a custom function and parameter.
// * Then pass the parameter into the custom function.
// * Invoke the function.
// * So, a callback system.
// */
////fun <T> View.setOnRealmListener(itemClickListener: ((View, T) -> Unit)?, item: T) {
////    this.setOnClickListener {
////        itemClickListener?.invoke(this, item)
////    }
////}
//
////private lateinit var itemTouchHelper: ItemTouchHelper
//val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
//    override fun isLongPressDragEnabled(): Boolean {
//        return true
//    }
//
//    override fun isItemViewSwipeEnabled(): Boolean {
//        return false
//    }
//
//    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        return makeMovementFlags(dragFlags, 0)
//    }
//
//    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//        val fromPosition = viewHolder.adapterPosition
//        val toPosition = target.adapterPosition
//
////        realmList?.let { realmList ->
////            realmList.move(fromPosition, toPosition)
////            notifyItemMoved(fromPosition, toPosition)
////            return true
////        }
//
//        return false
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        // not needed in this case
//    }
//})
//
//
