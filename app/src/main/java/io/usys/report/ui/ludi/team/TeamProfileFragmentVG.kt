package io.usys.report.ui.ludi.team

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.firebase.fireGetRosterInBackground
import io.usys.report.firebase.fireGetTeamProfileInBackground
import io.usys.report.firebase.fireGetTryOutProfileIntoRealm
import io.usys.report.realm.findRosterById
import io.usys.report.realm.findTeamById
import io.usys.report.realm.model.Team
import io.usys.report.ui.fragments.*
import io.usys.report.ui.views.LudiViewGroup
import io.usys.report.ui.views.ludiNotesAndEvalsVGFragments
import io.usys.report.ui.views.ludiTeamVGFragments
import io.usys.report.utils.*
import io.usys.report.utils.views.loadUriIntoImgView

/**
 * Created by ChazzCoin : October 2022.
 */

class TeamProfileFragmentVG : LudiTeamFragment() {

    private var linearLayout: LinearLayout? = null
    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
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
            realmInstance?.fireGetTeamProfileInBackground(it)
            val team = realmInstance?.findTeamById(it)
            rosterId = team?.rosterId
            rosterId?.let { rosterId ->
                val roster = realmInstance?.findRosterById(rosterId)
                if (roster != null) {
                    this.roster = roster
                } else {
                    fireGetRosterInBackground(rosterId)
                }
            }
            if (_MODE == YsrMode.TRYOUTS) {
                fireGetTryOutProfileIntoRealm(it)
            }
        }
        teamId?.let {
            menu = TeamMenuProvider(this, it)
            requireActivity().addMenuProvider(menu ?: return@let)
        }
        //Setup Functions
        setupCallBacks()
        setupOnClickListeners()
        setupTeamViewPager()
    }

    private fun setupTeamViewPager() {
        linearLayout?.let {
            val lvg = LudiViewGroup(this, it, teamId)
//            lvg.setupLudiTabs(ludiTeamVGFragments())
            lvg.setupLudiTabs(ludiNotesAndEvalsVGFragments())
        }
    }

    private fun setupCallBacks() {
        realmTeamCallBack = {
            setupHeader(it)
        }
    }

    private fun setupHeader(team: Team) {
        tryCatch {
            requireActivity().ludiActionBar()?.title = team.name
        }
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTitle?.text = team.name
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtOne?.text = team.headCoachName
        _binding?.includeTeamProfileCard?.cardTeamMediumTxtTwo?.text = team.ageGroup + team.year
        team.imgUrl?.let {
            _binding?.includeTeamProfileCard?.cardTeamMediumImgMainIcon?.loadUriIntoImgView(it)
        }
    }

    override fun setupOnClickListeners() {
        log("Setting up onClickListeners")
    }

}

