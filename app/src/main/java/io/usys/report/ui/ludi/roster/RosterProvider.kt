package io.usys.report.ui.ludi.roster

import io.realm.Realm
import io.realm.RealmChangeListener
import io.usys.report.realm.model.Roster
import io.usys.report.utils.log

class RosterRealmSingleEventListener(val rosterId: String,
                                     private val onRealmChange: (teamId: String) -> Unit) : RealmChangeListener<Roster> {
    private val realm: Realm = Realm.getDefaultInstance()
    private lateinit var rosterResult: Roster

    init {
        registerListener()
    }

    override fun onChange(t: Roster) {
        log("Team listener called")
        onRealmChange(rosterId)
    }

    fun registerListener() {
        rosterResult = realm.where(Roster::class.java).equalTo("id", rosterId).findFirstAsync()
        rosterResult.addChangeListener(this)
    }

    fun unregisterListener() {
        rosterResult.removeChangeListener(this)
        realm.close()
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
