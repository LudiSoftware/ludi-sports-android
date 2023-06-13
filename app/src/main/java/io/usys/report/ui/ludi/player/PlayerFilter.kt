package io.usys.report.ui.ludi.player

import io.realm.RealmList
import io.usys.report.realm.model.PlayerRef
import io.usys.report.utils.ludi.LudiAttribute
import io.usys.report.utils.ludi.LudiFilters
import io.usys.report.utils.ludi.LudiValue
import java.util.*

fun RealmList<PlayerRef>.filterByAttribute(filter: (PlayerRef) -> Boolean): RealmList<PlayerRef> {
    val filteredList = RealmList<PlayerRef>()

    for (playerRef in this) {
        if (filter(playerRef)) {
            filteredList.add(playerRef)
        }
    }

    return filteredList
}

fun ludiFilters(vararg pairs: Pair<LudiAttribute, LudiValue>): LudiFilters {
    return mutableMapOf(*pairs)
}

fun RealmList<PlayerRef>.ludiFilters(ludiFilters: MutableMap<String, String>?): RealmList<PlayerRef> {
    if (ludiFilters.isNullOrEmpty()) return this
    val filteredList = RealmList<PlayerRef>()
    for (playerRef in this) {
        if (playerRef.matchesLudiFilters(ludiFilters)) {
            filteredList.add(playerRef)
        }
    }
    return filteredList
}

fun PlayerRef.matchesLudiFilters(ludiFilters: MutableMap<String,String>): Boolean {
    val filterCount = ludiFilters.size
    var filterMatchCount = 0
    for (filter in ludiFilters) {
        if (this.matchesLudiFilter(filter.key, filter.value)) {
            filterMatchCount++
        }
    }

    if (filterCount == filterMatchCount) {
        return true
    }

    return false
}

fun PlayerRef.matchesLudiFilter(ludiFilters: MutableMap<String,String>): Boolean {
    for (filter in ludiFilters) {
        if (this.matchesLudiFilter(filter.key, filter.value)) {
            return true
        }
    }
    return false
}

/** BASE FILTER **/
fun PlayerRef.matchesLudiFilter(filterKey:String, filterValue:String): Boolean {
    when (filterKey.toLowerCase(Locale.getDefault())) {
        "status" -> {
            if (this.status.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        "position" -> {
            if (this.position.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        "foot" -> {
            if (this.foot.equals(filterValue, ignoreCase = true)) {
                return true
            }
        }
        "selectednumber" -> {
            if (this.selectedNumber == filterValue) {
                return true
            }
        }
    }
    return false
}