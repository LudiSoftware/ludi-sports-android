package io.usys.report.model

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.RealmList
import io.usys.report.utils.*
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ChazzCoin : October 2022.
 */
open class Location : RealmObject() {


    @PrimaryKey
    var id: String? = ""
    var name: String? = ""
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sports: RealmList<String>? = null
    var dateCreated: String? = "" // timestamp
    var parkingInfo: String? = "" // "Park on the third spot to the right"
    var estPeople: String? = "" //Amount of expected people
    var status : String? = "" //Has it been bought?
    var locationManager: String? = "" //Creators Display Name
    var organizationId: String? = ""
    var hasReview: Boolean = false
    var reviewId: String? = ""


}

fun Location.isOld(): Boolean {
    if (this.dateCreated == null) return false
    val toDate = SimpleDateFormat(FireHelper.SPOT_DATE_FORMAT, Locale.US).parse(this.dateCreated!!) ?: return false
    return toDate.before(Date())
}

fun Location.getDate(): Date? {
    if (this.dateCreated == null) return null
    return SimpleDateFormat(FireHelper.SPOT_DATE_FORMAT, Locale.US).parse(this.dateCreated!!) ?: return null
}

fun Location.addUpdateToFirebase(mContext: Context) {
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









