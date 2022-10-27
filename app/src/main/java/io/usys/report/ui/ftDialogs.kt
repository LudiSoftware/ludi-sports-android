package io.usys.report.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import io.usys.report.R
import io.usys.report.model.*
import kotlinx.android.synthetic.main.dialog_ask_user_logout.*
import kotlinx.android.synthetic.main.dialog_field_error.*


/**
 * Created by ChazzCoin : Feb 2021.
 */


fun createErrorDialog(activity: Activity, message:String? = "Something has gone wrong") : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_field_error)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.txtFieldErrorTitle.text = "Uh Oh!"
    dialog.txtFieldErrorBody.text = message
    // On Clicks
    val okay = dialog.findViewById(R.id.btnOkayFieldError) as Button
    okay.setOnClickListener {
        activity.onBackPressed()
        dialog.dismiss()
    }
    return dialog
}

fun createFieldErrorDialog(activity: Activity) : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_field_error)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    // On Clicks
    val okay = dialog.findViewById(R.id.btnOkayFieldError) as Button
    okay.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun createAskUserLogoutDialog(activity: Activity) : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_ask_user_logout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val txtTitle = dialog.findViewById(R.id.txtAskUserBody) as TextView
    txtTitle.text = "Are you sure you want to logout?"
    // On Clicks
    val yes = dialog.findViewById(R.id.btnYesAskUser) as Button
    val cancel = dialog.findViewById(R.id.btnCancelAskUser) as Button
    yes.setOnClickListener {
        Session.restartApplication(activity)
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun createAskUserGenericDialog(activity: Activity, title: String, body: String) : Dialog {
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_ask_user_logout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.txtAskUserTitle.text = title
    dialog.txtAskUserBody.text = body
    // On Clicks
    val yes = dialog.findViewById(R.id.btnYesAskUser) as Button
    val cancel = dialog.findViewById(R.id.btnCancelAskUser) as Button
    yes.setOnClickListener {
        Session.restartApplication(activity)
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun createProfileDialog(activity: Activity, user: User) : Dialog {

    val _SAVE = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY
    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.fragment_user_profile)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    val editProfileName = dialog.findViewById(R.id.editProfileName) as EditText
    val editEmail = dialog.findViewById(R.id.editEmail) as EditText
    val editPhoneNumber = dialog.findViewById(R.id.editPhoneNumber) as EditText

    val linearLayoutTruckInfo = dialog.findViewById(R.id.linearLayoutTruckInfo) as LinearLayout
    val editTruckName = dialog.findViewById(R.id.editTruckName) as EditText
    val spinTruckFoodType = dialog.findViewById(R.id.spinFoodtruckType) as Spinner
    val spinTruckFoodType2 = dialog.findViewById(R.id.spinFoodtruckType2) as Spinner
    var foodtruckType: String? = null
    var foodtruckType2: String? = null
    val eSpinAdapter = ArrayAdapter(
        activity,
        android.R.layout.simple_list_item_1,
        activity.resources.getStringArray(R.array.foodtruck_types)
    )
    //Foodtruck Type
    spinTruckFoodType.onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val array = activity.resources.getStringArray(R.array.foodtruck_types)
            foodtruckType = array[position]

        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // sometimes you need nothing here
        }
    }
    spinTruckFoodType.adapter = eSpinAdapter
    //Sub Foodtruck Type
    spinTruckFoodType2.onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val array = activity.resources.getStringArray(R.array.foodtruck_types)
            foodtruckType2 = array[position]

        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // sometimes you need nothing here
        }
    }
    spinTruckFoodType2.adapter = eSpinAdapter

    if (user.auth != AuthTypes.BASIC_USER) {
        linearLayoutTruckInfo.visibility = View.GONE
    }

    editProfileName.setText(user.name)
    editEmail.setText(user.email)
    editPhoneNumber.setText(user.phone)

    editProfileName.isEnabled = false
    editEmail.isEnabled = false
    editPhoneNumber.isEnabled = false

//    if (user.auth == AuthTypes.BASIC_USER) {
//        val trucks : RealmList<Organization>? = Session.session?.foodtrucks
//        if (!trucks.isNullOrEmpty()) {
//            val truck = trucks?.first()
//            editTruckName.setText(truck?.truckName)
//            spinTruckFoodType.setSelection(
//                (spinTruckFoodType.adapter as ArrayAdapter<String>).getPosition(truck?.truckType)
//            )
//            spinTruckFoodType2.setSelection(
//                (spinTruckFoodType2.adapter as ArrayAdapter<String>).getPosition(truck?.truckSubType)
//            )
//        }
//        editTruckName.isEnabled = false
//        spinTruckFoodType.isEnabled = false
//        spinTruckFoodType2.isEnabled = false
//    }

    // On Clicks
    val update = dialog.findViewById(R.id.btnUpdateProfile) as Button
    val cancel = dialog.findViewById(R.id.btnCancelProfile) as Button
    update.setOnClickListener {
        when (MODE) {
            _DISPLAY -> {
                MODE = _SAVE
                update.text = "Save"
                editProfileName.isEnabled = true
                editEmail.isEnabled = true
                editPhoneNumber.isEnabled = true
                if (user.auth == AuthTypes.BASIC_USER) {
                    editTruckName.isEnabled = true
                    spinTruckFoodType.isEnabled = true
                    spinTruckFoodType2.isEnabled = true
                }
                return@setOnClickListener
            }
            _SAVE -> {
                //-> BOTH
                if (editProfileName.text.isNullOrEmpty()) {
                    editProfileName.setHintTextColor(Color.RED)
                    return@setOnClickListener
                }
                if (editEmail.text.isNullOrEmpty()) {
                    editEmail.setHintTextColor(Color.RED)
                    return@setOnClickListener
                }
                if (editPhoneNumber.text.isNullOrEmpty()) {
                    editPhoneNumber.setHintTextColor(Color.RED)
                    return@setOnClickListener
                }

                //-> FOODTRUCK
                if (user.auth == AuthTypes.BASIC_USER) {
                    if (editTruckName.text.isNullOrEmpty()) {
                        editTruckName.setHintTextColor(Color.RED)
                        return@setOnClickListener
                    }
                    if (foodtruckType == "Pick Food Type") {
                        spinTruckFoodType.background =
                            activity.resources.getDrawable(R.drawable.ft_border_rounded_red)
                        return@setOnClickListener
                    }
                    if (foodtruckType2 == "Pick Food Type") {
                        spinTruckFoodType2.background =
                            activity.resources.getDrawable(R.drawable.ft_border_rounded_red)
                        return@setOnClickListener
                    }
                }

                //-> BOTH
                val newUser = User().apply {
                    this.id = user.id
                    this.auth = user.auth
                    this.name = editProfileName.text.toString()
                    this.email = editEmail.text.toString()
                    this.phone = editPhoneNumber.text.toString()
                }

                if (!user.equals(newUser)) {
                    newUser.addUpdateToFirebase(activity)
                }

//                //-> FOODTRUCK USER
//                if (user.auth == AuthTypes.BASIC_USER) {
//                    val trucks : RealmList<Organization>? = Session.session?.foodtrucks
//                    val newTruckName = editTruckName.text.toString()
//                    if (!trucks.isNullOrEmpty()) {
//                        val curTruck = trucks!![0] as Organization
//                        val tid = curTruck.id
//                        if (foodtruckType != foodtruckType2) {
//                            Organization(tid, AuthController.USER_UID, newTruckName, foodtruckType, foodtruckType2)
//                                .addUpdateForFirebase(activity)
//                            dialog.dismiss()
//                        } else {
//                            //TODO: ADD POP UP FOR ERROR
//                            spinTruckFoodType.background =
//                                activity.resources.getDrawable(R.drawable.ft_border_rounded_red)
//                            spinTruckFoodType2.background =
//                                activity.resources.getDrawable(R.drawable.ft_border_rounded_red)
//                        }
//                    } else {
//                        val id = UUID.randomUUID().toString()
//                        Organization(id, AuthController.USER_UID, newTruckName, foodtruckType, foodtruckType2)
//                            .addUpdateForFirebase(activity)
//                        dialog.dismiss()
//                    }
//                }
            }
        }
    }

    cancel.setOnClickListener {
        when (MODE) {
            _SAVE -> {
                MODE = _DISPLAY
                update.text = "Edit"
                editProfileName.isEnabled = false
                editEmail.isEnabled = false
                editPhoneNumber.isEnabled = false
                if (user.auth == AuthTypes.BASIC_USER) {
                    editTruckName.isEnabled = false
                    spinTruckFoodType.isEnabled = false
                    spinTruckFoodType2.isEnabled = false
                }
            }
            _DISPLAY -> {
                dialog.dismiss()
            }
        }
    }
    return dialog
}
