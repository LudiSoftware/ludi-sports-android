package io.usys.report.realm.local

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TeamRef
import io.usys.report.realm.model.TryOut
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable


open class CoachSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var teamIds: RealmList<String>? = RealmList()


    var teamRef: TeamRef? = null
    var team: Team? = null
    var rosterId: String? = "null"
    var roster: Roster? = null
    var tryout: TryOut? = null
    var playerIds: RealmList<String>? = RealmList()
    //formation
    var teamColorsAreOn: Boolean = true
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationListIds: RealmList<String>? = RealmList()
    var deckListIds: RealmList<String>? = RealmList()
    var blackListIds: RealmList<String>? = RealmList()
}




