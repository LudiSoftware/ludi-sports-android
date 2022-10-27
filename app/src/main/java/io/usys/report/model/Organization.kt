package io.usys.report.model

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.RealmList
import io.usys.report.AuthController
import io.usys.report.R
import io.usys.report.coachUser.manage.LocManageFragment
import io.realm.RealmObject
import io.usys.report.ui.BaseListAdapter
import io.usys.report.utils.*
import kotlinx.android.synthetic.main.dialog_ask_user_logout.*
import java.io.Serializable

/**
 * Created by ChazzCoin : December 2019.
 */
open class Organization : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sport: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = ""
    var managerId: String? = "unassigned"
    var managerName: String? = "unassigned"
    var staff: RealmList<String>? = null
    var estPeople: String? = ""

    var reviews: RealmList<String>? = null

    fun getCityStateZip(): String {
        return "$city, $state $zip"
    }


    fun matches(org: Organization) : Boolean {
        if (this.id == org.id &&
            this.name == org.name &&
            this.addressOne == org.addressOne &&
            this.addressTwo == org.addressTwo &&
            this.city == org.city &&
            this.state == org.state &&
            this.zip == org.zip &&
            this.estPeople == org.estPeople) return true
        return false
    }

}

//private fun <E> RealmList<E>.add(element: JSONObject?) {
//    this.add(element as? E)
//}

fun Organization.createDeleteLocationDialog(fragment: LocManageFragment) : Dialog {
    val dialog = Dialog(fragment.requireActivity())
    dialog.setContentView(R.layout.dialog_ask_user_logout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialog.txtAskUserTitle.text = "Delete?"
    dialog.txtAskUserBody.text = "Are you sure you want to delete this location?"

    // On Clicks
    dialog.btnYesAskUser.setOnClickListener {
        //REMOVE FROM FIREBASE
        this.removeFromFirebase(fragment)
        dialog.dismiss()
    }
    dialog.btnCancelAskUser.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}


fun Organization.removeFromFirebase(fragment: LocManageFragment? = null) {
    Log.d("Location: ", "removeFromFirebase")
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
        .child(AuthController.USER_ID).child(this.id.toString())
        .removeValue()
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            fragment?.reloadLocAdapter()
            fragment?.let { showSuccess(it.requireContext(), "Location Removed!") }
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            fragment?.let { showFailedToast(it.requireContext()) }
        }
}