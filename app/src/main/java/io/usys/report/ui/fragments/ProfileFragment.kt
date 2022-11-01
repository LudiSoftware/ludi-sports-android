package io.usys.report.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.usys.report.R
import io.usys.report.databinding.FragmentUserProfileBinding
import io.usys.report.db.FireTypes.Companion.USER_PROFILE_IMAGE_PATH_BY_ID
import io.usys.report.db.getDownloadUrlAsync
import io.usys.report.db.getStorageRefByPath
import io.usys.report.utils.loadUriIntoImgView
import io.usys.report.utils.safeUserId

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
        safeUserId { itId ->
            val path = USER_PROFILE_IMAGE_PATH_BY_ID(itId)
            getStorageRefByPath(path).getDownloadUrlAsync {
                _binding?.imgProfileUser?.let { itImgView ->
                    this.loadUriIntoImgView(it, itImgView)
                }
            }
        }


        return rootView
    }

    private fun setupDisplay() {
        //General User Info
        _binding?.txtProfileName?.setText(user?.name)
        _binding?.editLayoutProfileEmail?.setText(user?.email)
//        rootView.editPhoneNumber.setText(user?.phone)
    }


}