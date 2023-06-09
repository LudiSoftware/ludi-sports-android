package io.usys.report.ui.ludi.coach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.realm.RealmObject
import io.usys.report.databinding.CoachesListFragmentBinding
import io.usys.report.ui.views.navController.toFragmentWithRealmObject
import io.usys.report.ui.views.navController.unbundleRealmObject

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : Fragment() {

    private lateinit var rootView : View
    private var _binding: CoachesListFragmentBinding? = null
    private val binding get() = _binding!!

    private var itemOnClick: ((View, RealmObject) -> Unit)? = null
    private var realmObjectArg: RealmObject? = null
//    private var coachesList: RealmList<Coach>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CoachesListFragmentBinding.inflate(inflater, container, false)
        rootView = binding.root

        realmObjectArg = unbundleRealmObject()
        setupOnClickListeners()
        setupDisplay()

        return rootView
    }

    private fun setupDisplay() {
        _binding?.recyclerList?.setupCoachList(requireContext(), realmObjectArg, itemOnClick)
    }

    private fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragmentWithRealmObject(R.id.navigation_profile_coach, obj)
        }
    }




}
