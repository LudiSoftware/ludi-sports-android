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
import io.usys.report.ui.ludi.onBackPressed
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

        onBackPressed {
            goBack()
        }

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

        try {
            TeamMode.parse(this.team?.mode).let {
                (requireActivity() as AppCompatActivity).ludiActionBarTeamMode(it, this.team?.name)
                _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.makeGone()
            }
        } catch (e:Exception) {
            log("Error: ${e.message}")
            _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.makeVisible()
            _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.text = this.team?.name

        }

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

fun String?.safe(default:String="") : String {
    return this ?: default
}
