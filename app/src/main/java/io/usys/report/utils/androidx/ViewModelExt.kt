package io.usys.report.utils.androidx

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.postUpdate(update: (T) -> T) {
    postValue(update(value!!))
}
