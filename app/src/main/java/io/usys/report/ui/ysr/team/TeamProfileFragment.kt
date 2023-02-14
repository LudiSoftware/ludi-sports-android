package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.model.*
import io.usys.report.ui.fragments.MyBottomSheetDrawerFragment
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.utils.YsrMode

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragment : YsrMiddleFragment() {

    private var _MODE = YsrMode.TRYOUTS

    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!
    private var team: Team? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        setupDisplay()

        return rootView
    }

    private fun setupDisplay() {
        setupHeader()
//        _binding?.includeItemTitleList?.itemTitleListTxtTitle?.text = "Try-Out Roster"
//        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Services"
    }

    private fun setupHeader() {
//        _binding?.includeOrgProfileCard?.cardTxtTitle?.text = "U17 Lady Angels (Bad Girls)"
//        _binding?.includeOrgProfileCard?.cardTxtOne?.text = "Region 3 Premier League"
//        _binding?.includeOrgProfileCard?.cardTxtTwo?.text = ""
    }
    override fun setupOnClickListeners() {

    }

}
