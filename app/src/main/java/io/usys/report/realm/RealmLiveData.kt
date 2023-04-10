package io.usys.report.realm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

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
