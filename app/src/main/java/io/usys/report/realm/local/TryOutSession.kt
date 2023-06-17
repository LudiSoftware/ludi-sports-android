package io.usys.report.realm.local

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.utils.androidx.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable


open class TryOutSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var teamId: String? = "null"
    var rosterId: String? = "null"
    var rosterSizeLimit: Int = 20
    var playersSelectedCount: Int = 0
    var playerIds: RealmList<String>? = RealmList()
    var rosterSplits: Int = 0
    //formation
    var teamColorsAreOn: Boolean = true
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationListIds: RealmList<String>? = RealmList()
    var deckListIds: RealmList<String>? = RealmList()
    // Layout
    var layout: Int = R.layout.roster_player_card_grid_medium
    var type: String = FireTypes.PLAYERS
    var size: String = "medium_grid"
    var mode: String = "official"
    var isOpen: Boolean = true
    // Click Listeners
    var touchEnabled: Boolean = true
}

inline fun Realm.tryoutSessionById(tryoutId:String?, crossinline block: (TryOutSession) -> Unit) {
    this.where(TryOutSession::class.java).equalTo("id", tryoutId).findFirst()?.let {
        block(it)
    }
}

inline fun Realm.tryoutSessionByTeamId(teamId:String?, crossinline block: (TryOutSession) -> Unit) {
    this.where(TryOutSession::class.java).equalTo("teamId", teamId).findFirst()?.let {
        block(it)
    }
}

fun Realm.tryoutSessionById(tryoutId:String?): TryOutSession? {
    return this.where(TryOutSession::class.java).equalTo("id", tryoutId).findFirst()
}
fun Realm.tryoutSessionByTeamId(teamId:String?): TryOutSession? {
    return this.where(TryOutSession::class.java).equalTo("teamId", teamId).findFirst()
}


