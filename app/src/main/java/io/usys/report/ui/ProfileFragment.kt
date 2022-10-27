package io.usys.report.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.usys.report.AuthController
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.getSpinnerForFoodTruckType
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ChazzCoin : 2020.
 */

class ProfileFragment : Fragment() {

    val _SAVE = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY

    val SAVE_BTN = "Save"
    val EDIT_BTN = "Edit"
    val EMPTY_SPINNER = "Pick Food Type"

    var user : User? = null
    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var rootView : View

    lateinit var eSpinAdapter: ArrayAdapter<String?>
    var foodtruckType: String? = null
    var foodtruckType2: String? = null
    var foodtruckFoodType: String? = null

    private var COLOR_RED: Drawable? = null
    private var COLOR_WHITE: Drawable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)

        COLOR_RED = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_red)
        COLOR_WHITE = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_white)

        Session.session?.let { user = Session.user }
        if (user?.auth != AuthTypes.BASIC_USER) {
            rootView.linearLayoutTruckInfo.visibility = View.GONE
        }

        eSpinAdapter = getSpinnerForFoodTruckType(requireContext())

        setupOnClickHandlers()
        setupSpinners()
        setupDisplay()

        return rootView
    }

    private fun setupDisplay() {

        //General User Info
        rootView.editProfileName.setText(user?.name)
        rootView.editEmail.setText(user?.email)
        rootView.editPhoneNumber.setText(user?.phone)
    }




    private fun setupSpinners() {
        //Foodtruck Type
        rootView.spinFoodtruckType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val array = requireActivity().resources.getStringArray(R.array.foodtruck_types)
                foodtruckType = array[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }
        rootView.spinFoodtruckType.adapter = eSpinAdapter
        //Sub Foodtruck Type
        rootView.spinFoodtruckType2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val array = requireActivity().resources.getStringArray(R.array.foodtruck_types)
                foodtruckType2 = array[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }
        rootView.spinFoodtruckType2.adapter = eSpinAdapter
    }


    private fun setupOnClickHandlers() {
        //Update Button
        rootView.btnUpdateProfile.setOnClickListener {
            when (MODE) {
                _DISPLAY -> {
                    MODE = _SAVE
                    rootView.btnUpdateProfile.text = SAVE_BTN
                    rootView.btnCancelProfile.isEnabled = true
                    toggleDisplay(true)
                    return@setOnClickListener
                }
                _SAVE -> {
                    //-> BOTH
                    if (rootView.editProfileName.text.isNullOrEmpty()) {
                        rootView.editProfileName.setHintTextColor(Color.RED)
                        return@setOnClickListener
                    }
                    if (rootView.editEmail.text.isNullOrEmpty()) {
                        rootView.editEmail.setHintTextColor(Color.RED)
                        return@setOnClickListener
                    }
                    if (rootView.editPhoneNumber.text.isNullOrEmpty()) {
                        rootView.editPhoneNumber.setHintTextColor(Color.RED)
                        return@setOnClickListener
                    }

                    //-> FOODTRUCK
                    if (user?.auth == AuthTypes.BASIC_USER) {
                        if (editTruckName.text.isNullOrEmpty()) {
                            rootView.editTruckName.setHintTextColor(Color.RED)
                            return@setOnClickListener
                        }
                        if (foodtruckType == EMPTY_SPINNER) {
                            rootView.spinFoodtruckType.background = COLOR_RED
                            return@setOnClickListener
                        }
                        if (foodtruckType2 == EMPTY_SPINNER) {
                            rootView.spinFoodtruckType2.background = COLOR_RED
                            return@setOnClickListener
                        }
                        if (foodtruckFoodType.isNullOrEmpty()) {
                            rootView.checkEntreeProfile.background = COLOR_RED
                            rootView.checkDessertProfile.background = COLOR_RED
                            return@setOnClickListener
                        }
                    }

                    //-> BOTH
                    val newUser = User().apply {
                        this.id = AuthController.USER_ID
                        this.auth = AuthController.USER_AUTH
                        this.name = rootView.editProfileName.text.toString()
                        this.email = rootView.editEmail.text.toString()
                        this.phone = rootView.editPhoneNumber.text.toString()
                    }

                    if (!user!!.equals(newUser)) {
                        newUser.addUpdateToFirebase(requireActivity())
                        resetDisplay()
                    }

//                    //-> FOODTRUCK USER
//                    if (user?.auth == AuthTypes.BASIC_USER) {
//                        val trucks : RealmList<Organization>? = Session.session?.foodtrucks
//                        val newTruckName = rootView.editTruckName.text.toString()
//                        if (!trucks.isNullOrEmpty()) {
//                            val curTruck = trucks[0] as Organization
//                            val tid = curTruck.id
//                            if (foodtruckType != foodtruckType2) {
//                                Organization(tid, AuthController.USER_UID, newTruckName, foodtruckType, foodtruckType2, foodtruckFoodType)
//                                    .addUpdateForFirebase(requireActivity())
//                                resetDisplay()
//                            } else {
//                                rootView.spinFoodtruckType.background = COLOR_RED
//                                rootView.spinFoodtruckType2.background = COLOR_RED
//                            }
//                        } else {
//                            val id = UUID.randomUUID().toString()
//                            Organization(id, AuthController.USER_UID, newTruckName, foodtruckType, foodtruckType2, foodtruckFoodType)
//                                .addUpdateForFirebase(requireActivity())
//                            resetDisplay()
//                        }
//                    }
                }
            }
        }
        //
        rootView.btnCancelProfile.setOnClickListener {
            when (MODE) {
                _SAVE -> {
                    MODE = _DISPLAY
                    rootView.btnUpdateProfile.text = EDIT_BTN
                    rootView.btnCancelProfile.isEnabled = false
                    toggleDisplay(false)
                }
            }
        }
        //
    }

    private fun resetDisplay() {
        MODE = _DISPLAY
        rootView.btnUpdateProfile.text = EDIT_BTN
        rootView.btnCancelProfile.isEnabled = false
        //General
        rootView.editProfileName.background = COLOR_WHITE
        rootView.editEmail.background = COLOR_WHITE
        rootView.editPhoneNumber.background = COLOR_WHITE
        //Truck Name
        if (user?.auth == AuthTypes.BASIC_USER) {
            rootView.editTruckName.background = COLOR_WHITE
            //Spinners
            rootView.spinFoodtruckType.background = COLOR_WHITE
            rootView.spinFoodtruckType2.background = COLOR_WHITE
            //Checks
            rootView.checkEntreeProfile.background = COLOR_WHITE
            rootView.checkDessertProfile.background = COLOR_WHITE
        }
        toggleDisplay(false)
    }

    private fun toggleDisplay(enable:Boolean) {
        rootView.editProfileName.isEnabled = enable
        rootView.editEmail.isEnabled = enable
        rootView.editPhoneNumber.isEnabled = enable
        if (user?.auth == AuthTypes.BASIC_USER) {
            rootView.editTruckName.isEnabled = enable
            rootView.spinFoodtruckType.isEnabled = enable
            rootView.spinFoodtruckType2.isEnabled = enable
            rootView.checkEntreeProfile.isEnabled = enable
            rootView.checkDessertProfile.isEnabled = enable
        }
    }

}