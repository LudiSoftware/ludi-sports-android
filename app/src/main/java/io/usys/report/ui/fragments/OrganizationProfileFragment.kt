package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.databinding.FragmentOrgProfileBinding
import io.usys.report.ui.reviewSystem.ReviewDialogFragment
import io.usys.report.ui.setupReviewList
import io.usys.report.utils.cast
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationProfileFragment : YsrMiddleFragment() {

    private var _binding: FragmentOrgProfileBinding? = null
    private val binding get() = _binding!!
    private var organization: Organization? = null

//    private var hasBeenLoaded = false
//    private var organizationList: RealmList<Organization>? = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgProfileBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        organization = realmObjectArg?.cast<Organization>()
        organization?.let {
            _binding?.recyclerList.setupReviewList(requireContext(), it.id, itemOnClick)
        }
        _binding?.includeItemTitleList?.itemTitleListTxtTitle?.text = "Reviews"
        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Staff / Coaches"
        //
        val imageDraw = AppCompatResources.getDrawable(requireContext(), R.drawable.path_4229)
        _binding?.includeGenericButtonCard?.cardGenericButtonImgIcon?.setImageDrawable(imageDraw)
        _binding?.includeGenericButtonCard?.cardGenericButtonMainLayout?.setOnClickListener {
            organization?.let {
                toFragment(R.id.navigation_coaches_list, bundleRealmObject(it))
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
            toFragment(R.id.navigation_coaches_list, bundleRealmObject(obj))
        }
    }

}
