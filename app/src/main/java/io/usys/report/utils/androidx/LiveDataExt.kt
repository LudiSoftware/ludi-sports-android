package io.usys.report.utils.androidx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

inline fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, crossinline onChanged: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            onChanged.invoke(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.asFlow(): Flow<T> = callbackFlow {
    val observer = Observer<T> { value -> this.trySend(value).isSuccess }
    observeForever(observer)
    awaitClose { removeObserver(observer) }
}

//fun <T> Flow<T>.asLiveData(): LiveData<T> = liveData {
//    collect { value -> emit(value) }
//}
//
//fun <T> LiveData<T>.asStateFlow(): StateFlow<T> = stateFlow {
//    emit(value)
//    val observer = Observer<T> { value -> emit(value) }
//    observeForever(observer)
//    awaitClose { removeObserver(observer) }
//}
