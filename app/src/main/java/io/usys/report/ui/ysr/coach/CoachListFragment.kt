package io.usys.report.ui.ysr.coach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.realm.RealmObject
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.ui.fragments.toFragmentWithRealmObject
import io.usys.report.ui.fragments.unbundleRealmObject

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : Fragment() {

    private lateinit var rootView : View
    private var _binding: FragmentOrgListBinding? = null
    private val binding get() = _binding!!

    private var itemOnClick: ((View, RealmObject) -> Unit)? = null
    private var realmObjectArg: RealmObject? = null
//    private var coachesList: RealmList<Coach>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrgListBinding.inflate(inflater, container, false)
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
