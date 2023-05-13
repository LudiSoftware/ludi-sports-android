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
    var isVisible: Boolean = true
    // base
    var dateCreated: String? = getTimeStamp()
    var dateUpdated: String? = getTimeStamp()
    var name: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var type: String? = null
    var subType: String? = null
    var details: String? = null
    var isFree: Boolean = false
    var status: String? = null
    var mode: String? = null
    var imgUrl: String? = null
    var sport: String? = null

}