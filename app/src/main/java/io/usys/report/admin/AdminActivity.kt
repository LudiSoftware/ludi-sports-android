package io.usys.report.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.usys.report.R
import io.usys.report.utils.FireHelper
import io.usys.report.utils.showFailedToast
import io.usys.report.utils.showSuccess

class AdminActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val map = mutableMapOf<String,String>()
        map.apply {
            this["1"] = "truck1"
            this["2"] = "truck2"
            this["3"] = "truck3"
            this["4"] = "truck4"
        }
        addUpdateAdmin(map as HashMap<String, String>)

    }


    //SAVE SPOT
    private fun addUpdateAdmin(map:HashMap<String,String>) {
        Log.d("AdminActivity: ", "saveSpotToFirebase")
        database = FirebaseDatabase.getInstance().reference
        database
            .child(FireHelper.PROFILES)
            .child(FireHelper.TRUCKLIST)
            .setValue(map)
            .addOnSuccessListener {
                showSuccess(this)
            }.addOnFailureListener {
                showFailedToast(this)
            }
    }
}