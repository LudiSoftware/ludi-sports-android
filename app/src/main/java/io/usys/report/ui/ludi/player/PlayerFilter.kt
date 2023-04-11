package io.usys.report.ui.ludi.player

import io.realm.RealmList
import io.usys.report.realm.model.PlayerRef
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


fun rosters(vararg pairs: Pair<String, String>): MutableMap<String, String> {
    return mutableMapOf(*pairs)
}

fun ludiFilters(vararg pairs: Pair<String, String>): MutableMap<String, String> {
    return mutableMapOf(*pairs)
}


fun RealmList<PlayerRef>.ludiFilters(ludiFilters: MutableMap<String, String>?): RealmList<PlayerRef> {
    if (ludiFilters.isNullOrEmpty()) return this
    val filteredList = RealmList<PlayerRef>()
    for (playerRef in this) {
        if (playerRef.matchesLudiFilter(ludiFilters)) {
            filteredList.add(playerRef)
        }
    }
    return filteredList
}


fun PlayerRef.matchesLudiFilter(ludiFilters: MutableMap<String,String>): Boolean {
    for (filter in ludiFilters) {
        if (this.matchesLudiFilter(filter.key, filter.value)) {
            return true
        }
    }
    return false
}

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
    }
    return false
}