package io.usys.report.ui.ludi.coach

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.usys.report.R
import io.usys.report.databinding.ProfileCoachBinding
import io.usys.report.realm.model.Coach
import io.usys.report.ui.fragments.LudiMiddleFragment
import io.usys.report.ui.ludi.review.coach.ReviewDialogFragment
import io.usys.report.utils.cast
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : 2020.
 */

class CoachProfileFragment : LudiMiddleFragment() {

    val _SAVE = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY

    val SAVE_BTN = "Save"
    val EDIT_BTN = "Edit"

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    private var _binding: ProfileCoachBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

//    private var reviewTemplate: ReviewTemplate? = null
    private var currentCoach: Coach? = null

    private var COLOR_RED: Drawable? = null
    private var COLOR_WHITE: Drawable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = ProfileCoachBinding.inflate(inflater, container, false)
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
        _binding?.userProfileCoachBtnReview?.setOnClickListener {
            launchCoachReviewFragment()
        }
        setupDisplay()
        setupProfileImage()
        return rootView
    }

    private fun setupDisplay() {
        //General User Info
        currentCoach?.let {
            _binding?.includeUserProfileCoachHeader?.cardUserHeaderTxtProfileName?.text = it.name
//            _binding?.includeUserProfileCoachHeader?.cardUserHeaderRatingBar?.rating = it.reviewBundle?.ratingScore?.toFloat()!!
//            _binding?.includeUserProfileCoachHeader?.cardUserHeaderTxtProfileReviewCount?.text = "${it.reviewBundle?.ratingCount} Reviews"

//            _binding?.fragProfileCoachRecyclerServices?.setupServiceList(requireContext(), itemOnClick)
        }
    }

    private fun setupProfileImage() {
        currentCoach?.let { itCoach ->
            if (!itCoach.imgUrl.isNullOrEmpty()) {
                _binding?.includeUserProfileCoachHeader?.cardUserHeaderImgProfile?.let { itImgView ->
                    this.loadUriIntoImgView(itCoach.imgUrl!!, itImgView)
                }
            }
        }

    }

    private fun launchCoachReviewFragment() {
        currentCoach?.let { ReviewDialogFragment(it).show(childFragmentManager, ReviewDialogFragment.TAG) }
    }




}