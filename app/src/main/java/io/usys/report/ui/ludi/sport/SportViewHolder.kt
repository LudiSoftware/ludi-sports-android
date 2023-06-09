package io.usys.report.ui.ludi.sport

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetBaseLudiObjects
import io.usys.report.realm.model.Sport
import io.usys.report.realm.model.addToSession
import io.usys.report.utils.views.bindTextView
import io.usys.report.utils.isNullOrEmpty
import io.usys.report.realm.sessionSports
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmList
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmListHorizontal

/**
 * SPORT LIST VIEW CONTROLS
 */

fun RealmList<Sport>?.containsItem(item: Sport?) : Boolean {
    if (item.isNullOrEmpty()) return false
    if (item == null) return false
    this?.let {
        if (it.isNullOrEmpty()) return false
        if (it.isEmpty()) return false
        if (it.size <= 0) return false
        if (it.contains(item)) return true
        it.forEach { itItem ->
            if (itItem == item) return true
            if (itItem.id == item.id) return true
        }
    }
    return false
}

fun RecyclerView?.setupSportList(onClickReturnViewRealmObject: ((View, Sport) -> Unit)?) {
    // Load Organizations by Sport
    val rv = this
    sessionSports {
        rv?.loadInRealmListHorizontal(it, FireTypes.SPORTS, onClickReturnViewRealmObject)
        return
    }
    var sportList: RealmList<Sport>? = RealmList()
    fireGetBaseLudiObjects<Sport>(FireTypes.SPORTS) {
        sportList = this ?: RealmList()
        sportList.addToSession()
        rv?.loadInRealmListHorizontal(sportList, FireTypes.SPORTS, onClickReturnViewRealmObject)
    }
}

class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtItemSportName = itemView.bindTextView(R.id.cardSportTxtSportName)
    fun bind(sport: Sport?) {
        sport?.let { txtItemSportName?.text = it.name }
    }
}
