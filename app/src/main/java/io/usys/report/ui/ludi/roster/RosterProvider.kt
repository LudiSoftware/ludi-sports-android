package io.usys.report.ui.ludi.roster

import io.realm.Realm
import io.realm.RealmChangeListener
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.model.Roster
import io.usys.report.realm.realm
import io.usys.report.utils.log

class RosterProvider {

    val realmInstance = realm()
    var rosterIds = mutableListOf<String>()
    var updateCallBack: ((rosterId:String) -> Unit)? = null

    fun pullFromFirebaseAsync(rosterId: String? = null) {
        rosterId?.let {
            fireGetRosterInBackground(rosterId)
        } ?: run {
            rosterIds.forEach { rosterId ->
                fireGetRosterInBackground(rosterId)
            }
        }
    }

}

inline fun Realm.subscribeToRosterUpdates(rosterId: String? = null, crossinline updateCallBack: (String) -> Unit) {
    val rosterListener = RealmChangeListener<Roster> { roster ->
        // Handle changes to the Realm data here
        log("Roster listener called")
        roster.id?.let { id -> updateCallBack(id) }
    }
    this.where(Roster::class.java)?.equalTo("id", rosterId)?.findFirstAsync()?.addChangeListener(rosterListener)
}
