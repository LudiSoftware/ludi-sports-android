package io.usys.report.ui.ludi.formationbuilder

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable


open class FormationSession : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var dateCreated: String = getTimeStamp()
    var teamId: String? = "null"
    var rosterId: String? = "null"
    var roster: Roster? = null
    var teamColorsAreOn: Boolean = true
    var currentLayout: Int = 0
    var layoutList: RealmList<Int>? = RealmList()
    var formationList: RealmList<PlayerRef>? = RealmList()
    var playerList: RealmList<PlayerRef>? = RealmList()
    var removedList: RealmList<PlayerRef>? = RealmList()
}

fun Realm.getUserFormationSession(): FormationSession? {
    var fs: FormationSession? = null
    this.safeUserId {
        fs = this.where(FormationSession::class.java).equalTo("id", it).findFirst()
    }
    return fs
}

inline fun Realm.getUserFormationSession(crossinline block: (FormationSession) -> Unit) {
    this.safeUserId { itUserId ->
        this.where(FormationSession::class.java).equalTo("id", itUserId).findFirst()?.let {
            block(it)
        }
    }
}

fun Realm.getFormationSessionById(id: String): FormationSession? {
    return this.where(FormationSession::class.java).equalTo("id", id).findFirst()
}

