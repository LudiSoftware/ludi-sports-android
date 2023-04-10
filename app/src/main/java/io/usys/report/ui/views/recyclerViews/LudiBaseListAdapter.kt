package io.usys.report.ui.views.recyclerViews

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmResults
import io.usys.report.realm.realm

abstract class LudiBaseListAdapter<R,L,T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    protected var context: Context? = null
    protected val realmInstance = realm()
    var results: RealmResults<R>? = null
    var itemList: RealmList<L>? = RealmList()

    init {
        realmInstance.isAutoRefresh = true
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        onBind(holder, position)
    }

    abstract fun onBind(holder: T, position: Int)

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    protected fun onDestroy() {
        realmInstance.removeAllChangeListeners()
    }

    protected fun destroyObserver() {
        realmInstance.removeAllChangeListeners()
    }

}