package io.usys.report.ui.views.listAdapters

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmResults
import io.usys.report.realm.realm
import io.usys.report.utils.log
import io.usys.report.utils.slideDown
import io.usys.report.utils.slideUp

/** Live List Model/Base Adapter **/
abstract class LudiBaseListAdapter<R,L,VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var parentFragment: Fragment? = null
    var mode: String? = null
    var touchEnabled: Boolean = false
    var bindCounter: Int = 0
    var layout: Int = 0
    val realmInstance = realm()
    var results: RealmResults<R>? = null
    var itemList: RealmList<L>? = RealmList()
    var realmIds = mutableListOf<String>()
    var onClickCallbacks : MutableMap<String, ((Int) -> Unit)> = mutableMapOf()

    init { realmInstance.isAutoRefresh = true }

    abstract fun observeRealmIds()



    override fun onBindViewHolder(holder: VH, position: Int) {
        bindCounter++
        onBind(holder, position)
//        holder.itemView.setOnClickListener {
//            onClickCallbacks["onClick"]?.invoke(position)
//        }
//        holder.itemView.setOnLongClickListener {
//            onClickCallbacks["onLongClick"]?.invoke(position)
//            true
//        }
        if (bindCounter != itemCount) return
        bindCounter = 0
        onBindFinished()
    }
    abstract fun onBind(holder: VH, position: Int)

    fun onClick(callbackFunction: (Int) -> Unit) {
        onClickCallbacks["onClick"] = { position -> callbackFunction(position)}
    }
    fun onLongClick(callbackFunction: (Int) -> Unit) {
        onClickCallbacks["onLongClick"] = { position -> callbackFunction(position)}
    }
    fun getItemAt(position: Int): Any? {
        return itemList?.get(position)
    }
    open fun onBindFinished() {
        log("onBindFinished")
    }
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

class HideShowScrollListener(private val hideShowView: View) : RecyclerView.OnScrollListener() {
    private var isShown = true
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        // Check if the first item is visible and user is scrolling up
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && dy < 0) {
            hideShowView.slideDown()
            isShown = true
        }
        // Check if the first item is visible and user is scrolling down
        else if (layoutManager.findFirstCompletelyVisibleItemPosition() >= 2 && dy > 0) {
            hideShowView.slideUp()
            isShown = false
        }
    }
}



