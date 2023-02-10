package io.usys.report.ui.ysr.sport

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetBaseYsrObjects
import io.usys.report.model.Sport
import io.usys.report.model.addToSession
import io.usys.report.utils.loadInRealmList
import io.usys.report.utils.bindTextView
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.utils.sessionSports

/**
 * SPORT LIST VIEW CONTROLS
 */

fun RealmList<Sport>?.containsItem(item: Sport?) : Boolean {
    if (item.isNullOrEmpty()) return false
    if (item == null) return false
    this?.let {
        if (it.isNullOrEmpty()) return false
        if (it.count() <= 0) return false
        if (it.size <= 0) return false
        if (it.contains(item)) return true
        it.forEach { itItem ->
            if (itItem == item) return true
            if (itItem.id == item.id) return true
        }
    }
    return false
}

fun <T> RealmList<T>?.containsItem(item: T?) : Boolean {
    if (item.isNullOrEmpty()) return false
    if (item == null) return false
    this?.let {
        if (it.isNullOrEmpty()) return false
        if (it.count() <= 0) return false
        if (it.size <= 0) return false
        if (it.contains(item)) return true
        it.forEach { itItem ->
            if (itItem == item) return true
        }
    }
    return false
}

fun RecyclerView?.setupSportList(context: Context, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
    // Load Organizations by Sport
    val rv = this
    sessionSports {
        rv?.loadInRealmList(it, context, FireTypes.SPORTS, onClickReturnViewRealmObject)
        return
    }
    var sportList: RealmList<Sport>? = RealmList()
    fireGetBaseYsrObjects<Sport>(FireTypes.SPORTS) {
        sportList = this ?: RealmList()
        sportList.addToSession()
        rv?.loadInRealmList(sportList, context, FireTypes.SPORTS, onClickReturnViewRealmObject)
    }
}

class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemSportName = itemView.bindTextView(R.id.cardSportTxtSportName)

    fun bind(sport: Sport?) {
        sport?.let { txtItemSportName?.text = it.name }
    }
}
