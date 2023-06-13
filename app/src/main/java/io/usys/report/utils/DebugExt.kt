package io.usys.report.utils

import io.usys.report.BuildConfig

inline fun debugModeOnly(block: () -> Unit) {
    if (BuildConfig.DEBUG) block()
}