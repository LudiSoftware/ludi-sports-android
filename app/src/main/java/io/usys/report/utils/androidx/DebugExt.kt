package io.usys.report.utils.androidx

import io.usys.report.BuildConfig

inline fun debugModeOnly(block: () -> Unit) {
    if (BuildConfig.DEBUG) block()
}