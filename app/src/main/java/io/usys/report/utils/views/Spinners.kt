package io.usys.report.utils.views

import android.R
import android.content.Context
import android.widget.ArrayAdapter

fun getSimpleSpinnerAdapter(context: Context, list:ArrayList<String?>) : ArrayAdapter<String?> {
    return ArrayAdapter(context, R.layout.simple_list_item_1, list)
}