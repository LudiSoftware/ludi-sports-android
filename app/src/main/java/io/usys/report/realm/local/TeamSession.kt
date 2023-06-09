package io.usys.report.realm.local

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.androidx.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable


open class TeamSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var teamId: String? = "null"
    var rosterId: String? = "null"
    var tryoutId: String? = "null"
    var tryoutRosterId: String? = "null"
    var rosterSizeLimit: Int = 20
    var playerIds: RealmList<String>? = RealmList()
    //formation
    var teamColorsAreOn: Boolean = true
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationListIds: RealmList<String>? = RealmList()
    var deckListIds: RealmList<String>? = RealmList()
}

inline fun Realm.teamSessionByTeamId(teamId:String?, crossinline block: (TeamSession) -> Unit) {
    this.where(TeamSession::class.java).equalTo("teamId", teamId).findFirst()?.let {
        block(it)
    }
}


