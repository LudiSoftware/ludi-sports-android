package io.usys.report.ui.ludi.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import io.usys.report.R
import io.usys.report.databinding.ProfileOrganizationBinding
import io.usys.report.realm.model.Organization
import io.usys.report.ui.fragments.LudiMiddleFragment
import io.usys.report.ui.ludi.review.organization.createOrgReviewDialog
import io.usys.report.ui.views.navController.bundleRealmObject
import io.usys.report.ui.views.navController.toFragmentWithRealmObject
import io.usys.report.utils.cast
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationProfileFragment : LudiMiddleFragment() {

    private var _binding: ProfileOrganizationBinding? = null
    private val binding get() = _binding!!
    private var organization: Organization? = null

//    private var hasBeenLoaded = false
//    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileOrganizationBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        organization = realmObjectArg?.cast<Organization>()
        organization?.let {
//            _binding?.recyclerList.setupOrgReviewCommentList(requireContext(), it.id, itemOnClick)
        }
        _binding?.includeItemTitleList?.itemTitleListTxtTitle?.text = "Reviews"
        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Staff / Coaches"
        //
        val imageDraw = AppCompatResources.getDrawable(requireContext(), R.drawable.path_4229)
//        _binding?.includeGenericButtonCard?.cardGenericButtonImgIcon?.setImageDrawable(imageDraw)
        _binding?.includeGenericButtonCard?.cardGenericButtonMainLayout?.setOnClickListener {
            organization?.let {
                toFragmentWithRealmObject(R.id.navigation_coaches_list, bundleRealmObject(it))
            }
        }
    }

    override fun setupOnClickListeners() {

        _binding?.orgProfileImgBtnLeaveReview?.setOnClickListener {
            log("Leave A Review Button!")
//            ReviewDialogFragment().show(childFragmentManager, ReviewDialogFragment.TAG)
//            createOrgReviewDialog2(requireActivity()).show()
            organization?.let { it1 ->
                createOrgReviewDialog(requireActivity(), it1).show()
            }
        }

        itemOnClick = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
