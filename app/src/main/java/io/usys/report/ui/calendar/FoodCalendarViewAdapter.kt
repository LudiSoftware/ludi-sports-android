package io.usys.report.ui.calendar

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.realm.model.Service
import io.usys.report.realm.model.Location
import io.usys.report.utils.inflate

class FoodCalendarViewAdapter(var activity: Activity, val fragment: FoodCalendarFragment) : RecyclerView.Adapter<FoodCalendarViewAdapter.InnerFoodCalendarViewHolder>() {

    var arrayOfSpots : ArrayList<Location> = ArrayList()
    var currentDate : String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerFoodCalendarViewHolder {
        return InnerFoodCalendarViewHolder(
            parent.inflate(R.layout.card_sport_small),
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
            if (spot.dateCreated == currentDate) {
                if (spot.status == Service.PENDING || spot.status == Service.BOOKED) {
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
        val containerView: View,
        val activity: Activity,
        val foodCalendarFragment: FoodCalendarFragment
    ) :
        RecyclerView.ViewHolder(containerView) {

        fun bind(spot: Location) {
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