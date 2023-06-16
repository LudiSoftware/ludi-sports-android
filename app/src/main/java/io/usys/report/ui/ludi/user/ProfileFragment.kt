package io.usys.report.ui.ludi.user

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.usys.report.R
import io.usys.report.databinding.ProfileUserBinding
import io.usys.report.realm.model.users.Coach
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.LudiMiddleFragment
import io.usys.report.utils.cast
import io.usys.report.utils.toUri
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : 2020.
 */

class ProfileFragment : LudiMiddleFragment() {

    val _SAVE = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY

    val SAVE_BTN = "Save"
    val EDIT_BTN = "Edit"

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    private var _binding: ProfileUserBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

//    private var reviewTemplate: ReviewTemplate? = null
    private var currentCoach: Coach? = null

    private var COLOR_RED: Drawable? = null
    private var COLOR_WHITE: Drawable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = ProfileUserBinding.inflate(inflater, container, false)
        rootView = binding.root

        COLOR_RED = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_red)
        COLOR_WHITE = ContextCompat.getDrawable(requireActivity(), R.drawable.ft_border_rounded_white)

        currentCoach = realmObjectArg?.cast<Coach>()

//        //-> Global On Click Listeners
//        rootView.btnLogout.setOnClickListener {
//            //FOR TESTING/ADMIN WORK ONLY
//            if (Session.isLogged){
//                popAskUserLogoutDialog(requireActivity()).show()
//            }
//        }
        setupProfileImage()
        return rootView
    }


    private fun setupProfileImage() {

//        realmInstance?.safeUser { itUser ->
//            itUser.photoUrl.toUri()?.let { itUri ->
//                _binding?.includeHeader?.root?.rcardUserHeaderImgProfile?.let { itImgView ->
//                    this.loadUriIntoImgView(itUri, itImgView)
//                }
//            }
//        }

//        safeUserId { itId ->
//            val path = USER_PROFILE_IMAGE_PATH_BY_ID(itId)
//            fireStorageRefByPath(path).getDownloadUrlAsync {
//                _binding?.includeUserProfileHeader?.cardUserHeaderImgProfileUser?.let { itImgView ->
//                    this.loadUriIntoImgView(it, itImgView)
//                    coreUpdateProfileImageUri(it)
//                }
//            }
//        }
    }

}