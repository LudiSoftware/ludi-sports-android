package io.usys.report.providers.liveData

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.toLudiObject
import io.usys.report.realm.model.Roster
import io.usys.report.realm.observe
import io.usys.report.realm.safeReplace
import io.usys.report.utils.log

class RosterLiveData(private val realmIds: List<String>,
                     private val realmInstance: Realm,
                     private val lifecycleOwner: LifecycleOwner) : LiveData<List<Roster>>(), ValueEventListener {

    private val fireReferences = mutableMapOf<String, DatabaseReference>()
    private val fireListeners = mutableMapOf<String, ValueEventListener>()
    private lateinit var reference: DatabaseReference
    private var enabled = false

    init {
        firebaseDatabase { reference = it.child(DatabasePaths.ROSTERS.path) }
        createObservers()
        observeRealmIds()
    }

    /** Create Observer Pairs **/
    private fun createObservers() {
        realmIds.forEach { realmId ->
            fireReferences[realmId] = reference.child(realmId)
            fireListeners[realmId] = this
            if (enabled) {
                fireReferences[realmId]?.addValueEventListener(fireListeners[realmId] ?: return@forEach)
            }
        }
    }

    /** Realm Observer **/
    private fun observeRealmIds() {
        realmInstance.observe<Roster>(lifecycleOwner) { results ->
            val currentList = value?.toMutableList() ?: mutableListOf()
            val newList = mutableListOf<Roster>()
            for (id in realmIds) {
                results.find { it.id == id }?.let { team ->
                    if (currentList.safeReplace(team)) {
                        newList.add(team)
                    }
                }
            }
            if (newList.isNotEmpty()) {
                postValue(newList)
            }
        }
    }

    /** Firebase Observer **/
    override fun onDataChange(snapshot: DataSnapshot) {
        log("RosterLiveData: onDataChange")
        snapshot.toLudiObject<Roster>(realmInstance)
    }

    override fun onCancelled(error: DatabaseError) {
        log("RosterLiveData: onCancelled")
    }

    /** Enable/Disable Helpers **/
    fun enable() {
        if (enabled) return
        enabled = true
        fireReferences.forEach { (id, reference) ->
            fireListeners[id]?.let { reference.addValueEventListener(it) }
        }
    }

    fun disable() {
        if (!enabled) return
        enabled = false
        fireReferences.forEach { (id, reference) ->
            fireListeners[id]?.let { reference.removeEventListener(it) }
        }
    }
}