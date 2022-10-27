package io.usys.report.model

import androidx.room.PrimaryKey
import io.realm.RealmObject
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