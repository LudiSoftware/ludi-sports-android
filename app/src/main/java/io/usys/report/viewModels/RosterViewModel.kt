package io.usys.report.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import io.realm.RealmObjectChangeListener
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.realm.model.Roster

class RosterViewModel : ViewModel() {
    private val _roster = MutableLiveData<Roster?>()
    val roster: LiveData<Roster?> = _roster

    private lateinit var databaseReference: DatabaseReference

    private val realmObjectChangeListener =
        RealmObjectChangeListener<Roster> { _, changeSet ->
            if (changeSet!!.isDeleted) {
                _roster.value = null
            } else {
                _roster.postValue(_roster.value)
            }
        }

    init {
        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().reference.child("rosters")
    }

    fun setRoster(roster: Roster?) {
        roster?.removeChangeListener(realmObjectChangeListener)
        _roster.value = roster
        roster?.addChangeListener(realmObjectChangeListener)
    }

    override fun onCleared() {
        super.onCleared()
        _roster.value?.removeChangeListener(realmObjectChangeListener)
    }

    fun syncRosterToFirebase() {
        _roster.value?.let { roster ->
            roster.id?.let { id ->
                databaseReference.child(id).setValue(roster)
            }
        }
    }

    fun fetchRosterFromFirebase(rosterId: String) {
        fireGetRosterInBackground(rosterId)
    }
}
