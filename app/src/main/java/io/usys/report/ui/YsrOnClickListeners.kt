package io.usys.report.ui

import android.view.View
import io.realm.RealmObject

fun onClickReturnEmpty(): (() -> Unit)? { return null }
fun onClickReturnViewRealmObject(): ((View, RealmObject) -> Unit)? { return null }
fun onClickReturnAnyAny(): ((Any, Any) -> Unit)? { return null }
fun onClickReturnStringString(): ((String, String) -> Unit)? { return null }
//fun onClickReturnStringString(): ((String, String) -> Unit)? { return null }