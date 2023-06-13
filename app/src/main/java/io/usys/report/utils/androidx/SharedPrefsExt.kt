package io.usys.report.utils.androidx

import android.content.SharedPreferences

inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        String::class -> getString(key, defaultValue as? String) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun <reified T : Any> SharedPreferences.put(key: String, value: T) {
    when (T::class) {
        Boolean::class -> edit().putBoolean(key, value as Boolean).apply()
        Float::class -> edit().putFloat(key, value as Float).apply()
        Int::class -> edit().putInt(key, value as Int).apply()
        Long::class -> edit().putLong(key, value as Long).apply()
        String::class -> edit().putString(key, value as String).apply()
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

