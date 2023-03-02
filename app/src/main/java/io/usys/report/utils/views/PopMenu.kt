package io.usys.report.utils.views

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RawRes

@JvmName("getPopMenu1")
fun getPopMenu(context: Context, view: View): PopupMenu {
    return PopupMenu(context, view)
}
fun Context.getPopMenu(view: View): PopupMenu {
    return PopupMenu(this, view)
}
fun View.attachPopMenu(): PopupMenu {
    return PopupMenu(this.context, this)
}

fun View.attachAndInflatePopMenu(layout: Int): PopupMenu {
    val popmenu = PopupMenu(this.context, this)
    popmenu.inflate(layout)
    return popmenu
}

inline fun View.attachAndInflatePopMenu(layout: Int, crossinline block: (MenuItem, View) -> Unit): PopupMenu {
    val popmenu = PopupMenu(this.context, this)
    popmenu.inflate(layout)
    popmenu.setOnMenuItemClickListener {
        block(it, this)
        false
    }
    return popmenu
}