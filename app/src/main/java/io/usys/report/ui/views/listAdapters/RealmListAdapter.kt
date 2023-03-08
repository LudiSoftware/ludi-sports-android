package io.usys.report.ui.views.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.writeToRealmOnMain
import io.usys.report.ui.onClickReturnStringString
import io.usys.report.ui.onClickReturnViewT
import io.usys.report.ui.views.touchAdapters.RealmListTouchAdapter
import io.usys.report.utils.getObjectType
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log
import java.util.*

/**
 * Convenience Methods for Displaying a Realm List
 */

inline fun <reified T> RecyclerView.loadInRealmListCallback(realmList: RealmList<T>?,
                                                            context: Context,
                                                            type: String,
                                                    noinline updateCallback: ((String, String) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, null, updateCallback)
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInCustomAttributes(realmList: RealmList<T>?,
                                                           context: Context,
                                                           type: String,
                                                           noinline itemOnClick: ((View, T) -> Unit)?
) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInCustomAttributes(realmList: RealmList<T>?,
                                                           type: String,
                                                           noinline itemOnClick: ((View, T) -> Unit)?,
                                                           size:String = "small"
) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick, size)
    this.layoutManager = linearLayoutManager(this.context)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmListGridArrangable(realmList: RealmList<T>?,
                                                                  type: String,
                                                                  noinline itemOnClick: ((View, T) -> Unit)?,
                                                                  size:String="medium_grid"){
    if (realmList.isNullOrEmpty()) return
    val typer = realmList.getObjectType()
    log(typer)
    val adapter = RealmListAdapter(realmList, type, itemOnClick, size)
    this.layoutManager = GridLayoutManager(this.context, 2)
    this.adapter = adapter
    val itemTouchHelper = ItemTouchHelper(RealmListTouchAdapter(adapter))
    itemTouchHelper.attachToRecyclerView(this)
}

inline fun <reified T> RecyclerView.loadInRealmListArrangable(realmList: RealmList<T>?,
                                                         type: String,
                                                         noinline itemOnClick: ((View, T) -> Unit)?
) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    val gridLayoutManager = gridLayoutManager(this.context, 2)
    this.layoutManager = gridLayoutManager
    this.adapter = adapter
    val itemTouchHelper = ItemTouchHelper(RealmListTouchAdapter(adapter))
    itemTouchHelper.attachToRecyclerView(this)
    return adapter
}


inline fun <reified T> RecyclerView.loadInRealmListHorizontal(realmList: RealmList<T>?,
                                                              context: Context,
                                                              type: String,
                                                    noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmListGrid(realmList: RealmList<T>?,
                                                        context: Context,
                                                        type: String,
                                                    noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = gridLayoutManager(context, 2)
    this.adapter = adapter
    return adapter
}

/**
 * Dynamic Master RecyclerView Adapter
 */

fun linearLayoutManager(context: Context, isHorizontal:Boolean=false): LinearLayoutManager {
    if (isHorizontal) return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
}
fun gridLayoutManager(context: Context, columnCount:Int=2): GridLayoutManager {
    return GridLayoutManager(context, columnCount)
}

open class RealmListAdapter<T>(): RecyclerView.Adapter<RouterViewHolder>() {

    var gridLayoutManager: GridLayoutManager? = null
    var itemClickListener: ((View, T) -> Unit)? = onClickReturnViewT()
    var updateCallback: ((String, String) -> Unit)? = onClickReturnStringString()
    var realmList: RealmList<T>? = null
    var layout: Int = R.layout.card_organization_medium2
    var type: String = FireTypes.ORGANIZATIONS
    var size: String = "small"

    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.layout = RouterViewHolder.getLayout(type, size)
    }

    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?, size:String) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.layout = RouterViewHolder.getLayout(type, size)
        this.size = size
    }
    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?, gridLayoutManager: GridLayoutManager) : this() {
        this.gridLayoutManager = gridLayoutManager
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.layout = RouterViewHolder.getLayout(type, size)
    }

    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?, updateCallback: ((String, String) -> Unit)?) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.updateCallback = updateCallback
        this.layout = RouterViewHolder.getLayout(type, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(itemView, type, updateCallback, size)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding realmlist")
        realmList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1 as RealmObject, position=position)
                holder.itemView.setOnRealmListener(itemClickListener, it1)
            }
        }
    }

    fun addRealmObject(realmObject: T) {
        writeToRealmOnMain { realmList?.add(realmObject) }
        this.notifyItemInserted(realmList?.size ?: 0)
    }

    fun removeRealmObject(realmObject: T) {
        writeToRealmOnMain { realmList?.remove(realmObject) }
        this.notifyItemRemoved(realmList?.size ?: 0)
    }

}

fun <T> RealmListAdapter<T>.swapRealmObjects(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
    val fromPosition = viewHolder.adapterPosition
    val toPosition = target.adapterPosition
    val fromColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(fromPosition, this.gridLayoutManager!!.spanCount)
    val toColumn = this.gridLayoutManager?.spanSizeLookup?.getSpanIndex(toPosition, this.gridLayoutManager!!.spanCount)
    if (fromColumn == toColumn) {
        // Move the item within the same column
        writeToRealmOnMain { Collections.swap(this.realmList, fromPosition, toPosition) }
        this.notifyItemMoved(fromPosition, toPosition)
    } else {
        // Move the item to a different column
        writeToRealmOnMain { Collections.swap(this.realmList, fromPosition, toPosition) }
        this.notifyItemChanged(fromPosition)
        this.notifyItemChanged(toPosition)
    }
}

/**
 * Pass a custom function and parameter.
 * Then pass the parameter into the custom function.
 * Invoke the function.
 * So, a callback system.
 */
fun <T> View.setOnRealmListener(itemClickListener: ((View, T) -> Unit)?, item: T) {
    this.setOnClickListener {
        itemClickListener?.invoke(this, item)
    }
}









