package io.usys.report.ui.views.listAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.realm.observe
import io.usys.report.realm.observeTeam
import io.usys.report.realm.realm
import io.usys.report.realm.safeAdd
import io.usys.report.ui.ludi.team.viewholders.TeamSmallViewHolder
import io.usys.report.utils.log

/** Live List Model/Base Adapter **/
abstract class LudiBaseListAdapter<R,L,VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var parentFragment: Fragment? = null
    var mode: String? = null
    var touchEnabled: Boolean = false
    var layout: Int = 0
    protected var context: Context? = null
    protected val realmInstance = realm()
    var results: RealmResults<R>? = null
    var itemList: RealmList<L>? = RealmList()

    var realmIds = mutableListOf<String>()

    init { realmInstance.isAutoRefresh = true }

    abstract fun observeRealmIds()

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBind(holder, position)
    }
    abstract fun onBind(holder: VH, position: Int)
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