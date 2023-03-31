package io.usys.report.ui.ludi.player

import io.realm.RealmList
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.toRealmList
import io.usys.report.utils.splitFullName
import kotlin.reflect.full.memberProperties

fun RealmList<PlayerRef>.sortByName(): RealmList<PlayerRef> {
    return this.sortedBy { it.name?.splitFullName()?.second ?: it.name }.toRealmList()
}

fun RealmList<PlayerRef>.sortByOrderIndex(ascending: Boolean = true): RealmList<PlayerRef> {
    val sortedList = this.sortedWith(compareBy { playerRef: PlayerRef ->
        if (playerRef.orderIndex == 0) {
            Int.MAX_VALUE
        } else {
            playerRef.orderIndex
        }
    })
    return if (ascending) {
        RealmList(*sortedList.toTypedArray())
    } else {
        RealmList(*sortedList.reversed().toTypedArray())
    }
}
fun RealmList<PlayerRef>.sortByAttribute(attribute: String, ascending: Boolean = true): RealmList<PlayerRef> {
    val sortedList = this.sortedWith(compareBy { playerRef: PlayerRef ->
        val property = PlayerRef::class.memberProperties.find { it.name == attribute }
        property?.get(playerRef) as Comparable<*>?
    })

    return if (ascending) {
        RealmList(*sortedList.toTypedArray())
    } else {
        RealmList(*sortedList.reversed().toTypedArray())
    }
}