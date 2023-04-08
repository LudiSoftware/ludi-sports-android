package io.usys.report.ui.views.listAdapters

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.utils.getObjectType
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.log

/**
 * Convenience Methods for Displaying a Realm List
 */

inline fun <reified T> RecyclerView.loadInRealmListCallback(realmList: RealmList<T>?,
                                                            type: String,
                                                            noinline updateCallback: ((String, String) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, null, updateCallback)
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInCustomAttributes(realmList: RealmList<T>?,
                                                           type: String,
                                                           noinline itemOnClick: ((View, T) -> Unit)?
) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
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

inline fun <reified T> RecyclerView.loadInRealmList(realmList: RealmList<T>?,
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

fun RecyclerView.loadInRealmIds(realmIds: MutableList<String>?, fragment: Fragment) : RealmListAdapterSync? {
    val adapter = realmIds?.let { RealmListAdapterSync(it, fragment) }
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
//    val itemTouchHelper = ItemTouchHelper(RealmListTouchAdapter(adapter))
//    itemTouchHelper.attachToRecyclerView(this)
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
//    val itemTouchHelper = ItemTouchHelper(RealmListTouchAdapter(adapter))
//    itemTouchHelper.attachToRecyclerView(this)
    return adapter
}


inline fun <reified T> RecyclerView.loadInRealmListHorizontal(realmList: RealmList<T>?,
                                                              type: String,
                                                              noinline itemOnClick: ((View, T) -> Unit)?,
                                                              size:String="medium_grid") : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick, size)
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    this.adapter = adapter
    return adapter
}

inline fun <reified T> RecyclerView.loadInRealmListGrid(realmList: RealmList<T>?,
                                                        type: String,
                                                        noinline itemOnClick: ((View, T) -> Unit)?) : RealmListAdapter<T>? {
    if (realmList.isNullOrEmpty()) return null
    val adapter = RealmListAdapter(realmList, type, itemOnClick)
    this.layoutManager = gridLayoutManager(this.context, 2)
    this.adapter = adapter
    return adapter
}