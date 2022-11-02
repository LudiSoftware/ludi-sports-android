package io.usys.report.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.db.FireDB
import io.usys.report.db.addUpdateDBAsync
import io.usys.report.utils.*
import java.io.Serializable

/**
 * Created by ChazzCoin : December 2019.
 */
open class Organization : RealmObject(), Serializable {

    companion object {
        const val ORDER_BY_SPORTS = "sport"
    }

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sport: String? = "unassigned"
    var leagues: RealmList<String>? = null
    var regions: RealmList<String>? = null
    var locations: RealmList<String>? = null
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = ""
    var managerId: String? = "unassigned"
    var managerName: String? = "unassigned"
    var staff: RealmList<String>? = null
    var estPeople: String? = ""
    var dateCreated: String? = "" // timestamp
    var reviews: RealmList<String>? = null

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }


    fun matches(org: Organization) : Boolean {
        if (this.id == org.id &&
            this.name == org.name &&
            this.addressOne == org.addressOne &&
            this.addressTwo == org.addressTwo &&
            this.city == org.city &&
            this.state == org.state &&
            this.zip == org.zip &&
            this.estPeople == org.estPeople) return true
        return false
    }

    fun addUpdateOrgToFirebase(): Boolean {
        return addUpdateDBAsync(FireDB.ORGANIZATIONS, this.id.toString(), this)
    }

}

fun createOrg() {
    val org = Organization()
    org.apply {
        this.id = newUUID()
        this.sport = "soccer"
        this.city = "birmingham"
        this.name = "USYSR Club"
    }
    addUpdateDBAsync(FireDB.ORGANIZATIONS, org.id.toString(), org)
}
