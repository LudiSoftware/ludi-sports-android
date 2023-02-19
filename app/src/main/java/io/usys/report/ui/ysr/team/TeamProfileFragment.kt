package io.usys.report.ui.ysr.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.usys.report.R
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.databinding.ProfileTeamViewpagerBinding
import io.usys.report.realm.model.Team
import io.usys.report.ui.fragments.YsrMiddleFragment
import io.usys.report.ui.fragments.YsrPagerAdapter
import io.usys.report.ui.tryouts.HomeTryOuts
import io.usys.report.ui.tryouts.TryoutTestFragment
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

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
        team = realmObjectArg as? Team


        setupOnClickListeners()
        setupDisplay()

        return rootView
    }

    private fun setupDisplay() {
        setupHeader()
        _binding?.includeYsrListViewRoster?.root?.setupPlayerList(team!!.id, itemOnClick)
//        _binding?.includeGenericButtonCard?.cardGenericButtonTxtTitle?.text = "Services"
    }

    private fun setupHeader() {
//        _binding?.includeOrgProfileCard?.cardTxtTitle?.text = "U17 Lady Angels (Bad Girls)"
//        _binding?.includeOrgProfileCard?.cardTxtOne?.text = "Region 3 Premier League"
//        _binding?.includeOrgProfileCard?.cardTxtTwo?.text = ""
    }
    override fun setupOnClickListeners() {
        itemOnClick = { _,obj ->
            log(obj)
        }

    }

}
