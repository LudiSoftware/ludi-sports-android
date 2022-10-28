package io.usys.report.model

import androidx.room.PrimaryKey
import io.realm.RealmObject
import io.usys.report.db.FireDB
import io.usys.report.db.addUpdateDB
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class Sport : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var type: String? = ""
    var subType: String? = ""

}

fun Sport.addUpdateInFirebase(): Boolean {
    return addUpdateDB(FireDB.SPORTS, this.id.toString(), this)
}

private fun createSport() {
    val sport = Sport()
    sport.apply {
        this.id = newUUID()
        this.name = "soccer"
    }
    addUpdateDB(FireDB.SPORTS, sport.id.toString(), sport)
}