package io.usys.report.ui.views.listAdapters.liveData

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView

class LiveDataRecyclerAdapter<T>(
    private val viewModel: LiveDataViewModel<T>,
    private val itemLayoutRes: Int,
    private val onBind: (View, T) -> Unit
) : RecyclerView.Adapter<LiveDataRecyclerAdapter<T>.ViewHolder>() {

    private var items: List<T> = emptyList()

    init {
        viewModel.getListLiveData().observeForever { newList ->
            items = newList
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        onBind(holder.itemView, item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
