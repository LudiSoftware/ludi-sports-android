package io.usys.report.realm

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.realm.model.PlayerRef
import io.usys.report.realm.model.Roster
import io.usys.report.realm.model.Team
import io.usys.report.realm.model.TryOut

/** Team Observable Helper **/
inline fun Realm.observeTeam(lifecycleOwner: LifecycleOwner, crossinline onUpdate: (results: RealmResults<Team>) -> Unit): RealmResults<Team>? {
    val results = this.where(Team::class.java).findAllAsync()
    results.asLiveData().observe(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

/** Roster Observable Helper **/
inline fun Realm.observeRoster(lifecycleOwner: LifecycleOwner, crossinline onUpdate: (results: RealmResults<Roster>) -> Unit): RealmResults<Roster>? {
    val results = this.where(Roster::class.java).findAllAsync()
    results.asLiveData().observe(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

/** TryOut Observable Helper **/
inline fun Realm.observeTryout(lifecycleOwner: LifecycleOwner, crossinline onUpdate: (results: RealmResults<TryOut>) -> Unit): RealmResults<TryOut>? {
    val results = this.where(TryOut::class.java).findAllAsync()
    results.asLiveData().observe(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

/** MAIN/All Observable Class **/
inline fun <reified T : RealmObject> Realm.observe(lifecycleOwner: LifecycleOwner,
                                                   crossinline onUpdate: (results: RealmResults<T>) -> Unit): RealmResults<T>? {
    val results = this.where(T::class.java).findAllAsync()
    results.asLiveData().observe(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

class RealmLiveData<T : RealmObject>(private val realmResults: RealmResults<T>) :
    LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
}

fun <T : RealmObject> RealmResults<T>.asLiveData() = RealmLiveData(this)
