package io.usys.report.ui.ludi.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.usys.report.databinding.DetailsServiceBinding
import io.usys.report.realm.model.Service
import io.usys.report.ui.fragments.LudiMiddleFragment
import io.usys.report.utils.cast

/**
 * Created by ChazzCoin : October 2022.
 */

class ServiceDetailsFragment : LudiMiddleFragment() {

    private var _binding: DetailsServiceBinding? = null
    private val binding get() = _binding!!
    private var currentService: Service? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DetailsServiceBinding.inflate(inflater, container, false)
        rootView = binding.root
        currentService = realmObjectArg?.cast<Service>()
        setupOnClickListeners()
        setupDisplay()
        return rootView
    }

    private fun setupDisplay() {

    }

    override fun setupOnClickListeners() {

    }

}
