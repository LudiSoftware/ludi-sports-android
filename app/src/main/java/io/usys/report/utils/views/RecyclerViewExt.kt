package io.usys.report.utils.views

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    setHasFixedSize(true)
    this.layoutManager = layoutManager
    this.adapter = adapter
}

fun RecyclerView.Adapter<*>.notifyDataSetChangedOnMainThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) notifyDataSetChanged()
    else Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
}

fun <T: RecyclerView.ViewHolder> RecyclerView.Adapter<T>.isEmpty(): Boolean {
    return this.itemCount == 0
}
fun <T: RecyclerView.ViewHolder> RecyclerView.Adapter<T>.isNotEmpty(): Boolean {
    return this.itemCount > 0
}
fun RecyclerView.toggleLayoutManager(layoutManager: RecyclerView.LayoutManager) {
    this.layoutManager = layoutManager
    this.adapter?.notifyDataSetChanged()
}
fun RecyclerView.smoothScrollToTop() {
    this.smoothScrollToPosition(0)
}
//fun <T: RecyclerView.ViewHolder> RecyclerView.Adapter<T>.addClickListener(clickListener: (position: Int) -> Unit) {
//    // implementation depends on your adapter's implementation
//}
//fun <T: RecyclerView.ViewHolder> RecyclerView.Adapter<T>.addLongClickListener(longClickListener: (position: Int) -> Unit) {
//    // implementation depends on your adapter's implementation
//}