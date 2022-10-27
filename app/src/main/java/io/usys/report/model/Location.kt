package io.usys.report.model

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.room.PrimaryKey
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.usys.report.R
import io.usys.report.utils.*
import io.realm.RealmObject
import kotlinx.android.synthetic.main.dialog_spot_details.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Spot(id:String? = "", addressOne:String? = "", addressTwo:String? = "",
                city:String? = "", state:String? = "", zip:String? = "",
                 date:String? = "", spotManager:String? = "") : RealmObject() {

    companion object {
        const val LUNCH_TIME : String = "11AM-2PM"
        const val DINNER_TIME : String = "5PM-8PM"

        const val PRICE : String = "5.00"

        const val AVAILABLE : String = "available"
        const val PENDING : String = "pending"
        const val PENDING_CASHAPP : String = "pending_cashapp"
        const val BOOKED : String = "booked"

        const val ENTREE : String = "Entree"
        const val DESSERT : String = "Dessert"

        fun parseMealTime(time:String) : String {
            return when {
                //11-2
                time.toLowerCase(Locale.ROOT) == "lunch" -> { LUNCH_TIME }
                //5-8
                time.toLowerCase(Locale.ROOT) == "dinner" -> { DINNER_TIME }
                else -> { time }
            }
        }
    }

    @PrimaryKey
    var id: String? = ""

    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223

    var date: String? = "" // 10 Dec 2019
    var foodType: String? = "" //Entree, Dessert. . .
    var mealTime: String? = "" //Breakfast, Lunch or Dinner?
    var parkingInfo: String? = "" // "Park on the third spot to the right"
    var estPeople: String? = "" //Amount of expected people
    var status : String? = AVAILABLE //Has it been bought?
    var price: String? = "" //Assigned Price to Spot
    var spotManager: String? = "" //Creators Display Name
    var assignedTruckUid : String? = "" //FoodTruck who buys Spot
    var assignedTruckName : String? = "" //FoodTruck who buys Spot
    var locationName: String? = "" //Custom Name Made by Creator
    var locationUUID: String? = "" //Custom Name Made by Creator

    var hasReview: Boolean = false
    var reviewUUID: String? = ""
    var reviewScore: Int = 9999
    var reviewDetails: String = ""

    init {
        this.id = id
        this.addressOne = addressOne
        this.addressTwo = addressTwo
        this.city = city
        this.state = state
        this.zip = zip
        this.date = date
        this.spotManager = spotManager
        this.price = if (price.isNullOrBlank() || price.isNullOrEmpty()) {PRICE} else {price}
    }

    fun toMealTime() : String {
        return when {
            //11-2
            this.mealTime?.toLowerCase(Locale.ROOT) == "lunch" -> { LUNCH_TIME }
            //5-8
            this.mealTime?.toLowerCase(Locale.ROOT) == "dinner" -> { DINNER_TIME }
            else -> { this.mealTime ?: "unknown" }
        }
    }

    fun toFullDate() : String {
        return "${toMealTime()}, ${this.date?.toFullDateString() ?: "unknwon"}"
    }

    fun toFullPrice() : String {
        return "$${this.price}"
    }

    fun toCityStateZip() : String {
        return "${this.city}, ${this.state} ${this.zip}"
    }

    fun toFullEstPeople() : String {
        return "Est: ${this.estPeople}"
    }
}

fun Spot.isOld(): Boolean {
    if (this.date == null) return false
    val toDate = SimpleDateFormat(FireHelper.SPOT_DATE_FORMAT, Locale.US).parse(this.date!!) ?: return false
    return toDate.before(Date())
}

fun Spot.getDate(): Date? {
    if (this.date == null) return null
    return SimpleDateFormat(FireHelper.SPOT_DATE_FORMAT, Locale.US).parse(this.date!!) ?: return null
}

fun Spot.addUpdateToFirebase(mContext: Context) {
    val database: DatabaseReference?
    this.id?.toMonthYearForFirebase()?.let {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.AREAS).child(FireHelper.ALABAMA).child(FireHelper.BIRMINGHAM)
            .child(FireHelper.SPOTS).child(it).child(this.id.toString()).setValue(this)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                Session.createNewSpot(this)
                showSuccess(mContext)
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                showFailedToast(mContext)
            }
    }
}

fun Spot.createDetailsLocationDialog(activity: Activity) : Dialog {
    val dialog = Dialog(activity, R.style.FT_Dialog)
    dialog.setContentView(R.layout.dialog_spot_details)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    //Location Info
    dialog.txtLocationNameDialog.setText(this.locationName)
    dialog.txtAddressOneDialog.setText(this.addressOne)
    if (this.addressTwo.isNullOrEmpty()) {
        dialog.txtAddressTwoDialog.visibility = View.GONE
    } else {
        dialog.txtAddressTwoDialog.setText(this.addressTwo)
    }
    dialog.txtCityStateZipDialog.setText(this.toCityStateZip())
    dialog.txtParkingInfoDialog.setText(this.parkingInfo)
    //Location Manager/Contact
    dialog.txtLocationManagerDialog.text = this.spotManager
    //Spot Details
    dialog.txtTimeDateDialog.setText(this.toFullDate()) //TIME AND DATE
    if (this.assignedTruckName.isNullOrEmpty()) {
        dialog.txtTruckNameDialog.visibility = View.GONE
    } else {
        dialog.txtTruckNameDialog.setText(this.assignedTruckName)
    }
    dialog.txtFoodTypeDialog.setText(this.foodType)
    dialog.txtEstPeopleDialog.setText(this.estPeople)
    dialog.txtCostDialog.setText(this.price)
    dialog.btnCloseDialog.setOnClickListener {
        dialog.dismiss()
    }
    dialog.btnGenericDialog.visibility = View.INVISIBLE
    return dialog
}

//fun Spot.createDetailsFoodtruckDialog(activity: Activity,
//                                      calendarFragment: FoodCalendarFragment? = null,
//                                      cartFragment: FoodCartFragment? = null,
//                                      isCart: Boolean = false) : Dialog {
//    val dialog = Dialog(activity, R.style.FT_Dialog)
//    dialog.setContentView(R.layout.dialog_spot_details)
//    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    //Location Info
//    dialog.txtLocationNameDialog.setText(this.locationName)
//    dialog.txtAddressOneDialog.setText(this.addressOne)
//    if (this.addressTwo.isNullOrEmpty()) {
//        dialog.txtAddressTwoDialog.visibility = View.GONE
//    } else {
//        dialog.txtAddressTwoDialog.setText(this.addressTwo)
//    }
//    dialog.txtCityStateZipDialog.setText(this.toCityStateZip())
//    dialog.txtParkingInfoDialog.setText(this.parkingInfo)
//    //Location Manager/Contact
//    dialog.txtLocationManagerDialog.text = this.spotManager
//    //Spot Details
//    dialog.txtTimeDateDialog.setText(this.toFullDate()) //TIME AND DATE
//    if (this.assignedTruckName.isNullOrEmpty()) {
//        dialog.txtTruckNameDialog.visibility = View.GONE
//    } else {
//        dialog.txtTruckNameDialog.setText(this.assignedTruckName)
//    }
//    dialog.txtFoodTypeDialog.setText(this.foodType)
//    dialog.txtEstPeopleDialog.setText(this.estPeople)
//    dialog.txtCostDialog.setText(this.price)
//    dialog.btnCloseDialog.setOnClickListener {
//        dialog.dismiss()
//    }
//
//    if (isCart) dialog.btnGenericDialog.text = "Remove" else dialog.btnGenericDialog.text = "Add"
//    dialog.btnGenericDialog.setOnClickListener {
//        //ADD SPOTS TO CART
//        if (isCart) {
//            this.createRemoveFromCartDialog(activity, cartFragment!!, dialog).show()
//        } else {
//            this.createAddToCartDialog(activity, calendarFragment!!, dialog).show()
//        }
//    }
//    return dialog
//}

//fun Spot.createAddToCartDialog(activity: Activity, fragment: FoodCalendarFragment, parent: Dialog) : Dialog {
//    val dialog = Dialog(activity)
//    dialog.setContentView(R.layout.dialog_ask_user_logout)
//    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    dialog.txtAskUserTitle.text = "Add to Cart?"
//    dialog.txtAskUserBody.text = "Are you sure you want to add this spot to your cart?"
//    // On Clicks
//    val yes = dialog.findViewById(R.id.btnYesAskUser) as Button
//    val cancel = dialog.findViewById(R.id.btnCancelAskUser) as Button
//
//    var assignedTruckUid = "null"
//    var assignedTruckName = "null"
//    getFoodtruck()?.let {
//        assignedTruckUid = it.id ?: "null"
//        assignedTruckName = it.truckName ?: "null"
//    }
//
//    yes.setOnClickListener {
//        val spot = this
//        main {
//            addSpotToCart(spot, activity)
//            spot.updatePendingAvailableToFirebase(activity, PENDING, assignedTruckUid, assignedTruckName)
//            fragment.spotsListAdapter?.notifyDataSetChanged()
//            dialog.dismiss()
//            parent.dismiss()
//        }
//    }
//    cancel.setOnClickListener {
//        dialog.dismiss()
//    }
//    return dialog
//}

//fun Spot.createRemoveFromCartDialog(activity: Activity, fragment: FoodCartFragment, parent: Dialog) : Dialog {
//    val dialog = Dialog(activity)
//    dialog.setContentView(R.layout.dialog_ask_user_logout)
//    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    dialog.txtAskUserTitle.text = "Remove from Cart?"
//    dialog.txtAskUserBody.text = "Are you sure you want to remove this spot from your cart?"
//    // On Clicks
//    val yes = dialog.findViewById(R.id.btnYesAskUser) as Button
//    val cancel = dialog.findViewById(R.id.btnCancelAskUser) as Button
//    yes.setOnClickListener {
//        val spot = this
//        main { removeSpotFromCart(spot, activity) }
//        spot.updatePendingAvailableToFirebase(activity, AVAILABLE, "", "")
//        fragment.cartAdapter?.notifyDataSetChanged()
//        fragment.setupDisplay()
//        dialog.dismiss()
//        parent.dismiss()
//    }
//    cancel.setOnClickListener {
//        dialog.dismiss()
//    }
//    return dialog
//}

//update Spot to PENDING
//fun Spot.updatePendingAvailableToFirebase(mContext:Context, status:String, assignedTruckUid:String, assignedTruckName:String, isCashApp:Boolean=false) {
//    val spot = this
//    val newSpot = Spot(spot.id, spot.addressOne, spot.addressTwo,
//        spot.city, spot.state, spot.zip, spot.date, spot.spotManager)
//    newSpot.apply {
//        //
//        this.status = status
//        this.assignedTruckUid = assignedTruckUid
//        this.assignedTruckName = assignedTruckName
//    }
//    val id = this.id ?: return
//    this.date?.toMonthYearForFirebase()?.let { itDate ->
//        firebase { itBase ->
//            itBase.child(FireHelper.AREAS)
//                .child(FireHelper.ALABAMA)
//                .child(FireHelper.BIRMINGHAM)
//                .child(FireHelper.SPOTS)
//                .child(itDate)
//                .child(id)
//                .setValue(newSpot)
//                .addOnSuccessListener {
//                    //TODO("HANDLE SUCCESS")
//                    when (status) {
//                        PENDING -> {
//                            Session.updateSpotAsPendingForFirebase(id, if (isCashApp) PENDING_CASHAPP else PENDING, assignedTruckUid, assignedTruckName)
//                        }
//                        AVAILABLE -> {
//                            Session.updateSpotAsAvailableForFirebase(id)
//                        }
//                    }
//                    showSuccess(mContext)
//                }.addOnCompleteListener {
//                    //TODO("HANDLE COMPLETE")
//                }.addOnFailureListener {
//                    //TODO("HANDLE FAILURE")
//                    showFailedToast(mContext)
//                }
//        }
//    }
//}

//update Spot to AVAILABLE
fun Spot.updateAvailableToFirebase(mContext: Context) {
    val database: DatabaseReference?
//    this.status = "available"
    val id = this.id ?: return
    this.date?.toMonthYearForFirebase()?.let {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.AREAS)
            .child(FireHelper.ALABAMA)
            .child(FireHelper.BIRMINGHAM)
            .child(FireHelper.SPOTS)
            .child(it)
            .child(id)
            .setValue(this)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
//                Session.updateSpotAsAvailableForFirebase(id)
                showSuccess(mContext)
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                showFailedToast(mContext)
            }
    }
}

//update Spot to BOOKED
fun Spot.updateBookedToFirebase(mContext: Context) {
    this.status = Spot.BOOKED
    val id = this.id ?: return
    this.date?.toMonthYearForFirebase()?.let { itDate ->
      firebase { itBase ->
          itBase.child(FireHelper.AREAS)
            .child(FireHelper.ALABAMA).child(FireHelper.BIRMINGHAM)
            .child(FireHelper.SPOTS)
            .child(itDate)
            .child(id)
            .setValue(this)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
//                Session.updateSpotAsBookedForFirebase(id)
                showSuccess(mContext)
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                showFailedToast(mContext)
            }
        }
    }
}

