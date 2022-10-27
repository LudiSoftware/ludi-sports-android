package io.usys.report.basicUser.dashboard

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.usys.report.R
import io.usys.report.model.Spot
import io.usys.report.utils.inflate
import kotlinx.android.extensions.LayoutContainer

class UserDashboardViewAdapter(mContext: Context) : RecyclerView.Adapter<UserDashboardViewAdapter.InnerSpotViewHolder>() {

//    var spotsList : RealmList<Spot>? = Session.session?.spots
//    var arrayOfSpots : ArrayList<Spot> = ArrayList()
//    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerSpotViewHolder {
        return InnerSpotViewHolder(parent.inflate(R.layout.item_list_sports_two))
    }

    override fun onBindViewHolder(viewHolder: InnerSpotViewHolder, position: Int) {
//        arrayOfSpots.let {
//            it[position].let { it1 ->
//                viewHolder.bind(it1)
//            }
//        }
//
//        viewHolder.itemView.setOnLongClickListener{
//            arrayOfSpots[position].let { it1 ->
//                AlertDialog.Builder(context)
//                    .setMessage(R.string.location_dialog_delete_confirmation)
//                    .setPositiveButton(R.string.delete) { _, _ ->
//                        //TODO: DELETE SPOT
//                        Session.removeSpot(it1)
//                        this.reloadSpots()
//                        this.notifyDataSetChanged()
//                    }
//                    .setNegativeButton(R.string.close, null)
//                    .show()
//            }
//            return@setOnLongClickListener true
//        }
    }

    override fun getItemCount(): Int {
        return 0
    }



    inner class InnerSpotViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
//                onClick(spots[adapterPosition])
            }


        }

        fun bind(spot: Spot) {
            //TODO: SETUP DESIGN HERE
//            containerView.itemLocationName.text = spot.locationName
//            containerView.txtAddressOne.text = spot.addressOne
//            containerView.txtAddressTwo.text = spot.addressTwo
//            containerView.txtCityStateZip.text = "${spot.city}, ${spot.state}, ${spot.zip}"
//            containerView.txtPeople.text = spot.estPeople
        }
    }
}