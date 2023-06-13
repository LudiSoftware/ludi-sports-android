package io.usys.report.ui.views.listAdapters.liveData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.RealmList
import io.realm.RealmObject

class LiveDataViewModel<T> : ViewModel() {

    private val itemListLiveData = MutableLiveData<List<T>>()

    fun getListLiveData(): LiveData<List<T>> {
        return itemListLiveData
    }

    fun updateItems(newItems: List<T>) {
        itemListLiveData.value = newItems
    }
}

class LiveRealmViewModel<T: RealmObject> : ViewModel() {

    private val itemListLiveData = MutableLiveData<RealmList<T>>()

    fun getListLiveData(): LiveData<RealmList<T>> {
        return itemListLiveData
    }

    fun updateItems(newItems: RealmList<T>) {
        itemListLiveData.value = newItems
    }
}

