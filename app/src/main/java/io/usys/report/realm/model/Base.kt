package io.usys.report.realm.model

import io.realm.RealmModel
import io.realm.RealmObject
import io.usys.report.utils.getTimeStamp

/**
 * Created by ChazzCoin : October 2022.
 *
 * - TODO: Abandon this idea unless we can just not make it a realm object and still inherit from it.
 *
 */
open class YsrRealmObject: RealmObject() {
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
