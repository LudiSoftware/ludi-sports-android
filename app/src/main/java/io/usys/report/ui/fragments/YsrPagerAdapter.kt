package io.usys.report.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R

class YsrPagerAdapter : RecyclerView.Adapter<YsrPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YsrPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_player_tiny, parent, false)
        return YsrPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: YsrPagerViewHolder, position: Int) {
        // bind data to the ViewHolder
    }

    override fun getItemCount(): Int {
        return 10
    }
}

class YsrPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // initialize your ViewHolder views here



}