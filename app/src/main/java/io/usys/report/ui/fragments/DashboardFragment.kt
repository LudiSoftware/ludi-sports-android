package io.usys.report.ui.fragments

import android.os.Bundle
import android.view.*
import io.usys.report.R
import io.usys.report.databinding.FragmentDashboardBinding
import io.usys.report.ui.setupSportList

/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSportsList() {
        _binding?.recyclerSportList?.setupSportList(requireContext(), itemOnClick)
    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
//            PopFragment().show(childFragmentManager, PopFragment.TAG)
//            findNavController().navigate(R.id.action_pop)
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

    }

}

