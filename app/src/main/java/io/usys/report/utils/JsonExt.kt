package io.usys.report.utils

import com.google.gson.Gson

fun Any?.asJson(): String = Gson().toJson(this)
inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)
