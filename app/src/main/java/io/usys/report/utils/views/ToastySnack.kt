package io.usys.report.utils.views

import android.content.Context
import android.widget.Toast

fun showFailedToast(context: Context, mess: String = "There was an Error.") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

fun showSuccess(context: Context, mess: String = "Success!") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}