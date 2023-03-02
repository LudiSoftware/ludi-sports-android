package io.usys.report.ui.ludi.formationbuilder

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.utils.newUUID
import java.io.Serializable


open class FormationSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var teamId: String? = "null"
    var rosterId: String? = "null"
    var roster: Roster? = null
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationList: RealmList<PlayerRef>? = RealmList()
    var rosterList: RealmList<PlayerRef>? = RealmList()
    var removedList: RealmList<PlayerRef>? = RealmList()
}


