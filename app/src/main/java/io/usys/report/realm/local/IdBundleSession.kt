package io.usys.report.realm.local

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.idBundleSession
import io.usys.report.realm.ifObjectDoesNotExist
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.safeWrite
import io.usys.report.utils.isNotNBE
import io.usys.report.utils.newUUID
import java.io.Serializable

open class IdBundleSession: RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var teamId: String = "null"
    var rosterId: String = "null"
    var tryoutRosterId: String = "null"
    var tryoutId: String = "null"
    var orgId: String = "null"
    var playerId: String = "null"
    var userId: String = "null"
    var coachId: String = "null"
    var locationId: String = "null"
}

fun Realm.createIdBundleSession() {
    var newId = "1"
    safeUserId { userId -> newId = userId }
    this.ifObjectDoesNotExist<IdBundleSession>(newId) {
        this.safeWrite {
            this.createObject(IdBundleSession::class.java, newId)
        }
    }
}

fun Realm.updateIdBundleIds(teamId:String?=null, rosterId:String?=null, tryoutRosterId:String?=null) {
    this.idBundleSession { idBundle ->
        this.safeWrite {
            if (teamId.isNotNBE()) idBundle.teamId = teamId.toString()
            if (rosterId.isNotNBE()) idBundle.rosterId = rosterId.toString()
            if (tryoutRosterId.isNotNBE()) idBundle.tryoutRosterId = tryoutRosterId.toString()
        }
    }
}