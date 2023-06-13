package io.usys.report.utils.androidx

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openWebsite(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
