package io.usys.report.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.usys.report.R
import io.usys.report.databinding.FragmentUserProfileBinding

/**
 * Created by ChazzCoin : 2020.
 */

class ProfileFragment : YsrFragment() {

    val _SAVE = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY

    val SAVE_BTN = "Save"
    val EDIT_BTN = "Edit"

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    private var _binding: FragmentUserProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var COLOR_RED: Drawable? = null
    private var COLOR_WHITE: Drawable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        rootView = binding.root

        COLOR_RED = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_red)
        COLOR_WHITE = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_white)

//        rootView.btnProfileReview.setOnClickListener {
//            createReviewDialog(requireActivity()).show()
//        }

//        //-> Global On Click Listeners
//        rootView.btnLogout.setOnClickListener {
//            //FOR TESTING/ADMIN WORK ONLY
//            if (Session.isLogged){
//                popAskUserLogoutDialog(requireActivity()).show()
//            }
//        }

        return rootView
    }

    private fun setupDisplay() {

        //General User Info
        _binding?.txtProfileName?.setText(user?.name)
        _binding?.editLayoutProfileEmail?.setText(user?.email)
//        rootView.editPhoneNumber.setText(user?.phone)
    }



//    private fun setupOnClickHandlers() {
//        //Update Button
//        rootView.btnUpdateProfile.setOnClickListener {
//            when (MODE) {
//                _DISPLAY -> {
//                    MODE = _SAVE
//                    rootView.btnUpdateProfile.text = SAVE_BTN
//                    rootView.btnCancelProfile.isEnabled = true
//                    toggleDisplay(true)
//                    return@setOnClickListener
//                }
//                _SAVE -> {
//                    //-> BOTH
//                    if (rootView.editProfileName.text.isNullOrEmpty()) {
//                        rootView.editProfileName.setHintTextColor(Color.RED)
//                        return@setOnClickListener
//                    }
//                    if (rootView.editEmail.text.isNullOrEmpty()) {
//                        rootView.editEmail.setHintTextColor(Color.RED)
//                        return@setOnClickListener
//                    }
//                    if (rootView.editPhoneNumber.text.isNullOrEmpty()) {
//                        rootView.editPhoneNumber.setHintTextColor(Color.RED)
//                        return@setOnClickListener
//                    }
//
//                    //-> FOODTRUCK
//                    if (user?.auth == AuthTypes.BASIC_USER) {
//                        if (editTruckName.text.isNullOrEmpty()) {
//                            rootView.editTruckName.setHintTextColor(Color.RED)
//                            return@setOnClickListener
//                        }
//                    }
//
//                    //-> BOTH
//                    val newUser = User().apply {
//                        this.id = AuthControllerActivity.USER_ID
//                        this.auth = AuthControllerActivity.USER_AUTH
//                        this.name = rootView.editProfileName.text.toString()
//                        this.email = rootView.editEmail.text.toString()
//                        this.phone = rootView.editPhoneNumber.text.toString()
//                    }
//
//                    if (!user!!.equals(newUser)) {
//                        newUser.addUpdateToFirebase(requireActivity())
//                        resetDisplay()
//                    }
//
////
//                }
//            }
//        }
//        //
//        rootView.btnCancelProfile.setOnClickListener {
//            when (MODE) {
//                _SAVE -> {
//                    MODE = _DISPLAY
//                    rootView.btnUpdateProfile.text = EDIT_BTN
//                    rootView.btnCancelProfile.isEnabled = false
//                    toggleDisplay(false)
//                }
//            }
//        }
//        //
//    }

//    private fun resetDisplay() {
//        MODE = _DISPLAY
//        rootView.btnUpdateProfile.text = EDIT_BTN
//        rootView.btnCancelProfile.isEnabled = false
//        //General
//        rootView.editProfileName.background = COLOR_WHITE
//        rootView.editEmail.background = COLOR_WHITE
//        rootView.editPhoneNumber.background = COLOR_WHITE
//        //Truck Name
//        if (user?.auth == AuthTypes.BASIC_USER) {
//            rootView.editTruckName.background = COLOR_WHITE
//            //Spinners
//            rootView.spinFoodtruckType.background = COLOR_WHITE
//            rootView.spinFoodtruckType2.background = COLOR_WHITE
//            //Checks
//            rootView.checkEntreeProfile.background = COLOR_WHITE
//            rootView.checkDessertProfile.background = COLOR_WHITE
//        }
//        toggleDisplay(false)
//    }
//
//    private fun toggleDisplay(enable:Boolean) {
//        rootView.editProfileName.isEnabled = enable
//        rootView.editEmail.isEnabled = enable
//        rootView.editPhoneNumber.isEnabled = enable
//        if (user?.auth == AuthTypes.BASIC_USER) {
//            rootView.editTruckName.isEnabled = enable
//            rootView.spinFoodtruckType.isEnabled = enable
//            rootView.spinFoodtruckType2.isEnabled = enable
//            rootView.checkEntreeProfile.isEnabled = enable
//            rootView.checkDessertProfile.isEnabled = enable
//        }
//    }

}