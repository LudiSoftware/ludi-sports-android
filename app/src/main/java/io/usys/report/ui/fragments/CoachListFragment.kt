package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.databinding.FragmentOrgListBinding
import io.usys.report.firebase.*
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import io.usys.report.ui.loadInRealmList
import io.usys.report.ui.setupCoachList

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
            toFragment(R.id.navigation_profile, obj)
        }
    }




}
