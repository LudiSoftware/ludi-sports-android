package io.usys.report.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.db.FireDB
import io.usys.report.db.FireTypes


fun RecyclerView.initRealmList(realmList: RealmList<*>, context: Context, type: String) : BaseListAdapter {
    val adapter = BaseListAdapter(realmList, type)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

open class BaseListAdapter(): RecyclerView.Adapter<RouterViewHolder>() {

    var realmList: RealmList<*>? = null
    var layout: Int = R.layout.item_list_organization
    var type: String = FireDB.ORGANIZATIONS

    constructor(realmList: RealmList<*>?, type: String) : this() {
        this.realmList = realmList
        this.type = type
        this.layout = FireTypes.getLayout(type)
    }

    constructor(realmList: RealmList<*>?, type: String, layout: Int) : this() {
        this.realmList = realmList
        this.type = type
        this.layout = layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(view, type)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding realmlist")
        realmList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1 as RealmObject)
            }
        }
    }

}


