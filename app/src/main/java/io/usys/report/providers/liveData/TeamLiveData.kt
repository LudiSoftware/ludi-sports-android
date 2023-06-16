package io.usys.report.providers.liveData

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.realm.Realm
import io.realm.RealmObject
import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.toLudiObject
import io.usys.report.realm.RealmLiveData
import io.usys.report.realm.model.Team
import io.usys.report.realm.observeForever
import io.usys.report.realm.realm
import io.usys.report.realm.safeReplace
import io.usys.report.utils.log

/**
 * Setup:
    teamLiveData = TeamLiveData(realmIds, realmInstance, fragment.viewLifecycleOwner).start()
 * Usage:
    teamLiveData?.safeObserve(fragment.viewLifecycleOwner) { teams ->
        log("Team results updated")
        teams.forEach {
            itemList?.safeAdd(it)
        }
        notifyDataSetChanged()
    }
 */

typealias LudiLiveList<T> = LiveData<List<T>>
fun TeamLiveData?.start() : TeamLiveData? {
    this?.enable()
    return this
}

inline fun <R: RealmObject, T: LudiLiveList<R>> T?.safeObserve(owner: LifecycleOwner?, crossinline block: (List<R>) -> Unit) {
    if (owner == null) return
    this?.observe(owner) { teams ->
        block(teams)
    }
}
class TeamLiveData(private val realmIds: List<String>,
                   private val realmInstance: Realm = realm()) : LudiLiveList<Team>(), ValueEventListener {

    private var observer : RealmLiveData<Team>? = null
    private val firebaseDatabaseReferences = mutableMapOf<String, DatabaseReference>()
    private val firebaseDatabaseListeners = mutableMapOf<String, ValueEventListener>()
    private lateinit var teamDbReference: DatabaseReference
    private var enabled = false

    init { firebaseDatabase { teamDbReference = it.child(DatabasePaths.TEAMS.path) } }

    /** Create Observer Pairs **/
    private fun createFirebaseChangeListeners() {
        realmIds.forEach { realmId ->
            firebaseDatabaseReferences[realmId] = teamDbReference.child(realmId)
            firebaseDatabaseListeners[realmId] = this
            if (enabled) {
                firebaseDatabaseReferences[realmId]?.addValueEventListener(firebaseDatabaseListeners[realmId] ?: return@forEach)
            }
        }
    }

    /** Realm Observer **/
    private fun observeRealmChangesForever() {
        observer = realmInstance.observeForever { results ->
            val currentList = value?.toMutableList() ?: mutableListOf()
            val newList = mutableListOf<Team>()
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
        log("TeamLiveData: onDataChange")
        snapshot.toLudiObject<Team>(realmInstance)
    }

    override fun onCancelled(error: DatabaseError) {
        log("TeamLiveData: onCancelled")
    }

    /** Enable/Disable Helpers **/
    fun enable() {
        if (enabled) return
        enabled = true
        createFirebaseChangeListeners()
        observeRealmChangesForever()
    }

    fun disable() {
        if (!enabled) return
        enabled = false
        firebaseDatabaseReferences.forEach { (id, reference) ->
            firebaseDatabaseListeners[id]?.let { reference.removeEventListener(it) }
        }
        observer?.remove()
        observer = null
    }
}

