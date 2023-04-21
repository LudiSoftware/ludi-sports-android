package io.usys.report.providers.liveData

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import io.realm.Realm
import io.usys.report.firebase.DatabasePaths
import io.usys.report.firebase.firebaseDatabase
import io.usys.report.firebase.fireludi.doubleId
import io.usys.report.firebase.toLudiObject
import io.usys.report.realm.model.Note
import io.usys.report.realm.observe
import io.usys.report.realm.safeReplace
import io.usys.report.utils.log

class NoteLiveData(ownerId: String, aboutId:String, private val realmInstance: Realm,
    private val lifecycleOwner: LifecycleOwner) : LiveData<List<Note>>(), ValueEventListener {

    private var doubleId: String
    private var fireReferences: Query? = null
    private var fireListeners: ValueEventListener? = null
    private lateinit var reference: DatabaseReference
    private var enabled = true

    init {
        firebaseDatabase { reference = it.child(DatabasePaths.NOTES.path) }
        doubleId = doubleId(ownerId, aboutId)
        createObservers()
        observeRealmIds()
    }

    /** Create Observer Pairs **/
    private fun createObservers() {
        fireReferences = reference.orderByChild("ownerId").equalTo(doubleId)
        fireListeners= this
        if (enabled) {
            fireReferences?.addValueEventListener(fireListeners ?: return)
        }
    }

    /** Realm Observer **/
    private fun observeRealmIds() {
        realmInstance.observe<Note>(lifecycleOwner) { results ->
            val currentList = value?.toMutableList() ?: mutableListOf()
            val newList = mutableListOf<Note>()
            results.find { it.ownerId == doubleId }?.let { note ->
                if (currentList.safeReplace(note)) {
                    newList.add(note)
                }
            }
            if (newList.isNotEmpty()) {
                postValue(newList)
            }
        }
    }

    /** Firebase Observer **/
    override fun onDataChange(snapshot: DataSnapshot) {
        log("NoteLiveData: onDataChange")
        snapshot.toLudiObject<Note>(realmInstance)
        realmInstance.refresh()
    }

    override fun onCancelled(error: DatabaseError) {
        log("NoteLiveData: onCancelled")
    }

    /** Enable/Disable Helpers **/
    fun enable() {
        if (enabled) return
        enabled = true
        fireListeners?.let { reference.addValueEventListener(it) }
    }

    fun disable() {
        if (!enabled) return
        enabled = false
        fireListeners?.let { reference.removeEventListener(it) }
    }
}