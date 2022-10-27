package io.usys.report.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.usys.report.R
import io.usys.report.model.AuthTypes
import io.usys.report.model.Session
import io.usys.report.model.Spot
import io.usys.report.model.User
import kotlinx.android.synthetic.main.activity_spot_details.*

/**
 * Created by ChazzCoin : 2020.
 */
class SpotDetailsActivity : AppCompatActivity() {

    private var MODE = "DISPLAY"
    private val SAVE = "Save"
    private val EDIT = "Edit"
    private val ADD_TO_CART = "Add to Cart"
    private val REMOVE_FROM_CART = "Remove from Cart"
    var isLocationManager = false
    var isCartMode = false

    var user : User? = null
    var spot : Spot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_details)

        intent.extras?.let {
            if (it.containsKey("cart")) { isCartMode = it.getBoolean("cart") }
            if (it.containsKey("spot")) { spot = it.getSerializable("spot") as? Spot }
        }

        Session.session?.let {
            user = Session.user
            isLocationManager = (user?.auth == AuthTypes.BASIC_USER)
            toggleGenericEditButton()
        }

        setupDisplay()
    }

    private fun setupDisplay() {

        //Location Info
        txtLocationName.setText(spot?.locationName)
        txtAddressOne.setText(spot?.addressOne)
        txtAddressTwo.setText(spot?.addressTwo)
        txtParkingInfo.setText(spot?.parkingInfo)
        //Location Manager/Contact
        txtLocationManager.text = spot?.spotManager
        //Spot Details
        txtTimeDate.setText(spot?.date) //TIME AND DATE
        txtTruckName.setText(spot?.assignedTruckName)
        txtFoodType.setText(spot?.foodType)
        txtEstPeople.setText(spot?.estPeople)
        txtCost.setText(spot?.price)
        //Buttons
        btnGeneric.setOnClickListener {
            when (MODE) {
                SAVE -> {
                    //Save spot details for Location Manager
                    //Verify details
                    //Grab Details
                    //Save Details to Spot
                    toggleGenericEditButton()
                    //Fields to display only
                }
                EDIT -> {
                    //toggle button to SAVE
                    toggleGenericEditButton()
                    //make fields editable
                }
                REMOVE_FROM_CART -> {
                    //Put spot back to AVAILABLE
                    //Remove spot from cartList
                }
                ADD_TO_CART -> {
                    //Make Spot PENDING
                    //Add spot to cartList
                }
                else -> {
                    //display mode
                    //Make everything display mode
                    toggleGenericEditButton()
                }

            }
            //if editMode ->

        }

        btnClose.setOnClickListener {
            //TODO: Ask if they are sure?
            onBackPressed()
        }

    }

    private fun toggleGenericEditButton() {

        if (isLocationManager){
            if (MODE == EDIT) {
                btnGeneric.text = SAVE
                MODE = SAVE
            } else {
                btnGeneric.text = EDIT
                MODE = EDIT
            }
        } else {
            if (isCartMode) {
                btnGeneric.text = REMOVE_FROM_CART
                MODE = REMOVE_FROM_CART
            } else {
                btnGeneric.text = ADD_TO_CART
                MODE = ADD_TO_CART
            }
        }

    }
}