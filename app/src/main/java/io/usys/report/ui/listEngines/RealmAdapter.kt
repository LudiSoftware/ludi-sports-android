package io.usys.report.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.utils.isNullOrEmpty

/**
 * Convenience Methods for Displaying a Realm List
 */

inline fun <reified T> RecyclerView.loadInRealmListCallback(realmList: RealmList<T>?, context: Context, type: String,
                                                    noinline updateCallback: ((String, String) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, null, updateCallback)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmList(realmList: RealmList<T>?, context: Context, type: String,
                                                    noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmListHorizontal(realmList: RealmList<T>?, context: Context, type: String,
                                                    noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmListGrid(realmList: RealmList<T>?, context: Context, type: String,
                                                    noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = GridLayoutManager(context, 2)
    this.adapter = adapter
    return adapter
}

/**
 * Dynamic Master RecyclerView Adapter
 */

open class RealmListAdapter<T>(): RecyclerView.Adapter<RouterViewHolder>() {

    var itemClickListener: ((View, T) -> Unit)? = null
    var updateCallback: ((String, String) -> Unit)? = null
    var realmList: RealmList<T>? = null
    var layout: Int = R.layout.card_organization
    var type: String = FireTypes.ORGANIZATIONS

    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.layout = RouterViewHolder.getLayout(type)
    }

    constructor(realmList: RealmList<T>?, type: String, itemClickListener: ((View, T) -> Unit)?, updateCallback: ((String, String) -> Unit)?) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.updateCallback = updateCallback
        this.layout = RouterViewHolder.getLayout(type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(itemView, type, updateCallback)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding realmlist")
        realmList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1 as RealmObject)
                holder.itemView.setOnRealmListener(itemClickListener, it1)
            }
        }
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



