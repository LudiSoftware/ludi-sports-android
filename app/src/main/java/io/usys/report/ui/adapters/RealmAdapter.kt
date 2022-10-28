package io.usys.report.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.db.FireDB
import io.usys.report.db.FireTypes


fun RecyclerView.initRealmList(realmList: RealmList<*>, context: Context, type: String, itemOnClick: ((View, RealmObject) -> Unit)?) : RealmListAdapter {
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}


open class RealmListAdapter(): RecyclerView.Adapter<RouterViewHolder>() {

    var itemClickListener: ((View, RealmObject) -> Unit)? = null
    var realmList: RealmList<*>? = null
    var layout: Int = R.layout.item_list_organization
    var type: String = FireDB.ORGANIZATIONS

    constructor(realmList: RealmList<*>?, type: String) : this() {
        this.realmList = realmList
        this.type = type
        this.layout = FireTypes.getLayout(type)
    }

    constructor(realmList: RealmList<*>?, type: String, itemClickListener: ((View, RealmObject) -> Unit)?) : this() {
        this.realmList = realmList
        this.type = type
        this.itemClickListener = itemClickListener
        this.layout = FireTypes.getLayout(type)
    }

    constructor(realmList: RealmList<*>?, type: String, layout: Int) : this() {
        this.realmList = realmList
        this.type = type
        this.layout = layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(itemView, type)
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
fun View.setOnRealmListener(itemClickListener: ((View, RealmObject) -> Unit)?, item: RealmObject) {
    this.setOnClickListener {
        itemClickListener?.invoke(this, item)
    }
}




