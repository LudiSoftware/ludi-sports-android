package io.usys.report.realm

import androidx.lifecycle.*
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.utils.androidx.observeOnce


/** MAIN/All Observable Class **/

@Deprecated("Use Realm.observeForever or observeSingleEvent instead")
inline fun <reified T : RealmObject> Realm.observe(lifecycleOwner: LifecycleOwner, crossinline onUpdate: (results: RealmResults<T>) -> Unit): RealmResults<T>? {
    val results = this.where(T::class.java).findAllAsync()
    results.asLiveData().observe(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

inline fun <reified T : RealmObject> Realm.observeForever(crossinline onUpdate: (results: RealmResults<T>) -> Unit): RealmLiveData<T> {
    val results = this.where(T::class.java).findAllAsync().asLiveData()
    results.observeForever { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

inline fun <reified T : RealmObject> Realm.observeSingleEvent(lifecycleOwner: LifecycleOwner, crossinline onUpdate: (results: RealmResults<T>) -> Unit): RealmLiveData<T> {
    val results = this.where(T::class.java).findAllAsync().asLiveData()
    results.observeOnce(lifecycleOwner) { updatedResults ->
        onUpdate(updatedResults)
    }
    return results
}

class RealmLiveData<T : RealmObject>(private val realmResults: RealmResults<T>) : LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }
    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
    fun remove() {
        realmResults.removeChangeListener(listener)
    }
}

fun <T : RealmObject> RealmResults<T>.asLiveData() = RealmLiveData(this)
