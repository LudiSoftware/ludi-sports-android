package io.usys.report.realm.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable


open class CustomAttribute : RealmObject(), Serializable {
    @PrimaryKey
    var key: String? = ""
    var value: String? = null

    fun add(key: String, value: String) {
        this.key = key
        this.value = value
    }
}


