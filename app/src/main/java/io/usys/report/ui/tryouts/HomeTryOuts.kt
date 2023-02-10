package io.usys.report.ui.tryouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.realm.RealmObject
import io.usys.report.databinding.ProfileTeamBinding
import io.usys.report.ui.onClickReturnViewRealmObject


/**
 * Created by ChazzCoin : October 2022.
 */

class HomeTryOuts : Fragment() {

    lateinit var rootView : View
    private var _binding: ProfileTeamBinding? = null
    private val binding get() = _binding!!

    var itemOnClickSportList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()
    var itemOnClickServiceList: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileTeamBinding.inflate(inflater, container, false)
        rootView = binding.root
//        setupOnClickListeners()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun setupSportsList() {
//        _binding?.dashboardRecyclerServiceList?.setupServiceList(requireContext(), itemOnClickServiceList)
//        _binding?.recyclerSportList?.setupSportList(requireContext(), itemOnClickSportList)
//    }
//
//    override fun setupOnClickListeners() {
//        itemOnClickSportList = { _, obj ->
//            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
//        }
//
//        itemOnClickServiceList = { _, obj ->
//            toFragment(R.id.navigation_service_details, bundleRealmObject(obj))
//        }
//
//    }

}

