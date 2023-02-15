package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireAddUpdateDBAsync
import io.usys.report.utils.getTimeStamp
import io.usys.report.utils.newUUID
import java.io.Serializable

/**
 * Created by ChazzCoin : October 2022.
 */
open class Sport : RealmObject(), Serializable {

    @PrimaryKey
    var id: String = newUUID()
    var dateCreated: String = getTimeStamp()
    var name: String = ""
    var imgUrl: String = ""
    var type: String = ""
    var subType: String = ""
    var isVisible: Boolean = true

}

fun Sport.fireAddUpdateToDB(): Boolean {
    return fireAddUpdateDBAsync(FireTypes.SPORTS, this.id, this)
}

fun createSport() {
    val sport = Sport()
    sport.apply {
        this.name = "baseball"
        this.imgUrl = ""
    }
    fireAddUpdateDBAsync(FireTypes.SPORTS, sport.id, sport)
}