package io.usys.report.ui.ludi.evaluation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.usys.report.R
import io.usys.report.databinding.CreatePlayerEvalBinding
import io.usys.report.databinding.TeamNotesFragmentBinding
import io.usys.report.firebase.fireludi.fireGetTeamNotesInBackground
import io.usys.report.realm.model.Note
import io.usys.report.realm.model.PEval
import io.usys.report.realm.safeAdd
import io.usys.report.ui.fragments.*
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ViewEvaluationsFragment : LudiStringIdsFragment() {

    companion object {
        const val TAB = "Evaluations"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: CreatePlayerEvalBinding? = null
    private val binding get() = _binding!!

    private var evalType: String? = null
    private var evaluations: RealmList<PEval>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = CreatePlayerEvalBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {
        onClickReturnViewRealmObject = { view, realmObject ->
            log("Clicked on player: ${realmObject}")
        }
    }

}
