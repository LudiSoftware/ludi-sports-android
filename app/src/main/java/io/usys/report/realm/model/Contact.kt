package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.utils.newUUID
import java.io.Serializable


open class Contact : RealmObject(), Serializable {
    @PrimaryKey
    var id: String? = newUUID()
    var contactId: String? = null
    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var relationship: String? = null
}


