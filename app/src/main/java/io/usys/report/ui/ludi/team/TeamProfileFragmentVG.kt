package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import io.usys.report.databinding.TeamVgFragmentBinding
import io.usys.report.providers.TeamMode
import io.usys.report.providers.syncTeamDataFromFirebase
import io.usys.report.realm.findTeamById
import io.usys.report.realm.model.Team
import io.usys.report.ui.fragments.*
import io.usys.report.ui.views.*
import io.usys.report.ui.views.viewGroup.ludiTeamVGFragments
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragmentVG : LudiTeamFragment() {

    private var linearLayout: LudiLinearLayout? = null
    private var _binding: TeamVgFragmentBinding? = null
    private val binding get() = _binding!!

    var isOpen = false
    private var team: Team? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TeamVgFragmentBinding.inflate(inflater, container, false)
        linearLayout = _binding?.profileTeamRosterRootLinearLayout
        rootView = binding.root
        return rootView
    }

    override fun onStart() {
        super.onStart()
        hideLudiNavView()
        showLudiActionBar()

        //Basic Setup
        teamId?.let {
            // 1. Get Team Profile from Firebase
            realmInstance?.syncTeamDataFromFirebase(it)
        }
        // Setup Team Menu
        teamId?.let {
            menu = TeamMenuPopupProvider(this, it)
            requireActivity().addMenuProvider(menu ?: return@let)
        }
        //Setup Functions
        setupCallBacks()
        setupOnClickListeners()
        setupTeamViewPager()
        setupHeader(refresh = true)
    }

    private fun setupTryoutMode() {
        (requireActivity() as AppCompatActivity).ludiActionBarTeamMode(TeamMode.TRYOUT)
    }

    private fun setupInSeasonMode() {
        (requireActivity() as AppCompatActivity).ludiActionBarTeamMode(TeamMode.IN_SEASON)
    }

    private fun setupTeamViewPager() {
        linearLayout.addLudiViewGroup(this, ludiTeamVGFragments(), teamId, null)
    }

    private fun setupCallBacks() {
        realmTeamCallBack = {
            rosterId = it.rosterId
            setupHeader(it)
        }
    }

    private fun setupHeader(team:Team?=null, refresh:Boolean=false) {

        if (refresh) { realmInstance?.findTeamById(teamId)?.let { this.team = it } }
        else { this.team = team }

        TeamMode.parse(this.team?.mode!!)?.let {
            (requireActivity() as AppCompatActivity).ludiActionBarTeamMode(it)
        }

        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.text = this.team?.name
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtCoachesName?.text = this.team?.headCoachName
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtAgeGroup?.text = "${this.team?.year} - ${this.team?.ageGroup}"
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtOne?.text = this.team?.gender
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTwo?.text = this.team?.mode
        this.team?.imgUrl?.let {
            _binding?.includeTeamProfileCard?.cardTeamMediumImgMainIcon?.loadUriIntoImgView(it)
        }
    }

    override fun setupOnClickListeners() {
        log("Setting up onClickListeners")
    }

}

