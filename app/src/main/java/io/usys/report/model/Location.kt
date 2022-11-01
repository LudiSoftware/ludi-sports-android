package io.usys.report.model

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.usys.report.utils.*
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Spot : RealmObject() {


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
    var status : String? = "" //Has it been bought?
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









