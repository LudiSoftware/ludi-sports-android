package io.usys.report.basicUser.calendar

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.model.Spot
import io.usys.report.utils.inflate
import kotlinx.android.extensions.LayoutContainer

class FoodCalendarViewAdapter(var activity: Activity, val fragment: FoodCalendarFragment) : RecyclerView.Adapter<FoodCalendarViewAdapter.InnerFoodCalendarViewHolder>() {

    var arrayOfSpots : ArrayList<Spot> = ArrayList()
    var currentDate : String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerFoodCalendarViewHolder {
        return InnerFoodCalendarViewHolder(
            parent.inflate(R.layout.item_list_sports_two),
            activity,
            fragment
        )
    }

    override fun onBindViewHolder(
        viewHolder: InnerFoodCalendarViewHolder,
        position: Int
    ) {
        arrayOfSpots.let {
            val spot = it[position]
            if (spot.date == currentDate) {
                if (spot.status == Spot.PENDING || spot.status == Spot.BOOKED) {
                    viewHolder.containerView.visibility = View.GONE
                    viewHolder.containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    return
                }
                viewHolder.bind(spot)
            } else {
                viewHolder.containerView.visibility = View.GONE
                viewHolder.containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayOfSpots.size
    }

    inner class InnerFoodCalendarViewHolder(
        override val containerView: View,
        val activity: Activity,
        val foodCalendarFragment: FoodCalendarFragment
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(spot: Spot) {
//            containerView.txtItemSpotName.text = spot.locationName
//            containerView.txtItemDate.text = spot.toFullDate()
//            containerView.txtItemEstPeople.text = spot.estPeople
//            containerView.txtItemCost.text = spot.toFullPrice()

//            containerView.setOnClickListener {
//                spot.createDetailsFoodtruckDialog(
//                    activity = activity,
//                    foodCalendarFragment,
//                    null,
//                    false
//                ).show()
//            }
        }
    }


}